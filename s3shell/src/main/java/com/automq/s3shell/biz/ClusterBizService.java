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

import com.automq.s3shell.S3Shell;
import com.automq.s3shell.s3resource.S3Cluster;

public class ClusterBizService {
    
    private final S3Shell s3Shell;
    
    private final ConfigBizService configBizService;
    
    public ClusterBizService(S3Shell s3Shell) {
        this.s3Shell = s3Shell;
        this.configBizService = new ConfigBizService(s3Shell);
    }
    
    public String createCluster() {
        S3Cluster s3Cluster = new S3Cluster();
        s3Shell.create(s3Cluster.getClusterMetadata().genKeyPath(), s3Cluster.serialize());
        return s3Cluster.getClusterMetadata().getResourceId();
    }
    
    public S3Cluster queryCluster(String clusterId) {
        var path = S3Cluster.genPathByClusterId(clusterId);
        if (s3Shell.exists(path)) {
            var data = s3Shell.getData(path);
            return (S3Cluster) new S3Cluster().deserialize(data);
        } else {
            return null;
        }
    }
    
}
