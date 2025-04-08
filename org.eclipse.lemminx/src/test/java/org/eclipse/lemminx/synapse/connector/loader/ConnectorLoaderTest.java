/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.synapse.connector.loader;

import org.apache.commons.io.FileUtils;
import org.eclipse.lemminx.MockXMLLanguageClient;
import org.eclipse.lemminx.customservice.SynapseLanguageClientAPI;
import org.eclipse.lemminx.customservice.synapse.InvalidConfigurationException;
import org.eclipse.lemminx.customservice.synapse.connectors.ConnectorHolder;
import org.eclipse.lemminx.customservice.synapse.inbound.conector.InboundConnectorHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConnectorLoaderTest {

    private ConnectorHolder connectorHolder;
    private MockConnectorLoader connectorLoader;
    private Path tempPath;

    @BeforeAll
    public void setUp() throws IOException {

        connectorHolder = ConnectorHolder.getInstance();
        connectorHolder.clearConnectors();
        InboundConnectorHolder inboundConnectorHolder = new InboundConnectorHolder();
        SynapseLanguageClientAPI mockLanguageClient = new MockXMLLanguageClient();
        tempPath = Files.createTempDirectory("mi-language-server-test-");
        connectorLoader =
                new MockConnectorLoader(mockLanguageClient, connectorHolder, inboundConnectorHolder, tempPath);
    }

    @Test
    @Order(0)
    public void testConnectorLoaderInit_WithNullProject() {

        assertThrowsExactly(InvalidConfigurationException.class, () -> connectorLoader.init(null),
                "Project root should be null");
    }

    @Test
    @Order(0)
    public void testConnectorLoaderInit_WithInvalidProject() {

        String path = Objects.requireNonNull(this.getClass().getResource("/synapse/invalid.project")).getPath();

        assertThrowsExactly(InvalidConfigurationException.class, () -> connectorLoader.init(path),
                "Project root should be invalid");
    }

    @Test
    @Order(1)
    public void testConnectorLoaderInit_WithValidProject() {

        String path = Objects.requireNonNull(this.getClass().getResource("/synapse/pom.parser/test_pom_parser"))
                .getPath();

        assertDoesNotThrow(() -> connectorLoader.init(path), "Project root should be valid");
    }

    @Test
    @Order(2)
    public void testConnectorLoading_WithNoConnectors() {

        connectorLoader.loadConnector();

        assertEquals(0, connectorHolder.getConnectors().size());
    }

    @Test
    @Order(3)
    public void testConnectorLoading_AddingValidConnector() throws IOException {

        String connectorPath = Objects.requireNonNull(
                this.getClass().getResource("/synapse/connectors/zips/mi-connector-http-0.1.8.zip")).getPath();
        loadConnector(connectorPath);

        assertEquals(1, connectorHolder.getConnectors().size());
        assertNotNull(connectorHolder.getConnector("http"));
    }

    @Test
    @Order(4)
    public void testConnectorLoading_AddingInvalidConnector() throws IOException {

        String connectorPath = Objects.requireNonNull(
                this.getClass().getResource("/synapse/connectors/zips/invalid-connector-0.1.0.zip")).getPath();
        loadConnector(connectorPath);

        assertEquals(1, connectorHolder.getConnectors().size());
        assertNotNull(connectorHolder.getConnector("http"));
    }

    @Test
    @Order(5)
    public void testConnectorLoading_AddingAnotherValidConnector() throws IOException {

        String connectorPath = Objects.requireNonNull(
                this.getClass().getResource("/synapse/connectors/zips/mi-connector-file-4.0.36.zip")).getPath();
        loadConnector(connectorPath);

        assertEquals(2, connectorHolder.getConnectors().size());
        assertNotNull(connectorHolder.getConnector("http"));
        assertNotNull(connectorHolder.getConnector("file"));
    }

    @Test
    @Order(6)
    public void testConnectorLoading_RemovingInvalidConnector() throws IOException {

        FileUtils.forceDelete(tempPath.resolve("connectors").resolve("invalid-connector-0.1.0.zip").toFile());
        connectorLoader.loadConnector();

        assertEquals(2, connectorHolder.getConnectors().size());
        assertNotNull(connectorHolder.getConnector("http"));
        assertNotNull(connectorHolder.getConnector("file"));
    }

    @Test
    @Order(7)
    public void testConnectorLoading_RemovingValidConnector() throws IOException {

        FileUtils.forceDelete(tempPath.resolve("connectors").resolve("mi-connector-http-0.1.8.zip").toFile());
        connectorLoader.loadConnector();

        assertEquals(1, connectorHolder.getConnectors().size());
        assertNotNull(connectorHolder.getConnector("file"));
    }

    @Test
    @Order(8)
    public void testConnectorLoading_AddValidAndInvalidConnectorAtSameTime() throws IOException {

        String connectorPath1 = Objects.requireNonNull(
                this.getClass().getResource("/synapse/connectors/zips/invalid-connector-0.1.0.zip")).getPath();
        String connectorPath2 = Objects.requireNonNull(
                this.getClass().getResource("/synapse/connectors/zips/mi-connector-http-0.1.8.zip")).getPath();
        loadConnector(connectorPath1, connectorPath2);

        assertEquals(2, connectorHolder.getConnectors().size());
        assertNotNull(connectorHolder.getConnector("http"));
        assertNotNull(connectorHolder.getConnector("file"));
    }

    private void loadConnector(String... connectorPaths) throws IOException {

        for (String path : connectorPaths) {
            FileUtils.copyFileToDirectory(new File(path), tempPath.resolve("connectors").toFile());
        }
        connectorLoader.loadConnector();
    }
}
