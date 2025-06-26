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

package org.eclipse.lemminx.customservice.synapse.resourceFinder;

import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.RequestedResource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.Resource;
import org.eclipse.lemminx.customservice.synapse.resourceFinder.pojo.ResourceResponse;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class NewProjectResourceFinder extends AbstractResourceFinder {

    @Override
    protected ResourceResponse findResources(String projectPath, List<RequestedResource> types) {

        ResourceResponse response = new ResourceResponse();

        findArtifactResources(projectPath, types, response);
        findRegistryResources(projectPath, types, response);

        Map<String, ResourceResponse> dependentResourcesMap = getDependentResourcesMap();
        for (RequestedResource type : types) {
            String resourceType = type.getType();
            if (dependentResourcesMap.containsKey(resourceType)) {
                ResourceResponse dependentResponse = dependentResourcesMap.get(resourceType);
                mergeResourceResponses(response, dependentResponse);
            }
        }
        return response;
    }

    private void findArtifactResources(String projectPath, List<RequestedResource> types, ResourceResponse response) {

        Path artifactsPath = Path.of(projectPath, "src", "main", "wso2mi", "artifacts");
        List<Resource> resourcesInArtifacts = findResourceInArtifacts(artifactsPath, types);
        Path localEntryPath = Path.of(artifactsPath.toString(), "local-entries");
        List<Resource> resourcesInLocalEntry = findResourceInLocalEntry(localEntryPath, types);
        resourcesInArtifacts.addAll(resourcesInLocalEntry);
        response.setResources(resourcesInArtifacts);
    }

    private void findRegistryResources(String projectPath, List<RequestedResource> types, ResourceResponse response) {

        Path registryPath = Path.of(projectPath, Constant.SRC, Constant.MAIN, Constant.WSO2MI, Constant.RESOURCES);
        List<Resource> resourcesInRegistry = findResourceInRegistry(registryPath, types);
        response.setRegistryResources(resourcesInRegistry);
    }

    @Override
    protected String getArtifactFolder(String type) {

        if (Constant.API.equalsIgnoreCase(type)) {
            return "apis";
        } else if (Constant.ENDPOINT.equalsIgnoreCase(type)) {
            return "endpoints";
        } else if (Constant.SEQUENCE.equalsIgnoreCase(type)) {
            return "sequences";
        } else if (Constant.MESSAGE_STORE.equalsIgnoreCase(type)) {
            return "message-stores";
        } else if (Constant.MESSAGE_PROCESSOR.equalsIgnoreCase(type)) {
            return "message-processors";
        } else if ("endpointTemplate".equalsIgnoreCase(type)) {
            return "templates";
        } else if ("sequenceTemplate".equalsIgnoreCase(type)) {
            return "templates";
        } else if (Constant.TASK.equalsIgnoreCase(type)) {
            return "tasks";
        } else if (Constant.LOCAL_ENTRY.equalsIgnoreCase(type)) {
            return "local-entries";
        } else if (Constant.INBOUND_DASH_ENDPOINT.equalsIgnoreCase(type)) {
            return "inbound-endpoints";
        } else if (Constant.DATA_SERVICE.equalsIgnoreCase(type)) {
            return "data-services";
        } else if (Constant.DATA_SOURCE.equalsIgnoreCase(type)) {
            return "data-sources";
        } else if (Constant.PROXY_SERVICE.equalsIgnoreCase(type)) {
            return "proxy-services";
        }
        return null;
    }
}
