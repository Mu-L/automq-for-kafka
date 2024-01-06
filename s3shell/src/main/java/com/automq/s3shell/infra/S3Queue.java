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

package com.automq.s3shell.infra;

import java.util.concurrent.Future;

/**
 * 类似于 Java JMS 的 Queue 模型，增加 ReplyTo 的功能，基本就能模拟出一个 RPC 的场景了。
 * 不用 RPC 的原因是担心数据面和控制面因为版本不一致带来一些处理的复杂度。或者未来要集成其他语言的客户端。
 * 消息设计为至少投递一次，不保证顺序，不保证幂等，消息在队列中按序单线程消费，严格的先入先出。
 */
public interface S3Queue {
    
    Future<Message> send(String queueName, Message message);
    
    void addListener(String queueName, long pollIntervalMilliseconds, MessageListener listener);
    
    interface Message {
        
        void setMessageID(long messageID);
        
        void getMessageID();
        
        long getCorrelationID();
        
        String getReplyToQueueName();
        
        void setCorrelationID(long correlationID);
        
        void setReplyToQueueName(String replyToQueueName);
        
        void setExpiration(long expiration);
        
        long getExpiration();
        
    }
    
    interface MessageListener {
        
        void onMessage(Message message);
    }
}
