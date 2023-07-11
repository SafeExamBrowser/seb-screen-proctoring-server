/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -8678699287639450955L;

    public final String request;

    public BadRequestException(final String request, final String message, final Throwable cause) {
        super(message, cause);
        this.request = request;
    }

    public BadRequestException(final String request, final String message) {
        super(message);
        this.request = request;
    }

}
