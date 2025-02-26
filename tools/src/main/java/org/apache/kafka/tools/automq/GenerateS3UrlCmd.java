/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.tools.automq;

import com.automq.s3shell.sdk.model.AuthMethod;
import com.automq.s3shell.sdk.model.EndpointProtocol;
import com.automq.stream.utils.S3Utils;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.kafka.common.Uuid;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.util.List;

import static net.sourceforge.argparse4j.impl.Arguments.store;
import static org.apache.kafka.tools.automq.AutoMQKafkaAdminTool.GENERATE_START_COMMAND_CMD;

/**
 * Generate s3url for user
 */
public class GenerateS3UrlCmd {

    private final GenerateS3UrlCmd.Parameter parameter;

    public GenerateS3UrlCmd(Parameter parameter) {
        this.parameter = parameter;
    }

    static class Parameter {
        final String s3AccessKey;
        final String s3SecretKey;

        final AuthMethod s3AuthMethod;

        final String s3Region;

        final EndpointProtocol endpointProtocol;

        final String s3Endpoint;

        final String s3DataBucket;

        final String s3OpsBucket;

        final boolean s3PathStyle;

        Parameter(Namespace res) {
            this.s3AccessKey = res.getString("s3-access-key");
            this.s3SecretKey = res.getString("s3-secret-key");
            String authMethodName = res.getString("s3-auth-method");
            if (authMethodName == null || authMethodName.trim().isEmpty()) {
                this.s3AuthMethod = AuthMethod.KEY_FROM_ARGS;
            } else {
                this.s3AuthMethod = AuthMethod.getByName(authMethodName);
            }
            this.s3Region = res.getString("s3-region");
            String endpointProtocolStr = res.get("s3-endpoint-protocol");
            this.endpointProtocol = EndpointProtocol.getByName(endpointProtocolStr);
            this.s3Endpoint = res.getString("s3-endpoint");
            this.s3DataBucket = res.getString("s3-data-bucket");
            String s3OpsBucketFromArg = res.getString("s3-ops-bucket");
            if (s3OpsBucketFromArg == null) {
                this.s3OpsBucket = this.s3DataBucket;
            } else {
                this.s3OpsBucket = s3OpsBucketFromArg;
            }
            this.s3PathStyle = Boolean.valueOf(res.getString("s3-path-style"));
        }
    }

    public static ArgumentParser addArguments(Subparser parser) {
        parser.addArgument("generate-s3-url")
            .action(store())
            .required(true);
        parser.addArgument("--s3-access-key")
            .action(store())
            .required(true)
            .type(String.class)
            .dest("s3-access-key")
            .metavar("S3-ACCESS-KEY")
            .help("Your accessKey that used to access S3");
        parser.addArgument("--s3-secret-key")
            .action(store())
            .required(true)
            .type(String.class)
            .dest("s3-secret-key")
            .metavar("S3-SECRET-KEY")
            .help("Your secretKey that used to access S3");
        parser.addArgument("--s3-auth-method")
            .action(store())
            .required(false)
            .setDefault(AuthMethod.KEY_FROM_ARGS.getKeyName())
            .type(String.class)
            .dest("s3-auth-method")
            .metavar("S3-AUTH-METHOD")
            .help("The auth method that used to access S3, default is key-from-env, other options are key-from-args and role");
        parser.addArgument("--s3-region")
            .action(store())
            .required(true)
            .type(String.class)
            .dest("s3-region")
            .metavar("S3-REGION")
            .help("The region of S3");
        parser.addArgument("--s3-endpoint-protocol")
            .action(store())
            .required(false)
            .setDefault("https")
            .type(String.class)
            .dest("s3-endpoint-protocol")
            .metavar("S3-ENDPOINT-PROTOCOL")
            .help("The protocol of S3 endpoint. Default is https, other options are http");
        parser.addArgument("--s3-endpoint")
            .action(store())
            .required(true)
            .type(String.class)
            .dest("s3-endpoint")
            .metavar("S3-ENDPOINT")
            .help("The endpoint of S3. Pay attention that protocol is not included. Example: s3.amazonaws.com");
        parser.addArgument("--s3-data-bucket")
            .action(store())
            .required(true)
            .type(String.class)
            .dest("s3-data-bucket")
            .metavar("S3-DATA-BUCKET")
            .help("The bucket name of S3 that used to store kafka's stream data");
        parser.addArgument("--s3-ops-bucket")
            .action(store())
            .required(false)
            .type(String.class)
            .dest("s3-ops-bucket")
            .metavar("S3-OPS-BUCKET")
            .help("The bucket name of S3 that used to store operation data like metric,log,naming service info etc. Use different bucket to store data and ops data is recommended");
        parser.addArgument("--s3-path-style")
            .action(store())
            .required(false)
            .setDefault(false)
            .type(String.class)
            .dest("s3-path-style")
            .metavar("S3-PATH-STYLE")
            .help("Enable s3 path style. If you are using minio, you need set it to true, default value is false.");

        return parser;
    }

    public String run() {
        System.out.println("####################################  S3 PRECHECK #################################");
        System.out.println();

        //precheck
        var context = S3Utils.S3Context.builder()
            .setEndpoint(parameter.endpointProtocol.getName() + "://" + parameter.s3Endpoint)
            .setCredentialsProviders(List.of(() -> AwsBasicCredentials.create(parameter.s3AccessKey, parameter.s3SecretKey)))
            .setBucketName(parameter.s3DataBucket)
            .setRegion(parameter.s3Region)
            .setForcePathStyle(false)
            .build();
        S3Utils.checkS3Access(context);

        String s3Url = buildS3Url();
        System.out.println("##########  S3 URL RESULT ############");
        System.out.println();
        System.out.println("Your S3 URL is: \n");
        System.out.println(s3Url);
        System.out.println("\n");

        System.out.println("############  S3 URL USAGE ##############");
        System.out.println("You can use s3url to generate start command to start AutoMQ");
        System.out.println("------------------------ COPY ME ------------------");
        //tips: Not add whitespace after \\
        System.out.println(String.format("bin/automq-kafka-admin.sh %s \\%n"
            + "--s3-url=\"%s\" \\%n"
            + "--controller-address=\"192.168.0.1:9093;192.168.0.2:9093;192.168.0.3:9093\"  \\%n"
            + "--broker-address=\"192.168.0.4:9092;192.168.0.5:9092\"   %n", GENERATE_START_COMMAND_CMD, s3Url
        ));
        System.out.println("TIPS: Replace the controller-address and broker-address with your real ip list.");

        return s3Url;
    }

    private String buildS3Url() {
        StringBuilder s3UrlBuilder = new StringBuilder();
        s3UrlBuilder
            .append("s3://")
            .append(parameter.s3Endpoint)
            .append("?").append("s3-access-key=").append(parameter.s3AccessKey)
            .append("&").append("s3-secret-key=").append(parameter.s3SecretKey)
            .append("&").append("s3-region=").append(parameter.s3Region)
            .append("&").append("s3-auth-method=").append(parameter.s3AuthMethod.getKeyName())
            .append("&").append("s3-endpoint-protocol=").append(parameter.endpointProtocol.getName())
            .append("&").append("s3-data-bucket=").append(parameter.s3DataBucket)
            .append("&").append("s3-path-style=").append(parameter.s3PathStyle)
            .append("&").append("s3-ops-bucket=").append(parameter.s3OpsBucket)
            .append("&").append("cluster-id=").append(Uuid.randomUuid());
        return s3UrlBuilder.toString();
    }

}
