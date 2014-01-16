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

package org.mongodb.operation

import org.mongodb.Document
import org.mongodb.FunctionalSpecification
import org.mongodb.MongoCollection
import org.mongodb.test.Worker
import org.mongodb.test.WorkerCodec

import static org.mongodb.Fixture.bufferProvider
import static org.mongodb.Fixture.session

class FindAndUpdateOperationSpecification extends FunctionalSpecification {
    private MongoCollection<Worker> workerCollection

    def setup() {
        //setup with a collection designed to store Workers not Documents
        workerCollection = database.getCollection(getCollectionName(), new WorkerCodec())
    }

    def 'should be able to specify a custom encoder and have the found value return in that type'() {
        given:
        Worker pete = new Worker('Pete', 'handyman', new Date(), 3)
        Worker sam = new Worker('Sam', 'plumber', new Date(), 7)

        workerCollection.insert(pete);
        workerCollection.insert(sam);

        when:
        FindAndUpdate findAndUpdate = new FindAndUpdate<Worker>().where(new Document('name', 'Pete'))
                                                                 .updateWith(new Document('$inc', new Document('numberOfJobs', 1)))
                                                                 .returnNew(true);

        FindAndUpdateOperation<Worker> operation = new FindAndUpdateOperation<Worker>(workerCollection.namespace, findAndUpdate,
                                                                                      new WorkerCodec(), bufferProvider, session,
                                                                                      false)
        Worker returnedValue = operation.execute()

        then:
        Worker updatedPete = new Worker(pete.id, pete.name, pete.jobTitle, pete.dateStarted, 4)
        returnedValue == updatedPete
    }

}
