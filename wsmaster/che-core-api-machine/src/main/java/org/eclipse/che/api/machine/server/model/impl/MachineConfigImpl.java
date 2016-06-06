/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.machine.server.model.impl;

import org.eclipse.che.api.core.model.machine.Limits;
import org.eclipse.che.api.core.model.machine.MachineConfig;
import org.eclipse.che.api.core.model.machine.MachineSource;
import org.eclipse.che.api.core.model.machine.ServerConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Data object for {@link MachineConfig}.
 *
 * @author Eugene Voevodin
 */
public class MachineConfigImpl implements MachineConfig {

    public static MachineConfigImplBuilder builder() {
        return new MachineConfigImplBuilder();
    }

    private boolean              isDev;
    private String               name;
    private String               type;
    private MachineSourceImpl    source;
    private LimitsImpl           limits;
    private List<ServerConfImpl> servers;
    private Map<String, String>  envVariables;
    private List<String>         dependsOn;
    private List<String>         ports;
    private List<String>         expose;
    private List<String>         machineLinks;
    private List<String>         entrypoint;
    private List<String>         command;
    private Map<String, String>  labels;
    private String               containerName;

    public MachineConfigImpl() {}

    public MachineConfigImpl(MachineConfig machineCfg) {
        setDev(machineCfg.isDev());
        setName(machineCfg.getName());
        setType(machineCfg.getType());
        setSource(machineCfg.getSource());
        setLimits(machineCfg.getLimits());
        setServers(machineCfg.getServers());
        setEnvVariables(machineCfg.getEnvVariables());
        setDependsOn(machineCfg.getDependsOn());
        setCommand(machineCfg.getCommand());
        setContainerName(machineCfg.getContainerName());
        setEntrypoint(machineCfg.getEntrypoint());
        setExpose(machineCfg.getExpose());
        setLabels(machineCfg.getLabels());
        setMachineLinks(machineCfg.getMachineLinks());
        setPorts(machineCfg.getPorts());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public MachineSourceImpl getSource() {
        return source;
    }

    public void setSource(MachineSource source) {
        if (source != null) {
            this.source = new MachineSourceImpl(source);
        }
    }

    @Override
    public boolean isDev() {
        return isDev;
    }

    public void setDev(boolean dev) {
        isDev = dev;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public LimitsImpl getLimits() {
        return limits;
    }

    public void setLimits(Limits limits) {
        this.limits = new LimitsImpl(limits);
    }

    @Override
    public List<ServerConfImpl> getServers() {
        if (servers == null) {
            servers = new ArrayList<>();
        }
        return servers;
    }

    public void setServers(List<? extends ServerConf> servers) {
        if (servers != null) {
            this.servers = servers.stream()
                                  .map(ServerConfImpl::new)
                                  .collect(Collectors.toList());
        }
    }

    @Override
    public Map<String, String> getEnvVariables() {
        if (envVariables == null) {
            envVariables = new HashMap<>();
        }
        return envVariables;
    }

    public void setEnvVariables(Map<String, String> envVariables) {
        this.envVariables = envVariables;
    }

    @Override
    public List<String> getDependsOn() {
        if (dependsOn == null) {
            dependsOn = new ArrayList<>();
        }
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Override
    public List<String> getEntrypoint() {
        if (entrypoint == null) {
            entrypoint = new ArrayList<>();
        }
        return entrypoint;
    }

    public void setEntrypoint(List<String> entrypoint) {
        this.entrypoint = entrypoint;
    }

    @Override
    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @Override
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Override
    public List<String> getPorts() {
        if (ports == null) {
            ports = new ArrayList<>();
        }
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    @Override
    public List<String> getCommand() {
        if (command == null) {
            command = new ArrayList<>();
        }
        return command;
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }

    @Override
    public List<String> getExpose() {
        if (expose == null) {
            expose = new ArrayList<>();
        }
        return expose;
    }

    public void setExpose(List<String> expose) {
        this.expose = expose;
    }

    @Override
    public List<String> getMachineLinks() {
        if (machineLinks == null) {
            machineLinks = new ArrayList<>();
        }
        return machineLinks;
    }

    public void setMachineLinks(List<String> machineLinks) {
        this.machineLinks = machineLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineConfigImpl)) return false;
        MachineConfigImpl that = (MachineConfigImpl)o;
        return isDev == that.isDev &&
               Objects.equals(name, that.name) &&
               Objects.equals(type, that.type) &&
               Objects.equals(source, that.source) &&
               Objects.equals(limits, that.limits) &&
               Objects.equals(servers, that.servers) &&
               Objects.equals(envVariables, that.envVariables) &&
               Objects.equals(dependsOn, that.dependsOn) &&
               Objects.equals(ports, that.ports) &&
               Objects.equals(expose, that.expose) &&
               Objects.equals(machineLinks, that.machineLinks) &&
               Objects.equals(entrypoint, that.entrypoint) &&
               Objects.equals(command, that.command) &&
               Objects.equals(labels, that.labels) &&
               Objects.equals(containerName, that.containerName);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(isDev, name, type, source, limits, servers, envVariables, dependsOn, ports, expose, machineLinks, entrypoint, command,
                      labels, containerName);
    }

    @Override
    public String toString() {
        return "MachineConfigImpl{" +
               "isDev=" + isDev +
               ", name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", source=" + source +
               ", limits=" + limits +
               ", servers=" + servers +
               ", envVariables=" + envVariables +
               ", dependsOn=" + dependsOn +
               ", ports=" + ports +
               ", expose=" + expose +
               ", machineLinks=" + machineLinks +
               ", entrypoint=" + entrypoint +
               ", command=" + command +
               ", labels=" + labels +
               ", containerName='" + containerName + '\'' +
               '}';
    }

    /**
     * Helps to build complex {@link MachineConfigImpl machine config impl}.
     *
     * @see MachineConfigImpl#builder()
     */
    public static class MachineConfigImplBuilder {

        private boolean                    isDev;
        private String                     name;
        private String                     type;
        private MachineSource              source;
        private Limits                     limits;
        private List<? extends ServerConf> servers;
        private Map<String, String>        envVariables;
        private List<String>               dependsOn;
        private List<String>               ports;
        private List<String>               expose;
        private List<String>               machineLinks;
        private List<String>               entrypoint;
        private List<String>               command;
        private Map<String, String>        labels;
        private String                     containerName;

        public MachineConfigImpl build() {
            MachineConfigImpl config = new MachineConfigImpl();
            config.setDev(isDev);
            config.setName(name);
            config.setType(type);
            config.setSource(source);
            config.setLimits(limits);
            config.setServers(servers);
            config.setEnvVariables(envVariables);
            config.setDependsOn(dependsOn);
            config.setCommand(command);
            config.setContainerName(containerName);
            config.setEntrypoint(entrypoint);
            config.setExpose(expose);
            config.setLabels(labels);
            config.setMachineLinks(machineLinks);
            config.setPorts(ports);
            return config;
        }

        public MachineConfigImplBuilder fromConfig(MachineConfig machineConfig) {
            isDev = machineConfig.isDev();
            name = machineConfig.getName();
            type = machineConfig.getType();
            source = machineConfig.getSource();
            limits = machineConfig.getLimits();
            servers = machineConfig.getServers();
            envVariables = machineConfig.getEnvVariables();
            dependsOn = machineConfig.getDependsOn();
            return this;
        }

        public MachineConfigImplBuilder setDev(boolean isDev) {
            this.isDev = isDev;
            return this;
        }

        public MachineConfigImplBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MachineConfigImplBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public MachineConfigImplBuilder setSource(MachineSource machineSource) {
            this.source = machineSource;
            return this;
        }

        public MachineConfigImplBuilder setLimits(Limits limits) {
            this.limits = limits;
            return this;
        }

        public MachineConfigImplBuilder setServers(List<? extends ServerConf> servers) {
            this.servers = servers;
            return this;
        }

        public MachineConfigImplBuilder setEnvVariables(Map<String, String> envVariables) {
            this.envVariables = envVariables;
            return this;
        }

        public MachineConfigImplBuilder setDependsOn(List<String> dependsOn) {
            this.dependsOn = dependsOn;
            return this;
        }

        public MachineConfigImplBuilder setPorts(List<String> ports) {
            this.ports = ports;
            return this;
        }

        public MachineConfigImplBuilder setMachineLinks(List<String> machineLinks) {
            this.machineLinks = machineLinks;
            return this;
        }

        public MachineConfigImplBuilder setExpose(List<String> expose) {
            this.expose = expose;
            return this;
        }

        public MachineConfigImplBuilder setLabels(Map<String, String> labels) {
            this.labels = labels;
            return this;
        }

        public MachineConfigImplBuilder setContainerName(String containerName) {
            this.containerName = containerName;
            return this;
        }

        public MachineConfigImplBuilder setCommand(List<String> command) {
            this.command = command;
            return this;
        }

        public MachineConfigImplBuilder setEntrypoint(List<String> entrypoint) {
            this.entrypoint = entrypoint;
            return this;
        }
    }
}
