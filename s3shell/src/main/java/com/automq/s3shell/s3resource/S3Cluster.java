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

package com.automq.s3shell.s3resource;

import com.automq.s3shell.util.JacksonUtil;
import java.util.UUID;
import lombok.Getter;

@Getter
public class S3Cluster implements S3Resource {
    
    private final S3ClusterMetadata clusterMetadata;
    
    public S3Cluster() {
        
        this.clusterMetadata = new S3ClusterMetadata(UUID.randomUUID().toString(), System.currentTimeMillis(), System.currentTimeMillis());
    }
    
    public static String genPathByClusterId(String clusterId) {
        return clusterId;
    }
    
    @Override
    public S3ResourceMetadata getMetadata() {
        return clusterMetadata;
    }
    
    @Override
    public byte[] serialize() {
        return JacksonUtil.toJsonString(this).getBytes();
    }
    
    @Override
    public S3Resource deserialize(byte[] bytes) {
        return JacksonUtil.readJson(new String(bytes), S3Cluster.class);
    }
}
