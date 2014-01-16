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

package org.mongodb

import org.mongodb.connection.ServerAddress
import spock.lang.Specification
import spock.lang.Unroll


class ServerAddressSpecification extends Specification {

    @Unroll
    def 'constructors should parse hostname and port correctly'() {
        expect:
        address.getHost() == host;
        address.getPort() == port;

        where:
        address                                                 | host                           | port
        new ServerAddress()                                     | ServerAddress.getDefaultHost() | ServerAddress.getDefaultPort()
        new ServerAddress('10.0.0.1:1000')                      | '10.0.0.1'                     | 1000
        new ServerAddress('10.0.0.1')                           | '10.0.0.1'                     | ServerAddress.getDefaultPort()
        new ServerAddress('10.0.0.1', 1000)                     | '10.0.0.1'                     | 1000
        new ServerAddress('somewhere')                          | 'somewhere'                    | ServerAddress.getDefaultPort()
        new ServerAddress('somewhere:1000')                     | 'somewhere'                    | 1000
        new ServerAddress('somewhere', 1000)                    | 'somewhere'                    | 1000
        new ServerAddress('[2010:836B:4179::836B:4179]')        | '2010:836B:4179::836B:4179'    | ServerAddress.getDefaultPort()
        new ServerAddress('[2010:836B:4179::836B:4179]:1000')   | '2010:836B:4179::836B:4179'    | 1000
        new ServerAddress('[2010:836B:4179::836B:4179]', 1000)  | '2010:836B:4179::836B:4179'    | 1000
    }

    def 'ipv4 host with a port specified should throw when a port is also specified as an argumentc'() {
        when:
        new ServerAddress('10.0.0.1:80', 80);
        then:
        thrown(IllegalArgumentException);

        when:
        new ServerAddress('10.0.0.1:1000', 80);
        then:
        thrown(IllegalArgumentException);
    }

    def 'ipv6 host with a port specified should throw when a port is also specified as an argument'() {
        when:
        new ServerAddress('[2010:836B:4179::836B:4179]:80', 80);
        then:
        thrown(IllegalArgumentException);

        when:
        new ServerAddress('[2010:836B:4179::836B:4179]:1000', 80);
        then:
        thrown(IllegalArgumentException);
    }

    def 'ipv6 host should throw when terminating ] is not specified'() {
        when:
        new ServerAddress('[2010:836B:4179::836B:4179');
        then:
        thrown(IllegalArgumentException);
    }

    def 'hostname with a port specified should throw when a port is also specified as an argument'() {
        when:
        new ServerAddress('somewhere:80', 80)
        then:
        thrown(IllegalArgumentException);

        when:
        new ServerAddress('somewhere:1000', 80);
        then:
        thrown(IllegalArgumentException);
    }
}