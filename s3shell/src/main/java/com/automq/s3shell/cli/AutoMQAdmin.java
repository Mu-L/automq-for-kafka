/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.automq.s3shell.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "automq-kafka-admin", mixinStandardHelpOptions = true, version = "AutoMQ Kafka Admin 1.0", description = "Command line tools for AutoMQ Kafka", showDefaultValues = true, subcommands = {})
public class AutoMQAdmin {
    
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;
    
    @CommandLine.Option(names = {"-e", "--endpoint"}, description = "The access endpoint of the server", required = true)
    String endpoint;
    
    @CommandLine.Option(names = {"-a", "--access-key"}, description = "The authentication access key")
    String accessKey = "";
    
    @CommandLine.Option(names = {"-s", "--secret-key"}, description = "The authentication secret key")
    String secretKey = "";
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public String getAccessKey() {
        return accessKey;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new AutoMQAdmin()).execute(args);
        System.exit(exitCode);
    }
}
