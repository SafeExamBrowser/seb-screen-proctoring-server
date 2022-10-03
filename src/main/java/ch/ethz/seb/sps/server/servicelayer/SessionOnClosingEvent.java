/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import org.springframework.context.ApplicationEvent;

public class SessionOnClosingEvent extends ApplicationEvent {

    private static final long serialVersionUID = 4074456635820364953L;

    public final String sessionUUID;

    public SessionOnClosingEvent(final String sessionUUID) {
        super(sessionUUID);
        this.sessionUUID = sessionUUID;
    }

}
