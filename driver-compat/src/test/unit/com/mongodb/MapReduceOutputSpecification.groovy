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

package com.mongodb

import org.mongodb.MapReduceCursor
import org.mongodb.MapReduceStatistics
import spock.lang.Subject

@SuppressWarnings('deprecated')
class MapReduceOutputSpecification extends FunctionalSpecification {
    //example response:
//    CommandResult{
//        address=localhost:27017,
//        response={ 'result' : { 'db':'output-1383912431569888000',
//                                'collection' : 'jmr1_out'
//                              },
//                   'timeMillis' : 2774,
//                   'timing' : { 'mapTime' : 0,
//                                'emitLoop' : 2755,
//                                'reduceTime' : 15,
//                                'mode' : 'mixed',
//                                'total' : 2774 },
//                   'counts' : { 'input' : 3,
//                                'emit' : 6,
//                                'reduce' : 2,
//                                'output' : 4 },
//                   'ok' : 1.0 },
//        elapsedNanoseconds=2777341000}


    def 'should return the name of the collection the results are contained in if it is not inline'() throws Exception {
        given:
        String expectedCollectionName = 'collectionForResults';
        DBCollection outputCollection = database.getCollection(expectedCollectionName)
        DBCursor results = outputCollection.find()

        @Subject
        MapReduceOutput mapReduceOutput = new MapReduceOutput(new BasicDBObject(), results, null, outputCollection, null);

        when:
        String collectionName = mapReduceOutput.getCollectionName();

        then:
        collectionName != null
        collectionName == expectedCollectionName
    }

    def 'should return the name of the datbase the results are contained in if it is not inline'() throws Exception {
        given:
        String expectedDatabaseName = databaseName
        String expectedCollectionName = 'collectionForResults';
        DBCollection outputCollection = database.getCollection(expectedCollectionName)
        DBCursor results = outputCollection.find()

        @Subject
        MapReduceOutput mapReduceOutput = new MapReduceOutput(new BasicDBObject(), results, null, outputCollection, null);

        when:
        String databaseName = mapReduceOutput.getDatabaseName();

        then:
        databaseName != null
        databaseName == expectedDatabaseName
    }

    def 'should return the duration for a map-reduce into a collection'() {
        given:
        int expectedDuration = 2774

        MapReduceStatistics mapReduceStats = Mock();
        mapReduceStats.getDuration() >> expectedDuration

        @Subject
        MapReduceOutput mapReduceOutput = new MapReduceOutput(new BasicDBObject(), null, mapReduceStats, null,
                                                              new org.mongodb.connection.ServerAddress());

        expect:
        mapReduceOutput.getDuration() == expectedDuration;
    }

    def 'should return the duration for an inline map-reduce'() {
        given:
        int expectedDuration = 2774

        MapReduceCursor mongoCursor = Mock();
        mongoCursor.getDuration() >> expectedDuration

        @Subject
        MapReduceOutput mapReduceOutput = new MapReduceOutput(new BasicDBObject(), mongoCursor,
                                                              new org.mongodb.connection.ServerAddress());

        expect:
        mapReduceOutput.getDuration() == expectedDuration
    }

    def 'should return the count values for a map-reduce into a collection'() {
        given:
        int expectedInputCount = 3
        int expectedOutputCount = 4
        int expectedEmitCount = 6

        MapReduceStatistics mapReduceStats = Mock();
        mapReduceStats.getInputCount() >> expectedInputCount
        mapReduceStats.getOutputCount() >> expectedOutputCount
        mapReduceStats.getEmitCount() >> expectedEmitCount

        @Subject
        MapReduceOutput mapReduceOutput = new MapReduceOutput(new BasicDBObject(), null, mapReduceStats, null, null);

        expect:
        mapReduceOutput.getInputCount() == expectedInputCount
        mapReduceOutput.getOutputCount() == expectedOutputCount
        mapReduceOutput.getEmitCount() == expectedEmitCount
    }

    def 'should return the count values for an inline map-reduce output'() {
        given:
        int expectedInputCount = 3
        int expectedOutputCount = 4
        int expectedEmitCount = 6

        MapReduceCursor mapReduceStats = Mock();
        mapReduceStats.getInputCount() >> expectedInputCount
        mapReduceStats.getOutputCount() >> expectedOutputCount
        mapReduceStats.getEmitCount() >> expectedEmitCount

        @Subject
        MapReduceOutput mapReduceOutput = new MapReduceOutput(new BasicDBObject(), mapReduceStats,
                                                              new org.mongodb.connection.ServerAddress());

        expect:
        mapReduceOutput.getInputCount() == expectedInputCount
        mapReduceOutput.getOutputCount() == expectedOutputCount
        mapReduceOutput.getEmitCount() == expectedEmitCount
    }
}
