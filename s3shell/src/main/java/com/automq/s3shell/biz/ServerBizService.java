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
import com.automq.s3shell.constant.ServerRoleType;
import com.automq.s3shell.s3resource.S3Server;
import java.io.File;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

public class ServerBizService {
    
    private final S3Shell s3Shell;
    
    public ServerBizService(S3Shell s3Shell) {
        this.s3Shell = s3Shell;
    }
    
    public void createServer(String clusterId, String serverId, int nodeId, ServerRoleType serverRoleType,
                             String autoSelectedListenerIpAddress, String controllerQuorumVoters) {
        S3Server s3Server = new S3Server(clusterId, serverId, nodeId, serverRoleType, autoSelectedListenerIpAddress,
                controllerQuorumVoters);
    }
    
    /**
     * Persist local server properties to s3
     */
    @SneakyThrows
    public void persistLocalServerProperties(String localPropsPath) {
        
        byte[] content = FileUtils.readFileToByteArray(new File(localPropsPath));
        
    }
    
}
