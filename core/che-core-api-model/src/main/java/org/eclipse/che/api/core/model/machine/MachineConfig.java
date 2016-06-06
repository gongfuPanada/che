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
package org.eclipse.che.api.core.model.machine;

import org.eclipse.che.commons.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author gazarenkov
 */
public interface MachineConfig {

    /**
     * Display name.
     */
    String getName();

    /**
     * From where to create this Machine (Recipe/Snapshot).
     */
    MachineSource getSource();

    /**
     * Is workspace bound to machine or not.
     */
    boolean isDev();

    /**
     * Machine type (i.e. "docker").
     */
    String getType();

    /**
     * Machine limits such as RAM size.
     */
    @Nullable
    Limits getLimits();

    /**
     * List of machines from env current machine is depends on.
     * <p/>
     * Machine from depends on list will be available to current machine by host equal to machine's name.
     * Machines from depends on list will be launched before machine that is dependent.
     */
    List<String> getDependsOn();

    /**
     * Get configuration of servers inside of machine.
     *
     * <p>Key is port/transport protocol, e.g. 8080/tcp or 100100/udp
     */
    List<? extends ServerConf> getServers();

    /**
     * Get predefined environment variables of machine.
     */
    Map<String, String> getEnvVariables();

    // TODO how to handle shell and exec forms of command and entry point?


    List<String> getEntrypoint();

    List<String> getCommand();

    // todo process links with depends on
    // todo is it possible to combine links and depends on
    List<String> getMachineLinks();

    // todo declare servers using ports, deprecate servers entry?
    // todo will we support not random ports?
    List<String> getPorts();

    //todo check how to prevent insertion labels that can broke docker/swarm or make them unsecure
    Map<String, String> getLabels();

    List<String> getExpose();

    // todo do not use in hosted version
    String getContainerName();
}
