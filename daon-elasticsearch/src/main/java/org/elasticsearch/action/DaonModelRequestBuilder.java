/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action;

import org.elasticsearch.action.support.nodes.NodesOperationRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.common.unit.TimeValue;

/**
 * model request builder
 */
public class DaonModelRequestBuilder extends NodesOperationRequestBuilder<DaonModelRequest, DaonModelResponse, DaonModelRequestBuilder> {

    public DaonModelRequestBuilder(ElasticsearchClient client, DaonModelAction action) {
        super(client, action, new DaonModelRequest());
    }

    public DaonModelRequestBuilder setInit(boolean init) {
        request.setInit(init);
        return this;
    }

    public DaonModelRequestBuilder setFilePath(String filePath) {
        request.setFilePath(filePath);
        return this;
    }

    public DaonModelRequestBuilder setURL(String url) {
        request.setUrl(url);
        return this;
    }

    public DaonModelRequestBuilder setTimeout(long timeout) {
        request.timeout(new TimeValue(timeout));
        return this;
    }



}
