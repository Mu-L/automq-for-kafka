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

package com.automq.s3shell.biz;

import com.automq.s3shell.S3ShellBaseTest;
import com.automq.s3shell.s3resource.S3Cluster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClusterBizServiceTest extends S3ShellBaseTest {
    
    private static ClusterBizService clusterBizService;
    
    @BeforeAll
    public static void setup() {
        clusterBizService = new ClusterBizService(s3Shell);
    }
    
    @Test
    void testClusterBizService() {
        s3Shell.create(testKeyName, "hello world".getBytes());
        var clusterId = clusterBizService.createCluster();
        S3Cluster myCluster = clusterBizService.queryCluster(clusterId);
        Assertions.assertEquals(myCluster.getClusterMetadata().getResourceId(), clusterId);
        
    }
    
}