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
package org.eclipse.lemminx.customservice.synapse.parser.config;

import org.eclipse.lemminx.customservice.synapse.parser.ConfigDetails;
import org.eclipse.lemminx.customservice.synapse.parser.Constants;
import org.eclipse.lemminx.customservice.synapse.parser.Node;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateConfigRequest;
import org.eclipse.lemminx.customservice.synapse.parser.UpdateResponse;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigParser {

    private static final Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());

    public static List<Node> getConfigDetails(String projectUri) {

        List<Node> result = new ArrayList<>();
        File propertyFilePath = getFilePath(projectUri);
        if (isConfigFileExist(propertyFilePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(propertyFilePath))) {
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith(Constants.HASH) || line.isEmpty()) {
                        lineNumber++;
                        continue;
                    }
                    int delimiterIndex = line.indexOf(':');
                    if (delimiterIndex != -1) {
                        String key = line.substring(0, delimiterIndex).trim();
                        result.add(new Node(key, line.substring(delimiterIndex + 1).trim(),
                                Either.forLeft(new Range(
                                        new Position(lineNumber, line.indexOf(key) + 1),
                                        new Position(lineNumber, line.length() + 1)))));
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error processing the config file: " + e.getMessage());
            }
        }
        return result;
    }

    public static UpdateResponse updateConfigFile(String projectUri, UpdateConfigRequest request) {
        File propertyFilePath = getFilePath(projectUri);
        if (isConfigFileExist(propertyFilePath)) {
            try {
                List<String> fileLines = new ArrayList<>(Files.readAllLines(propertyFilePath.toPath()));
                UpdateResponse updateResponse = new UpdateResponse();
                int startLine = fileLines.size();
                for (ConfigDetails entry : request.configs) {
                    String value = entry.key + Constants.COLON + entry.value;
                    if (entry.range != null) {
                        Range range = entry.range;
                        updateResponse.add(new TextEdit(new Range(range.getStart(),
                                new Position(range.getStart().getLine(), range.getEnd().getCharacter())), value));
                    } else {
                        startLine++;
                        updateResponse.add(new TextEdit(new Range(new Position(startLine, 0),
                                new Position(startLine, value.length() + 1)), value));
                    }
                }
                return updateResponse;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error processing the config file: " + e.getMessage());
            }
        }
        return null;
    }

    public static List<ConfigurableEntry> scanConfigurableEntries(String projectPath)
            throws IOException {

        List<Node> configDetails = getConfigDetails(projectPath);
        List<ConfigurableEntry> configurableEntries = new ArrayList<>();
        for (Node configDetail : configDetails) {
            ConfigurableEntry configurableEntry = new ConfigurableEntry(configDetail.getKey(), configDetail.getValue());
            configurableEntries.add(configurableEntry);
        }
        return configurableEntries;
    }

    private static File getFilePath(String projectUri) {
        return new File(Path.of(projectUri, Constants.SRC, Constants.MAIN, Constants.WSO2_MI,
                Constants.RESOURCES, Constants.CONF ,Constants.CONFIG_FILE).toUri());
    }

    private static boolean isConfigFileExist(File propertiesFile) {
        if (!propertiesFile.exists()) {
            LOGGER.log(Level.SEVERE, "Config file does not exist: " + propertiesFile.getAbsolutePath());
            return false;
        }
        return true;
    }
}
