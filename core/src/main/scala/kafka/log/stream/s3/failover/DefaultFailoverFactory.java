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

package kafka.log.stream.s3.failover;

import com.automq.stream.s3.failover.FailoverFactory;
import com.automq.stream.s3.metadata.S3ObjectMetadata;
import com.automq.stream.s3.metadata.StreamMetadata;
import com.automq.stream.s3.objects.CommitStreamSetObjectRequest;
import com.automq.stream.s3.objects.CommitStreamSetObjectResponse;
import com.automq.stream.s3.objects.CompactStreamObjectRequest;
import com.automq.stream.s3.objects.ObjectManager;
import com.automq.stream.s3.streams.StreamManager;
import kafka.log.stream.s3.objects.ControllerObjectManager;
import kafka.log.stream.s3.streams.ControllerStreamManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DefaultFailoverFactory implements FailoverFactory {
    private final ControllerStreamManager streamManager;
    private final ControllerObjectManager objectManager;

    public DefaultFailoverFactory(ControllerStreamManager streamManager, ControllerObjectManager objectManager) {
        this.streamManager = streamManager;
        this.objectManager = objectManager;
    }

    public StreamManager getStreamManager(final int nodeId, final long epoch) {
        return new StreamManager() {
            public CompletableFuture<List<StreamMetadata>> getOpeningStreams() {
                return DefaultFailoverFactory.this.streamManager.getOpeningStreams(nodeId, epoch, true);
            }

            public CompletableFuture<List<StreamMetadata>> getStreams(List<Long> list) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<Long> createStream() {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<StreamMetadata> openStream(long streamId, long epochx) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<Void> trimStream(long streamId, long epochx, long offset) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<Void> closeStream(long streamId, long streamEpoch) {
                return DefaultFailoverFactory.this.streamManager.closeStream(streamId, streamEpoch, nodeId, epoch);
            }

            public CompletableFuture<Void> deleteStream(long streamId, long epochx) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }
        };
    }

    public ObjectManager getObjectManager(final int nodeId, final long epoch) {
        return new ObjectManager() {
            public CompletableFuture<Long> prepareObject(int count, long ttl) {
                return DefaultFailoverFactory.this.objectManager.prepareObject(count, ttl);
            }

            public CompletableFuture<CommitStreamSetObjectResponse> commitStreamSetObject(CommitStreamSetObjectRequest commitStreamSetObjectRequest) {
                return DefaultFailoverFactory.this.objectManager.commitStreamSetObject(commitStreamSetObjectRequest, nodeId, epoch, true);
            }

            public CompletableFuture<Void> compactStreamObject(CompactStreamObjectRequest compactStreamObjectRequest) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<List<S3ObjectMetadata>> getObjects(long streamId, long startOffset, long endOffset, int limit) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<List<S3ObjectMetadata>> getServerObjects() {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }

            public CompletableFuture<List<S3ObjectMetadata>> getStreamObjects(long streamId, long startOffset, long endOffset, int limit) {
                return CompletableFuture.failedFuture(new UnsupportedOperationException());
            }
        };
    }
}
