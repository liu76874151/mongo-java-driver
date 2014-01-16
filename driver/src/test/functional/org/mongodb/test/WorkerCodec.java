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

package org.mongodb.test;

import org.bson.BSONReader;
import org.bson.BSONWriter;
import org.bson.types.ObjectId;
import org.mongodb.CollectibleCodec;

import java.util.Date;

public final class WorkerCodec implements CollectibleCodec<Worker> {
    @Override
    public Object getId(final Worker worker) {
        return worker.getId();
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final Worker value) {
        bsonWriter.writeStartDocument();
        bsonWriter.writeObjectId("_id", value.getId());
        bsonWriter.writeString("name", value.getName());
        bsonWriter.writeString("jobTitle", value.getJobTitle());
        bsonWriter.writeDateTime("dateStarted", value.getDateStarted().getTime());
        bsonWriter.writeInt32("numberOfJobs", value.getNumberOfJobs());
        bsonWriter.writeEndDocument();
    }

    @Override
    public Worker decode(final BSONReader reader) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("_id");
        String name = reader.readString("name");
        String jobTitle = reader.readString("jobTitle");
        Date dateStarted = new Date(reader.readDateTime("dateStarted"));
        int numberOfJobs = reader.readInt32("numberOfJobs");
        reader.readEndDocument();
        return new Worker(id, name, jobTitle, dateStarted, numberOfJobs);
    }

    @Override
    public Class<Worker> getEncoderClass() {
        return Worker.class;
    }
}
