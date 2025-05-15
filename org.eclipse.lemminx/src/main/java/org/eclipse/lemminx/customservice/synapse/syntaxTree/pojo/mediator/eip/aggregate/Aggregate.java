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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.eip.aggregate;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Aggregate extends Mediator {

    CorrelateOnOrCompleteConditionOrOnComplete correlateOnOrCompleteConditionOrOnComplete;
    String description;
    String id;

    public Aggregate() {
        setDisplayName("Aggregate");
    }

    public CorrelateOnOrCompleteConditionOrOnComplete getCorrelateOnOrCompleteConditionOrOnComplete() {

        return correlateOnOrCompleteConditionOrOnComplete;
    }

    public void setCorrelateOnOrCompleteConditionOrOnComplete(CorrelateOnOrCompleteConditionOrOnComplete correlateOnOrCompleteConditionOrOnComplete) {

        this.correlateOnOrCompleteConditionOrOnComplete = correlateOnOrCompleteConditionOrOnComplete;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }
}