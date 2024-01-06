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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class S3ShellTest extends S3ShellBaseTest {
    
    @Test
    void create() {
        s3Shell.create(testKeyName, "hello world".getBytes());
        Assertions.assertTrue(s3Shell.exists(testKeyName));
    }
    
    @Test
    void exists() {
        Assertions.assertFalse(s3Shell.exists("UNKNOWN"));
    }
    
    @Test
    void list() {
        s3Shell.create(testKeyName, "hello world".getBytes());
        s3Shell.create(testKeyName + "/" + "child1", "hello world".getBytes());
        s3Shell.create(testKeyName + "/" + "child2", "hello world".getBytes());
        s3Shell.create(testKeyName + "/" + "child3", "hello world".getBytes());
        var pathList = s3Shell.list(testKeyName);
        Assertions.assertEquals(3, pathList.size());
        
    }
    
    @Test
    void delete() {
        s3Shell.create(testKeyName, "hello world".getBytes());
        s3Shell.delete(testKeyName);
        Assertions.assertFalse(s3Shell.exists(testKeyName));
        
    }
    
    @Test
    void getData() {
        s3Shell.create(testKeyName, "hello world".getBytes());
        var data = s3Shell.getData(testKeyName);
        Assertions.assertEquals("hello world", new String(data));
        
    }
}