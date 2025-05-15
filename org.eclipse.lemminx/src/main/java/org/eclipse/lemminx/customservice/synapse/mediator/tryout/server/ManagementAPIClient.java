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

package org.eclipse.lemminx.customservice.synapse.mediator.tryout.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.mediator.TryOutConstants;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.DeployedArtifactType;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ManagementAPIClient {

    private static final Logger LOGGER = Logger.getLogger(ManagementAPIClient.class.getName());
    private static final int DEFAULT_PORT = 9164;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private ObjectMapper objectMapper;
    private HttpClient client;
    private static final String HOST = TryOutConstants.LOCALHOST;
    private int port = DEFAULT_PORT;
    private String accessToken;
    private boolean isRetried = false;

    public ManagementAPIClient() {

        try {
            objectMapper = new ObjectMapper();
            init();
            connect();
        } catch (NoSuchAlgorithmException | KeyManagementException | InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Failed to initialize the client: " + e.getMessage());
        }
    }

    public void init() throws NoSuchAlgorithmException, KeyManagementException {

        // Create SSL context that ignores certificate verification
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {

                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // No need to check client certificate
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // No need to check server certificate
            }
        }}, new SecureRandom());

        // Create HttpClient with SSL context
        client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }

    public void connect() throws InterruptedException {

        try {

            // Create Basic Auth token
            String token = Base64.getEncoder().encodeToString(
                    (USERNAME + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + token;

            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("https://%s:%d/management/login", HOST, port)))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            // Parse the response to get the access token
            JsonNode responseBody = objectMapper.readTree(response.body());
            accessToken = responseBody.get("AccessToken").asText();
        } catch (IOException e) {
            LOGGER.severe("Failed to connect to the server: " + e.getMessage());
        }
    }

    public List<String> getDeployedCapps() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://%s:%d/management/%s", HOST, port, "applications")))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            isRetried = false;
            return extractDeployedCapps(response.body());
        } else if (response.statusCode() == 401 && !isRetried) {
            isRetried = true;
            connect();
            return getDeployedCapps();
        }
        return Collections.emptyList();
    }

    private List<String> extractDeployedCapps(String body) {

        JsonObject jsonObject = Utils.getJsonObject(body);
        if (jsonObject != null) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("activeList");
            if (jsonArray != null) {
                List<String> capps = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    capps.add(jsonArray.get(i).getAsJsonObject().get("name").getAsString());
                }
                return capps;
            }
        }
        return Collections.emptyList();
    }

    public List<DeployedArtifact> getArtifacts(DeployedArtifactType type) throws IOException, InterruptedException {

        // Build the request URL using the passed endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://%s:%d/management/%s", HOST,
                        port, type.toString().toLowerCase())))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the status code and handle the response
        if (response.statusCode() == 200) {
            isRetried = false;
            return extractDeployedArtifacts(response.body());
        } else if (response.statusCode() == 401 && !isRetried) {
            isRetried = true;
            connect();
            return getArtifacts(type);
        }
        LOGGER.severe("Failed to get artifacts: " + response.body());
        return Collections.emptyList();
    }

    public List<DeployedArtifact> extractDeployedArtifacts(String jsonString) throws IOException {

        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode listNode = rootNode.get("list");

        List<DeployedArtifact> nameUrlPairs = new ArrayList<>();
        if (listNode.isArray()) {
            for (JsonNode node : listNode) {
                String name = node.get("name").asText();
                String url = null;
                if (node.has("url")) {
                    url = node.get("url").asText();
                }
                nameUrlPairs.add(new DeployedArtifact(name, url));
            }
        }
        return nameUrlPairs;
    }

    protected static class DeployedArtifact {

        private String name;
        private String url;

        public DeployedArtifact(String name, String url) {

            this.name = name;
            this.url = url;
        }

        public String getName() {

            return name;
        }

        public String getUrl() {

            return url;
        }

        @Override
        public String toString() {

            return "DeployedArtifact{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
