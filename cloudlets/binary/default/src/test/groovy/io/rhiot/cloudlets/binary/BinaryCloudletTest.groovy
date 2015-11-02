/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.cloudlets.binary

import io.rhiot.steroids.camel.Route
import org.apache.camel.builder.RouteBuilder
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext
import static io.rhiot.steroids.camel.CamelBootInitializer.eventBus

class BinaryCloudletTest extends Assert {

    static def cloudlet = new BinaryCloudlet()

    @BeforeClass
    static void beforeClass() {
        cloudlet.start()
    }

    @Test
    void shouldReceiveBinaryFromDevice() {
        def binaries = camelContext().createConsumerTemplate().receiveBody(eventBus('device.binary'))
        assertThat(binaries).isNotNull()
    }

}

@Route
class TestMqttClient extends RouteBuilder {

    @Override
    void configure() throws Exception {
        from('timer:foo').setBody(constant('message')).
                to('paho:device/binary/clientID')
    }

}