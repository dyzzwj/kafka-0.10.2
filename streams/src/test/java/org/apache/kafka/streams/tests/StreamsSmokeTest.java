/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.streams.tests;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class StreamsSmokeTest {

    /**
     *  args ::= command kafka zookeeper stateDir disableAutoTerminate
     *  command := "run" | "process"
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String kafka = args[0];
        String stateDir = args.length > 1 ? args[1] : null;
        String command = args.length > 2 ? args[2] : null;
        boolean disableAutoTerminate = args.length > 3;

        System.out.println("StreamsTest instance started (StreamsSmokeTest)");
        System.out.println("command=" + command);
        System.out.println("kafka=" + kafka);
        System.out.println("stateDir=" + stateDir);
        System.out.println("disableAutoTerminate=" + disableAutoTerminate);

        switch (command) {
            case "standalone":
                SmokeTestDriver.main(args);
                break;
            case "run":
                // this starts the driver (data generation and result verification)
                final int numKeys = 10;
                final int maxRecordsPerKey = 500;
                if (disableAutoTerminate) {
                    SmokeTestDriver.generate(kafka, numKeys, maxRecordsPerKey, false);
                } else {
                    Map<String, Set<Integer>> allData = SmokeTestDriver.generate(kafka, numKeys, maxRecordsPerKey);
                    SmokeTestDriver.verify(kafka, allData, maxRecordsPerKey);
                }
                break;
            case "process":
                // this starts a KafkaStreams client
                final SmokeTestClient client = new SmokeTestClient(new File(stateDir), kafka);
                client.start();

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        client.close();
                    }
                });
                break;
            case "close-deadlock-test":
                final ShutdownDeadlockTest test = new ShutdownDeadlockTest(kafka);
                test.start();
                break;
            default:
                System.out.println("unknown command: " + command);
        }
    }

}
