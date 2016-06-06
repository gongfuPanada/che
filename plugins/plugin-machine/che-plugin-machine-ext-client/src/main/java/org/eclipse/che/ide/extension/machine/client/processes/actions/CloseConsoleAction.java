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
package org.eclipse.che.ide.extension.machine.client.processes.actions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import static java.util.Collections.singletonList;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.extension.machine.client.processes.ConsolesPanelPresenter;
import static org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

import javax.validation.constraints.NotNull;

/**
 * Stop selected process and close the console action.
 *
 * @author Vitaliy Guliy
 */
@Singleton
public class CloseConsoleAction extends AbstractPerspectiveAction {

    @Inject
    public CloseConsoleAction(ConsolesPanelPresenter consolesPanelPresenter) {
        super(singletonList(PROJECT_PERSPECTIVE_ID), "Close", "Close Description", null, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {

    }

}
