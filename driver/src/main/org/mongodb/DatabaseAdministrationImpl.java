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

package org.mongodb;

import org.mongodb.codecs.DocumentCodec;
import org.mongodb.operation.CommandOperation;
import org.mongodb.operation.Find;
import org.mongodb.operation.QueryOperation;
import org.mongodb.operation.RenameCollectionOperation;

import java.util.HashSet;
import java.util.Set;

/**
 * Runs the admin commands for a selected database.  This should be accessed from MongoDatabase.  The methods here are not implemented in
 * MongoDatabase in order to keep the API very simple, these should be the methods that are not commonly used by clients of the driver.
 */
class DatabaseAdministrationImpl implements DatabaseAdministration {
    private static final Document DROP_DATABASE = new Document("dropDatabase", 1);
    private static final Find FIND_ALL = new Find().readPreference(ReadPreference.primary());

    private final String databaseName;
    private final Codec<Document> commandCodec = new DocumentCodec();
    private final MongoClientImpl client;

    public DatabaseAdministrationImpl(final String databaseName, final MongoClientImpl client) {
        this.databaseName = databaseName;
        this.client = client;
    }

    @Override
    public void drop() {
        //TODO: should inspect the CommandResult to make sure it went OK
        new CommandOperation(databaseName, DROP_DATABASE, null, commandCodec, commandCodec, client.getCluster().getDescription(),
                             client.getBufferProvider(), client.getSession(), false).execute();
    }

    @Override
    public Set<String> getCollectionNames() {
        MongoNamespace namespacesCollection = new MongoNamespace(databaseName, "system.namespaces");
        MongoCursor<Document> cursor = new QueryOperation<Document>(namespacesCollection, FIND_ALL, commandCodec, commandCodec,
                                                                    client.getBufferProvider(), client.getSession(), false).execute();

        HashSet<String> collections = new HashSet<String>();
        int lengthOfDatabaseName = databaseName.length();
        while (cursor.hasNext()) {
            String collectionName = (String) cursor.next().get("name");
            if (!collectionName.contains("$")) {
                String collectionNameWithoutDatabasePrefix = collectionName.substring(lengthOfDatabaseName + 1);
                collections.add(collectionNameWithoutDatabasePrefix);
            }
        }
        return collections;
    }

    @Override
    public void createCollection(final String collectionName) {
        createCollection(new CreateCollectionOptions(collectionName));
    }

    @Override
    public void createCollection(final CreateCollectionOptions createCollectionOptions) {
        CommandResult commandResult = new CommandOperation(databaseName, createCollectionOptions.asDocument(), null, commandCodec,
                                                           commandCodec, client.getCluster().getDescription(),
                                                           client.getBufferProvider(), client.getSession(), false).execute();
        ErrorHandling.handleErrors(commandResult);
    }

    @Override
    public void renameCollection(final String oldCollectionName, final String newCollectionName) {
        new RenameCollectionOperation(client.getBufferProvider(), client.getSession(), false,
                                      databaseName, oldCollectionName, newCollectionName, false)
            .execute();
    }

    @Override
    public void renameCollection(final String oldCollectionName, final String newCollectionName, final boolean dropTarget) {
        new RenameCollectionOperation(client.getBufferProvider(), client.getSession(), false,
                                      databaseName, oldCollectionName, newCollectionName, dropTarget)
            .execute();
    }

}
