/*
 * Copyright (c) 2015-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

import {CheWorkspaceAgent} from './che-workspace-agent';

/**
 * This class is handling the workspace retrieval
 * It sets to the array workspaces the current workspaces which are not temporary
 * @author Florent Benoit
 */
export class CheWorkspace {

  /**
   * Default constructor that is using resource
   * @ngInject for Dependency injection
   */
  constructor ($resource, $q, cheUser, cheWebsocket, lodash) {
    // keep resource
    this.$resource = $resource;

    this.$q = $q;
    this.lodash = lodash;

    this.cheUser = cheUser;
    this.cheWebsocket = cheWebsocket;

    this.lodash = lodash;

    // current list of workspaces
    this.workspaces = [];

    // per Id
    this.workspacesById = new Map();

    //Workspace agents per workspace id:
    this.workspaceAgents = new Map();

    // listeners if workspaces are changed/updated
    this.listeners = [];

    // list of websocket bus per workspace
    this.websocketBusByWorkspaceId = new Map();
    this.statusDefers = {};

    // remote call
    this.remoteWorkspaceAPI = this.$resource('/api/workspace', {}, {
        getDetails: {method: 'GET', url: '/api/workspace/:workspaceId'},
        create: {method: 'POST', url: '/api/workspace?account=:accountId'},
        deleteWorkspace: {method: 'DELETE', url: '/api/workspace/:workspaceId'},
        updateWorkspace: {method: 'PUT', url : '/api/workspace/:workspaceId'},
        addProject: {method: 'POST', url : '/api/workspace/:workspaceId/project'},
        deleteProject: {method: 'DELETE', url : '/api/workspace/:workspaceId/project/:path'},
        stopWorkspace: {method: 'DELETE', url : '/api/workspace/:workspaceId/runtime'},
        startWorkspace: {method: 'POST', url : '/api/workspace/:workspaceId/runtime?environment=:envName'},
        addCommand: {method: 'POST', url: '/api/workspace/:workspaceId/command'}
      }
    );
  }

  getWorkspaceAgent(workspaceId) {
    if (this.workspaceAgents.has(workspaceId)) {
      return this.workspaceAgents.get(workspaceId);
    }

    let runtimeConfig = this.getWorkspaceById(workspaceId).runtime;
    if (runtimeConfig) {
      let wsAgentLink = this.lodash.find(runtimeConfig.links, (link) => {
        return link.rel === 'wsagent';
      });

      if (!wsAgentLink) {
        return null;
      }

      let workspaceAgentData = {path : wsAgentLink.href};
      let wsagent = new CheWorkspaceAgent(this.$resource, this.$q, this.cheWebsocket, workspaceAgentData);
      this.workspaceAgents.set(workspaceId, wsagent);
      return wsagent;
    }
    return null;
  }

/**
 * Gets all workspace agents of this remote
 * @returns {Map}
 */
  getWorkspaceAgents() {
    return this.workspaceAgents;
  }

  /**
   * Add a listener that need to have the onChangeWorkspaces(workspaces: Array) method
   * @param listener a changing listener
   */
  addListener(listener) {
    this.listeners.push(listener);
  }


  /**
   * Gets the workspaces of this remote
   * @returns {Array}
   */
  getWorkspaces() {
    return this.workspaces;
  }

  /**
   * Gets the workspaces per id
   * @returns {Map}
   */
  getWorkspacesById() {
    return this.workspacesById;
  }

  /**
   * Gets the workspace by id
   * @param workspace id
   * @returns {workspace}
   */
  getWorkspaceById(id) {
    return this.workspacesById.get(id);
  }

  /**
   * Ask for loading the workspaces in asynchronous way
   * If there are no changes, it's not updated
   */
  fetchWorkspaces() {
    let query = this.remoteWorkspaceAPI.query();
    let promise = query.$promise;
    let updatedPromise = promise.then((data) => {
      var remoteWorkspaces = [];
      this.workspaces.length = 0;
      //TODO It's a fix used not to loose account ID of the workspace.
      //Can be removed, when API will return accountId in the list of user workspaces response:
      var copyWorkspaceById = new Map();
      angular.copy(this.workspacesById, copyWorkspaceById);

      this.workspacesById.clear();
      // add workspace if not temporary
      data.forEach((workspace) => {

        if (!workspace.config.temporary) {
          remoteWorkspaces.push(workspace);
          this.workspaces.push(workspace);
          this.workspacesById.set(workspace.id, workspace);
        }
        this.startUpdateWorkspaceStatus(workspace.id);
      });
      return this.workspaces;
    });

    let callbackPromises = updatedPromise.then((data) => {
      var promises = [];
      promises.push(updatedPromise);

      this.listeners.forEach((listener) => {
        let promise = listener.onChangeWorkspaces(data);
        promises.push(promise);
      });
      return this.$q.all(promises);
    });

    return callbackPromises;
  }

  fetchWorkspaceDetails(workspaceId) {
    var defer = this.$q.defer();

    let promise = this.remoteWorkspaceAPI.getDetails({workspaceId : workspaceId}).$promise;
    promise.then((data) => {
      this.workspacesById.set(workspaceId, data);
      this.startUpdateWorkspaceStatus(workspaceId);
      defer.resolve();
    }, (error) => {
      if (error.status !== 304) {
        defer.reject(error);
      } else {
        defer.resolve();
      }
    });

    return defer.promise;
  }

  /**
   * Adds a project on the workspace
   * @param workspaceId the workspace ID required to add a project
   * @param project the project JSON entry to add
   * @returns {*}
   */
  addProject(workspaceId, project) {
    let promise = this.remoteWorkspaceAPI.addProject({workspaceId : workspaceId}, project).$promise;
    return promise;
  }

  /**
   * Deletes a project of the workspace by it's path
   * @param workspaceId the workspace ID required to delete a project
   * @param path path to project to be deleted
   * @returns {*}
   */
  deleteProject(workspaceId, path) {
    let promise = this.remoteWorkspaceAPI.deleteProject({workspaceId : workspaceId, path: path}).$promise;
    return promise;
  }

  /**
   * Returns workspace config
   * @param config
   * @param workspaceName
   * @param recipeUrl
   * @param ram
   * @returns {*}
   */
  formWorkspaceConfig(config, workspaceName, recipeUrl, ram) {
    config = config || {};
    config.name = workspaceName;
    config.projects = [];
    config.defaultEnv = workspaceName;
    config.description = null;
    ram = ram || 2048;
    config.environments = [{
      'name': workspaceName,
      'recipe': null,
      'machineConfigs': [{
        'name': 'ws-machine',
        'limits': {'ram': ram},
        'type': 'docker',
        'source': {'location': recipeUrl, 'type': 'dockerfile'},
        'dev': true
      }]
    }];

    return config;
  }

  createWorkspace(accountId, workspaceName, recipeUrl, ram, attributes) {
    let data = this.formWorkspaceConfig(workspaceName,recipeUrl, ram);

    let attrs = this.lodash.map(this.lodash.pairs(attributes || {}), (item) => { return item[0] + ':' + item[1]});
    let promise = this.remoteWorkspaceAPI.create({accountId : accountId, attribute: attrs}, data).$promise;
    return promise;
  }

  createWorkspaceFromConfig(accountId, workspaceConfig, attributes) {
    let attrs = this.lodash.map(this.lodash.pairs(attributes || {}), (item) => { return item[0] + ':' + item[1]});
    return this.remoteWorkspaceAPI.create({accountId : accountId, attribute: attrs}, workspaceConfig).$promise;
  }

  /**
   * Add a command into the workspace
   * @param workspaceId the id of the workspace on which we want to add the command
   * @param command the command object that contains attribute like name, type, etc.
   * @returns promise
   */
  addCommand(workspaceId, command) {
    return this.remoteWorkspaceAPI.addCommand({workspaceId : workspaceId}, command).$promise;
  }

  /**
   * Starts the given workspace by specifying the ID and the environment name
   * @param workspaceId the workspace ID
   * @param envName the name of the environment
   * @returns {*} promise
   */
  startWorkspace(workspaceId, envName) {
    let promise = this.remoteWorkspaceAPI.startWorkspace({workspaceId: workspaceId, envName : envName}, {}).$promise;
    return promise;
  }


  stopWorkspace(workspaceId) {
    let promise = this.remoteWorkspaceAPI.stopWorkspace({workspaceId: workspaceId}, {}).$promise;
    return promise;
  }

  /**
   * Performs workspace config update by the given workspaceId and new data.
   * @param workspaceId the workspace ID
   * @param data the new workspace details
   * @returns {*|promise|n|N}
   */
  updateWorkspace(workspaceId, data) {
    let promise = this.remoteWorkspaceAPI.updateWorkspace({workspaceId : workspaceId}, data).$promise;
    promise.then(() => {this.fetchWorkspaceDetails(workspaceId);});
    return promise;
  }

  /**
   * Performs workspace deleting by the given workspaceId.
   * @param workspaceId the workspace ID
   * @returns {*|promise|n|N}
   */
  deleteWorkspaceConfig(workspaceId) {
    var defer = this.$q.defer();
    let promise = this.remoteWorkspaceAPI.deleteWorkspace({workspaceId : workspaceId}).$promise;
    promise.then(() => {
      this.listeners.forEach((listener) => {
        listener.onDeleteWorkspace(workspaceId);
      });
      defer.resolve();
    }, (error) => {
        defer.reject(error);
      });

    return defer.promise;
  }

  /**
   * Gets the map of projects by workspace id.
   * @returns
   */
  getWorkspaceProjects() {
    let workspaceProjects = {};
    this.workspacesById.forEach((workspace) => {
      let projects = workspace.config.projects;
      projects.forEach((project) => {
        project.workspaceId = workspace.id;
        project.workspaceName = workspace.config.name;
      });

      workspaceProjects[workspace.id] = projects;
    });

    return workspaceProjects;
  }

  getAllProjects() {
    let projects = this.lodash.pluck(this.workspaces, 'config.projects');
    return [].concat.apply([], projects);
  }

  /**
   * Gets websocket for a given workspace. It needs to have fetched first the runtime configuration of the workspace
   * @param workspaceId the id of the workspace
   * @returns {string}
   */
  getWebsocketUrl(workspaceId) {
    let workspace = this.workspacesById.get(workspaceId);
    if (!workspace || !workspace.runtime || !workspace.runtime.devMachine) {
      return '';
    }
    let websocketLink = this.lodash.find(workspace.runtime.devMachine.links, l => l.rel === "wsagent.websocket");
    return websocketLink ? websocketLink.href : '';
  }

  getIdeUrl(workspaceName) {
    return '/ide/' + workspaceName;
  }

  /**
   * Creates deferred object which will be resolved
   * when workspace change it's status to given
   * @param workspaceId
   * @param status needed to resolve deferred object
     */
  fetchStatusChange(workspaceId, status) {
    let defer = this.$q.defer();
    if (status === this.getWorkspaceById(workspaceId).status) {
      defer.resolve();
    } else {
      if (!this.statusDefers[workspaceId]) {
        this.statusDefers[workspaceId] = {};
      }
      if (!this.statusDefers[workspaceId][status]) {
        this.statusDefers[workspaceId][status] = [];
      }
      this.statusDefers[workspaceId][status].push(defer);
    }
    return defer.promise;
  }

  /**
   * Add subscribe to websocket channel for specified workspaceId
   * to handle workspace's status changes.
   * @param workspaceId
     */
  startUpdateWorkspaceStatus(workspaceId) {
    if (!this.websocketBusByWorkspaceId.has(workspaceId)) {
      let bus = this.cheWebsocket.getBus(workspaceId);
      this.websocketBusByWorkspaceId.set(workspaceId, bus);

      bus.subscribe('workspace:' + workspaceId, (message) => {
        this.getWorkspaceById(workspaceId).status = message.eventType;

        if (!this.statusDefers[workspaceId] || !this.statusDefers[workspaceId][message.eventType]) {
          return;
        }

        this.statusDefers[workspaceId][message.eventType].forEach((defer) => {defer.resolve(message)});
        this.statusDefers[workspaceId][message.eventType].length = 0;
      });
    }
  }
}
