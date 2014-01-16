/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.connection;

import org.mongodb.CommandResult;
import org.mongodb.Document;
import org.mongodb.MongoCommandFailureException;
import org.mongodb.MongoCredential;
import org.mongodb.codecs.DocumentCodec;

import static org.mongodb.connection.CommandHelper.executeCommand;
import static org.mongodb.connection.NativeAuthenticationHelper.getAuthCommand;

class NativeAuthenticator extends Authenticator {
    public NativeAuthenticator(final MongoCredential credential, final InternalConnection internalConnection,
                               final BufferProvider bufferProvider) {
        super(credential, internalConnection, bufferProvider);
    }

    @Override
    public void authenticate() {
        try {
            CommandResult nonceResponse = executeCommand(getCredential().getSource(),
                                                         NativeAuthenticationHelper.getNonceCommand(), new DocumentCodec(),
                                                         getInternalConnection(), getBufferProvider());

            Document authCommand = getAuthCommand(getCredential().getUserName(),
                                                  getCredential().getPassword(),
                                                  nonceResponse.getResponse().getString("nonce"));
            executeCommand(getCredential().getSource(), authCommand, new DocumentCodec(), getInternalConnection(), getBufferProvider());
        } catch (MongoCommandFailureException e) {
            throw new MongoSecurityException(getCredential(), "Exception authenticating", e);
        }
    }
}
