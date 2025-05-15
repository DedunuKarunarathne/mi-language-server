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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.enrich;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Enrich extends Mediator {

    SourceEnrich source;
    TargetEnrich target;
    String description;

    public Enrich() {
        setDisplayName("Enrich");
    }

    public SourceEnrich getSource() {

        return source;
    }

    public void setSource(SourceEnrich source) {

        this.source = source;
    }

    public TargetEnrich getTarget() {

        return target;
    }

    public void setTarget(TargetEnrich target) {

        this.target = target;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}