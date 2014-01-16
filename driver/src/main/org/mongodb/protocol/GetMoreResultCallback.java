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

package org.mongodb.protocol;

import org.mongodb.Decoder;
import org.mongodb.MongoCursorNotFoundException;
import org.mongodb.MongoException;
import org.mongodb.MongoInternalException;
import org.mongodb.ServerCursor;
import org.mongodb.connection.Connection;
import org.mongodb.connection.ResponseBuffers;
import org.mongodb.connection.SingleResultCallback;
import org.mongodb.protocol.message.ReplyMessage;

class GetMoreResultCallback<T> extends ResponseCallback {
    private final SingleResultCallback<QueryResult<T>> callback;
    private final Decoder<T> decoder;
    private final long cursorId;

    public GetMoreResultCallback(final SingleResultCallback<QueryResult<T>> callback, final Decoder<T> decoder,
                                 final long cursorId, final long requestId, final Connection connection, final boolean closeConnection) {
        super(requestId, connection, closeConnection);
        this.callback = callback;
        this.decoder = decoder;
        this.cursorId = cursorId;
    }

    @Override
    protected boolean callCallback(final ResponseBuffers responseBuffers, final MongoException e) {
        QueryResult<T> result = null;
        MongoException exceptionResult = null;
        try {
            if (e != null) {
                throw e;
            } else if (responseBuffers.getReplyHeader().isCursorNotFound()) {
                throw new MongoCursorNotFoundException(new ServerCursor(cursorId, getConnection().getServerAddress()));
            } else {
                result = new QueryResult<T>(new ReplyMessage<T>(responseBuffers, decoder, getRequestId()),
                                            getConnection().getServerAddress());
            }
        } catch (MongoException me) {
            exceptionResult = me;
        } catch (Throwable t) {
            exceptionResult = new MongoInternalException("Internal exception", t);
        } finally {
            if (responseBuffers != null) {
                responseBuffers.close();
            }
        }
        callback.onResult(result, exceptionResult);
        return true;
    }
}
