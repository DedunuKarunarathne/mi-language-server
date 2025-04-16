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
import org.apache.commons.lang3.NotImplementedException;

public class DebugInfo implements IDebugInfo {

    String mediatorPosition;
    boolean isValid = Boolean.TRUE;
    String error;

    @Override
    public boolean isValid() {

        return isValid;
    }

    @Override
    public void setValid(boolean valid) {

        isValid = valid;
    }

    @Override
    public String getError() {

        return error;
    }

    @Override
    public void setError(String error) {

        this.error = error;
    }

    @Override
    public JsonElement toJson() {

        throw new NotImplementedException("toJson method is not implemented");
    }

    @Override
    public void setMediatorPosition(String mediatorPosition) {

        this.mediatorPosition = mediatorPosition;
    }

    @Override
    public String getMediatorPosition() {

        return mediatorPosition;
    }

    @Override
    public IDebugInfo clone() throws CloneNotSupportedException {

        return (IDebugInfo) super.clone();
    }

    @Override
    public String toString() {

        return "DebugInfo{" +
                "mediatorPosition='" + mediatorPosition + '\'' +
                ", isValid=" + isValid +
                ", error='" + error + '\'' +
                '}';
    }
}
