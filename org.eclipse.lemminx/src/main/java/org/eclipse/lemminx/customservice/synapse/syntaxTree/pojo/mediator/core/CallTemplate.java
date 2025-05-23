/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     WSO2 LLC - support for WSO2 Micro Integrator Configuration
 */

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.core;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class CallTemplate extends Mediator {

    WithParam[] withParam;
    String target;
    String onError;
    String description;

    public CallTemplate() {
        setDisplayName("Call Template");
    }

    public WithParam[] getWithParam() {

        return withParam;
    }

    public void setWithParam(WithParam[] withParam) {

        this.withParam = withParam;
    }

    public String getTarget() {

        return target;
    }

    public void setTarget(String target) {

        this.target = target;
    }

    public String getOnError() {

        return onError;
    }

    public void setOnError(String onError) {

        this.onError = onError;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}