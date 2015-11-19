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
package io.rhiot.component.kura.router;

import org.apache.camel.component.kura.KuraRouter;
import org.osgi.framework.BundleContext;

public abstract class RhiotKuraRouter extends KuraRouter {

    // TODO Remove this overridden method as soon as Camel 2.17 is out (see CAMEL-9314)
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        try {
            super.start(bundleContext);
        } catch (Throwable e) {
            String errorMessage = "Problem when starting Kura module " + getClass().getName() + ":";
            log.warn(errorMessage, e);

            // Print error to the Kura console.
            System.err.println(errorMessage);
            e.printStackTrace();

            throw e;
        }
    }

}