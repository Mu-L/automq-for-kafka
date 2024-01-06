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

import java.util.Map;

/**
 * 以时间维度上传到 S3 的 Metric
 */
public interface S3Metric {
    
    void putMetric(String metricName, String metricContent);
    
    String getLatestMetric(String metricName);
    
    Map<Long, String> getMetric(String metricName, long startTime, long endTime);
    
    void removeMetric(String metricName, long startTime, long endTime);
}
