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

package com.mongodb;

import org.bson.BSONDecoder;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO Documentation needed.
 */
public interface DBDecoder extends BSONDecoder {

    DBCallback getDBCallback(DBCollection collection);

    DBObject decode(InputStream in, DBCollection collection) throws IOException;

    DBObject decode(byte[] bytes, DBCollection collection);
}
