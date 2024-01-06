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

import com.automq.s3shell.constant.S3ResourceType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.NoArgsConstructor;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor
public class S3ServerMetadata implements S3ResourceMetadata {
    
    private String clusterId;
    
    private String serverId;
    
    private long createTimeMs;
    
    private long updateTimeMs;
    
    public S3ServerMetadata(String clusterId, String serverId) {
        this.clusterId = clusterId;
        this.serverId = serverId;
        this.createTimeMs = System.currentTimeMillis();
        this.updateTimeMs = System.currentTimeMillis();
    }
    
    @Override
    public S3ResourceType getResourceType() {
        return S3ResourceType.SERVER;
    }
    
    @Override
    public S3ResourceType getChildResourceType() {
        return S3ResourceType.NONE;
    }
    
    @Override
    public String getResourceId() {
        return serverId;
    }
    
    @Override
    public String genKeyPath() {
        return clusterId + "/" + serverId;
    }
    
    @Override
    public long getCreateTimeMs() {
        return createTimeMs;
    }
    
    @Override
    public long getUpdateTimeMs() {
        return updateTimeMs;
    }
}
