/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.user.ClientAccess;
import ch.ethz.seb.sps.utils.Result;

public interface ClientAccessDAO extends ActivatableEntityDAO<ClientAccess, ClientAccess> {

    /** Get the encoded client password that is used by a SEB client to authenticate.
     * 
     * @param clientId The client authentication entity id.
     * @param checkActive Only gets the password if the client authentication entity is active.
     * @return Result refer to CharSequence with the encoded client password. */
    Result<CharSequence> getEncodedClientPWD(String clientId, boolean checkActive);

}
