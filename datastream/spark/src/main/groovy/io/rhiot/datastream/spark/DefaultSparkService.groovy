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

import io.rhiot.bootstrap.classpath.Bean
import io.rhiot.bootstrap.classpath.Named
import io.rhiot.bootstrap.Bootstrap
import io.rhiot.bootstrap.BootstrapAware
import org.apache.camel.CamelContext

@Bean
@Named(name = 'spark')
class DefaultSparkService implements SparkService, BootstrapAware {

    private Bootstrap bootstrap

    @Override
    Object execute(String rdd, String rddCallback, Object payload) {
        def template = bootstrap.beanRegistry().bean(CamelContext.class).get().createProducerTemplate()
        template.requestBody("spark:rhiot?rdd=#${rdd}&rddCallback=#${rddCallback}", payload)
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}