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
package io.rhiot.gateway.gps

import com.fasterxml.jackson.databind.ObjectMapper
import io.rhiot.steroids.PropertyCondition
import io.rhiot.steroids.camel.Route
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.aggregate.AggregationStrategy

import static io.rhiot.utils.Properties.stringProperty
import static org.apache.camel.model.dataformat.JsonLibrary.Jackson

/**
 * Camel route reading current position data from the GPSD socket.
 */
@Route
@PropertyCondition(property = 'gps')
public class GpsdRoute extends RouteBuilder {

    // Configuration

    def gpsEndpoint = stringProperty('gps_endpoint', 'gpsd://gps')

    def storeDirectory = stringProperty('gps_store_directory', '/var/rhiot/gps')

    def enrich = stringProperty('gps_enrich')

    // Routing

    @Override
    void configure() {
        new File(storeDirectory).mkdirs()
        def route = from(gpsEndpoint).routeId('gps')
        if(enrich != null) {
            route = route.enrich(enrich, new AggregationStrategy() {
                @Override
                Exchange aggregate(Exchange original, Exchange polled) {
                    def body = new ObjectMapper().convertValue(original.in.body, Map.class)
                    original.in.body = body
                    body.put('enriched', new ObjectMapper().convertValue(polled.in.body, Map.class))
                    original
                }
            })
        }
        route.marshal().json(Jackson).to("file://${storeDirectory}")
    }

}