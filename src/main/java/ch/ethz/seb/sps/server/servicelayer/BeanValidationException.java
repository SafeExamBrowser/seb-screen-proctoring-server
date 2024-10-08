/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class BeanValidationException extends RuntimeException {

    private static final long serialVersionUID = 3631243977662300454L;

    private final BindingResult bindingResult;

    public BeanValidationException(final BindingResult bindingResult) {
        super();
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return this.bindingResult;
    }

    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder("Validation failed for ");
        if (this.bindingResult.getErrorCount() > 1) {
            sb.append(" with ").append(this.bindingResult.getErrorCount()).append(" errors");
        }
        sb.append(": ");
        for (final ObjectError error : this.bindingResult.getAllErrors()) {
            sb.append("[").append(error).append("] ");
        }
        return sb.toString();
    }

}
