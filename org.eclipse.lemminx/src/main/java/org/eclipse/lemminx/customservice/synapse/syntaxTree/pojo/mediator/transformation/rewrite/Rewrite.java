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

package org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.rewrite;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.Mediator;

public class Rewrite extends Mediator {

    RewriteRewriterule[] rewriterule;
    String inProperty;
    String outProperty;
    String description;

    public Rewrite(){
        setDisplayName("Rewrite");
    }

    public RewriteRewriterule[] getRewriterule() {

        return rewriterule;
    }

    public void setRewriterule(RewriteRewriterule[] rewriterule) {

        this.rewriterule = rewriterule;
    }

    public String getInProperty() {

        return inProperty;
    }

    public void setInProperty(String inProperty) {

        this.inProperty = inProperty;
    }

    public String getOutProperty() {

        return outProperty;
    }

    public void setOutProperty(String outProperty) {

        this.outProperty = outProperty;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }
}