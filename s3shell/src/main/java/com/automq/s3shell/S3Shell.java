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
import com.automq.s3shell.util.S3PathUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Shell {
    
    protected final S3Client s3Client;
    
    protected final String opsBucketName;
    
    private static final Logger log = LoggerFactory.getLogger(S3Shell.class);
    
    public S3Shell(S3ShellOption options) {
        opsBucketName = options.getOpsBucketName();
        Region region = Region.of(options.getRegion());
        if (AuthMethod.KEY_FROM_ARGS == options.getAuthMethod()) {
            this.s3Client = S3Client.builder().region(region)
                    .credentialsProvider(
                            AwsCredentialsProviderChain.builder().reuseLastProviderEnabled(true).credentialsProviders(() -> AwsBasicCredentials.create(options.getAccessKey(), options.getSecretKey()),
                                    InstanceProfileCredentialsProvider.create(), AnonymousCredentialsProvider.create()).build())
                    .build();
        } else if (AuthMethod.KEY_FROM_ENV == options.getAuthMethod()) {
            EnvironmentVariableCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
            this.s3Client = S3Client.builder().region(region).credentialsProvider(credentialsProvider).build();
            
        } else {
            throw new UnsupportedOperationException(String.format("Not supported auth type: %s", options.getAuthMethod()));
        }
        
    }
    
    /**
     * @param path The path for the s3 node. Aws also call it key.
     * @param data The data to be stored in the s3 node
     */
    public void create(final String path, byte[] data) {
        if (!S3PathUtil.isValidPath(path)) {
            String errMsg = "Path %s is not a valid s3 path, maybe it is empty.";
            log.error(String.format(errMsg, path));
            throw new IllegalArgumentException(String.format(errMsg, path));
        }
        
        try {
            PutObjectRequest putOb = PutObjectRequest.builder().bucket(opsBucketName).key(path).build();
            
            s3Client.putObject(putOb, RequestBody.fromBytes(data));
            
        } catch (S3Exception e) {
            log.error(String.format("Create s3 node failed with exception. Path is %s. Root cause is " + ExceptionUtils.getRootCause(e), path));
            throw e;
        }
    }
    
    /**
     * @param path The path for the s3 node. Aws also call it key.
     * @return Whether the node exists
     */
    public boolean exists(String path) {
        if (!S3PathUtil.isValidPath(path)) {
            String errMsg = "Path %s is not a valid s3 path, maybe it is empty.";
            log.error(String.format(errMsg, path));
            throw new IllegalArgumentException(String.format(errMsg, path));
        }
        
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(opsBucketName).key(path).build();
            
            s3Client.headObject(headObjectRequest);
            
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404 || e.statusCode() == 403) {
                return false;
            } else {
                log.error(String.format("Check s3 node exists failed with exception. Path is %s . Root cause is " + ExceptionUtils.getRootCause(e), path));
                throw e;
            }
        }
    }
    
    /**
     * @param path The path for the s3 node. Aws also call it key.
     * @return children object's key list
     */
    public List<String> list(String path) {
        if (!S3PathUtil.isValidPath(path)) {
            String errMsg = "Path %s is not a valid s3 path, maybe it is empty.";
            log.error(String.format(errMsg, path));
            throw new IllegalArgumentException(String.format(errMsg, path));
        }
        
        if (!path.endsWith("/")) {
            path += "/";
        }
        
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder().prefix(path).bucket(opsBucketName).build();
            
            ListObjectsResponse res = s3Client.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            
            return objects.stream().map(S3Object::key).collect(Collectors.toList());
            
        } catch (S3Exception e) {
            log.error(String.format("Get s3 node children failed with exception. Path is %s . Root cause is " + ExceptionUtils.getRootCause(e), path));
            throw e;
        }
    }
    
    public void delete(String path) {
        if (!S3PathUtil.isValidPath(path)) {
            String errMsg = "Path %s is not a valid s3 path, maybe it is empty.";
            log.error(String.format(errMsg, path));
            throw new IllegalArgumentException(String.format(errMsg, path));
        }
        
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder().key(path).build());
        
        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder().bucket(opsBucketName).delete(Delete.builder().objects(toDelete).build()).build();
            
            s3Client.deleteObjects(dor);
            
        } catch (S3Exception e) {
            log.error(String.format("Delete s3 node failed with exception. Path is %s . Root cause is " + ExceptionUtils.getRootCause(e), path));
            throw e;
        }
    }
    
    public void delete(List<String> pathList) {
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        
        for (String path : pathList) {
            if (!S3PathUtil.isValidPath(path)) {
                String errMsg = "Path %s is not a valid s3 path, maybe it is empty.";
                log.error(String.format(errMsg, path));
                throw new IllegalArgumentException(String.format(errMsg, path));
            }
            toDelete.add(ObjectIdentifier.builder().key(path).build());
        }
        
        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest
                    .builder()
                    .bucket(opsBucketName)
                    .delete(Delete.builder()
                            .objects(toDelete)
                            .build())
                    .build();
            
            s3Client.deleteObjects(dor);
            
        } catch (S3Exception e) {
            log.error(String.format("Delete s3 node batch failed with exception. Path count is %s . Root cause is " + ExceptionUtils.getRootCause(e), pathList.size()));
            throw e;
        }
    }
    
    public byte[] getData(String path) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(path)
                    .bucket(opsBucketName)
                    .build();
            
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            log.error(String.format("Get s3 node data failed with exception. Path is %s . Root cause is " + ExceptionUtils.getRootCause(e), path));
            throw e;
        }
    }
    
    public void close() {
        s3Client.close();
        log.warn("S3Shell is closed..");
    }
}
