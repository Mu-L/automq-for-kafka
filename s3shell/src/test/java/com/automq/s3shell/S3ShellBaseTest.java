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

package com.automq.s3shell;

import com.automq.s3shell.client.S3ShellOption;
import com.automq.s3shell.constant.AuthMethod;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import software.amazon.awssdk.regions.Region;

/**
 * <pre>
 * Test case depend on local environment to acquire aws credentials.
 * Ensure you already configured AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY in your environment.
 * </pre>
 */
public class S3ShellBaseTest {
    
    protected static S3Shell s3Shell;
    
    protected static final String testBucketName = "s3shell-test-bucket";
    
    protected static final String testKeyName = "s3shell-test";
    
    @BeforeAll
    public static void setUp() {
        S3ShellOption s3ShellOption = S3ShellOption.builder()
                .opsBucketName(testBucketName)
                .authMethod(AuthMethod.KEY_FROM_ENV)
                .region(Region.CN_NORTHWEST_1.id())
                .s3Endpoint("http://s3.cn-northwest-1.amazonaws.com.cn")
                .build();
        s3Shell = new S3Shell(s3ShellOption);
    }
    
    @AfterEach
    public void clean() {
        var pathList = s3Shell.list(testKeyName + "/");
        if (!pathList.isEmpty()) {
            s3Shell.delete(pathList);
        }
        s3Shell.delete(testKeyName);
    }
    
    @AfterAll
    public static void tearDown() {
        var pathList = s3Shell.list(testKeyName + "/");
        if (!pathList.isEmpty()) {
            s3Shell.delete(pathList);
        }
        s3Shell.close();
    }
    
}
