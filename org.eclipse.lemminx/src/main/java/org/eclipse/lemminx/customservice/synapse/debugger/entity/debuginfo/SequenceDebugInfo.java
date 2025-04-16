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

public class SequenceDebugInfo extends DebugInfo {

    String sequenceKey;

    public void setSequenceKey(String sequenceKey) {

        this.sequenceKey = sequenceKey;
    }

    @Override
    public JsonElement toJson() {

        JsonObject rootNode = new JsonObject();
        JsonObject sequence = new JsonObject();
        sequence.addProperty("sequence-type", "named");
        sequence.addProperty("sequence-key", sequenceKey);
        sequence.addProperty("mediator-position", mediatorPosition);

        rootNode.add("sequence", sequence);

        rootNode.addProperty("mediation-component", "sequence");

        return rootNode;
    }

    @Override
    public String toString() {

        return "SequenceDebugInfo{" +
                "sequenceKey='" + sequenceKey + '\'' +
                ", mediatorPosition='" + mediatorPosition + '\'' +
                ", isValid=" + isValid +
                ", error='" + error + '\'' +
                '}';
    }
}
