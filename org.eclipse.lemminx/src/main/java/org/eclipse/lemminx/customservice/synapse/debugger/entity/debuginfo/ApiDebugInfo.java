/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ApiDebugInfo extends DebugInfo {

    String apiKey;
    String method;
    String uriTemplate;
    String urlMapping;
    String sequenceType;

    public void setApiKey(String apiKey) {

        this.apiKey = apiKey;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public void setSequenceType(String sequenceType) {

        this.sequenceType = sequenceType;
    }

    public void setUriTemplate(String uriTemplate) {

        this.uriTemplate = uriTemplate;
    }

    public void setUrlMapping(String urlMapping) {

        this.urlMapping = urlMapping;
    }

    public String getApiKey() {

        return apiKey;
    }

    public String getMethod() {

        return method;
    }

    public String getUriTemplate() {

        return uriTemplate;
    }

    public String getUrlMapping() {

        return urlMapping;
    }

    public String getSequenceType() {

        return sequenceType;
    }

    public JsonElement toJson() {

        JsonObject rootNode = new JsonObject();
        JsonObject sequence = new JsonObject();
        JsonObject api = new JsonObject();
        api.addProperty("api-key", apiKey);
        JsonObject resource = new JsonObject();
        resource.addProperty("method", method);
        if (uriTemplate != null) {
            resource.addProperty("uri-template", uriTemplate);
        } else {
            resource.addProperty("url-mapping", urlMapping);
        }
        api.add("resource", resource);
        api.addProperty("sequence-type", sequenceType);
        api.addProperty("mediator-position", mediatorPosition);
        sequence.add("api", api);
        rootNode.add("sequence", sequence);

        rootNode.addProperty("mediation-component", "sequence");
        return rootNode;
    }

    @Override
    public String toString() {

        return "ApiDebugInfo{" +
                "apiKey='" + apiKey + '\'' +
                ", method='" + method + '\'' +
                ", uriTemplate='" + uriTemplate + '\'' +
                ", urlMapping='" + urlMapping + '\'' +
                ", sequenceType='" + sequenceType + '\'' +
                ", mediatorPosition='" + mediatorPosition + '\'' +
                ", isValid=" + isValid +
                ", error='" + error + '\'' +
                '}';
    }
}
