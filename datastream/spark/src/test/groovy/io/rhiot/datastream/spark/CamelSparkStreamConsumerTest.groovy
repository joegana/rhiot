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
package io.rhiot.datastream.spark

import io.rhiot.datastream.engine.DataStream
import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.json.Json
import org.apache.camel.CamelContext
import org.apache.camel.component.spark.RddCallback
import org.apache.spark.api.java.JavaRDD
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static org.apache.camel.component.spark.Sparks.createLocalSparkContext

class CamelSparkStreamConsumerTest {

    static dataStream = new DataStream().start()
    static {
        CamelBootInitializer.registry().put('rdd', createLocalSparkContext().textFile('src/test/resources/testrdd.txt'))
        CamelBootInitializer.registry().put('callback', new RddCallback<Long>() {
            @Override
            Long onRdd(JavaRDD rdd, Object... payloads) {
                return ((int) payloads[0]) * rdd.count();
            }
        })

    }

    @Test
    void should() {
        def encodedResult = dataStream.beanRegistry().bean(CamelContext.class).get().createProducerTemplate().requestBody(amqp('spark.rdd.callback'), Json.encode([input: 10]), String.class)
        def result = Json.decodeValue(encodedResult, Map.class).result
        assertThat(result).isEqualTo(170)
    }

}
