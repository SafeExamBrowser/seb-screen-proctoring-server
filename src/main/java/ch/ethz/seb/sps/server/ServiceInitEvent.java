/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import org.springframework.context.ApplicationEvent;


public class ServiceInitEvent extends ApplicationEvent {

    private static final long serialVersionUID = -3608628289559324471L;

    public final ServiceInit serviceInit;

    public ServiceInitEvent(final ServiceInit serviceInit) {
        super(serviceInit);
        this.serviceInit = serviceInit;
    }
}
