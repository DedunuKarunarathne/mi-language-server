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

package org.eclipse.lemminx.customservice.synapse.connectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class to load connectors for new projects.
 */
public class NewProjectConnectorLoader extends AbstractConnectorLoader {

    private static final Logger log = Logger.getLogger(NewProjectConnectorLoader.class.getName());
    private String projectId;

    public NewProjectConnectorLoader(SynapseLanguageClientAPI languageClient, ConnectorHolder connectorHolder,
                                     InboundConnectorHolder inboundConnectorHolder) {

        super(languageClient, connectorHolder, inboundConnectorHolder);
    }

    @Override
    protected File getConnectorExtractFolder() {

        String tempFolderPath = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                Constant.CONNECTORS, projectId, Constant.EXTRACTED).toString();
        File tempFolder = new File(tempFolderPath);
        return tempFolder;
    }

    @Override
    protected void copyToProjectIfNeeded(List<File> connectorZips) {

        if (!Utils.isOlderCARPlugin(projectUri)) {
            return;
        }
        File downloadedConnectorsFolder = getConnnectorDownloadPath().toFile();
        File projectConnectorPath = Path.of(projectUri).resolve(TryOutConstants.PROJECT_CONNECTOR_PATH).toFile();
        if (downloadedConnectorsFolder.exists()) {
            File[] downloadedConnectors = downloadedConnectorsFolder.listFiles();
            for (File downloadedConnector : downloadedConnectors) {
                boolean isExists = FileUtils.getFile(projectConnectorPath, downloadedConnector.getName()).exists();
                if (!isExists) {
                    try {
                        FileUtils.copyFileToDirectory(downloadedConnector, projectConnectorPath);
                    } catch (IOException e) {
                        log.log(Level.WARNING, "Failed to copy connector to project", e);
                    }
                }
            }
        }
    }

    @Override
    protected boolean canContinue(File connectorExtractFolder) {

        try {
            if (!connectorExtractFolder.exists()) {
                connectorExtractFolder.mkdirs();
            }
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to create connector extract folder", e);
            return false;
        }
    }

    @Override
    protected void cleanOldConnectors(File connectorExtractFolder, List<File> connectorZips) {

        File[] tempFiles = connectorExtractFolder.listFiles();
        List<String> tempConnectorNames =
                Arrays.stream(tempFiles).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
        for (String connectorName : tempConnectorNames) {
            boolean isConnectorAvailable =
                    connectorZips.stream().anyMatch(file -> file.getName().contains(connectorName));
            if (!isConnectorAvailable) {
                File connectorFolder =
                        new File(connectorExtractFolder.getAbsolutePath() + File.separator + connectorName);
                connectorHolder.removeConnector(getConnectorName(connectorFolder));
                try {
                    if (connectorFolder.getName().contains(Constant.INBOUND_CONNECTOR_PREFIX) ) {
                        String schema = Utils.readFile(connectorFolder.toPath().resolve(Constant.RESOURCES)
                                .resolve(Constant.UI_SCHEMA_JSON).toFile());
                        String fileName = Utils.getJsonObject(schema).get(Constant.NAME).getAsString() + Constant.JSON_FILE_EXT;
                        String projectFolderName = connectorExtractFolder.getParentFile().getName();
                        File schemaToRemove = Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                                Constant.INBOUND_CONNECTORS).resolve(projectFolderName).resolve(fileName).toFile();
                        FileUtils.delete(schemaToRemove);
                    }
                    FileUtils.deleteDirectory(connectorFolder);
                    notifyRemoveConnector(connectorName, true, "Connector deleted successfully");
                } catch (IOException e) {
                    log.log(Level.WARNING, "Failed to delete connector folder:" + connectorName, e);
                }
            }
        }
    }

    private Path getConnnectorDownloadPath() {

        return Path.of(System.getProperty(Constant.USER_HOME), Constant.WSO2_MI,
                Constant.CONNECTORS, projectId, Constant.DOWNLOADED);
    }

    @Override
    protected void setConnectorsZipFolderPath(String projectRoot) {

        connectorsZipFolderPath.add(Path.of(projectRoot, Constant.SRC, Constant.MAIN, Constant.WSO2MI,
                Constant.RESOURCES, Constant.CONNECTORS).toString());
        projectId = new File(projectRoot).getName() + "_" + Utils.getHash(projectRoot);
        connectorsZipFolderPath.add(getConnnectorDownloadPath().toString());
    }
}
