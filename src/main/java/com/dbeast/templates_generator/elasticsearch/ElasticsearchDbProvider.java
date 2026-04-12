package com.dbeast.templates_generator.elasticsearch;

import com.dbeast.templates_generator.app_settings.AppSettingsPOJO;
import com.dbeast.templates_generator.data_warehouse.DataWarehouse;
import com.dbeast.templates_generator.exceptions.ClusterConnectionException;
import com.dbeast.templates_generator.templates_generator.pojo.EsSettingsPOJO;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class ElasticsearchDbProvider {
    private static final Logger logger = LogManager.getLogger();
    private AppSettingsPOJO appSettings = DataWarehouse.getInstance().getAppSettings();

    public ElasticsearchDbProvider() {
    }

    public ElasticsearchDbProvider(AppSettingsPOJO appSettings) {
        this.appSettings = appSettings;
    }

    public RestHighLevelClient getHighLevelClient(final EsSettingsPOJO connectionSettings,
                                                  final String projectId) throws ClusterConnectionException {
        RestClientBuilder clientBuilder = buildLowLevelRestClient(connectionSettings);
        if (connectionSettings.isSsl_enabled() && connectionSettings.isAuthentication_enabled()) {
            clientBuilder = addSslToClientBuilder(connectionSettings, clientBuilder, projectId);
        } else if (connectionSettings.isAuthentication_enabled()) {
            clientBuilder = addBasicAuthenticationToClientBuilder(connectionSettings, clientBuilder);
        } else {
            // Plain connection - still apply proxy if configured
            clientBuilder = addProxyToClientBuilder(clientBuilder);
        }
        return new RestHighLevelClient(clientBuilder);
    }

    public RestHighLevelClient getHighLevelClient(final EsSettingsPOJO connectionSettings) throws ClusterConnectionException {
        RestClientBuilder clientBuilder = buildLowLevelRestClient(connectionSettings);
        if (connectionSettings.isSsl_enabled() && connectionSettings.isAuthentication_enabled()) {
            clientBuilder = addSslToClientBuilder(connectionSettings, clientBuilder, connectionSettings.getSsl_file());
        } else if (connectionSettings.isAuthentication_enabled()) {
            clientBuilder = addBasicAuthenticationToClientBuilder(connectionSettings, clientBuilder);
        } else {
            // Plain connection - still apply proxy if configured
            clientBuilder = addProxyToClientBuilder(clientBuilder);
        }
        return new RestHighLevelClient(clientBuilder);
    }

    public RestClient getLowLevelClient(final EsSettingsPOJO connectionSettings,
                                        final String projectId) throws ClusterConnectionException {
        return getHighLevelClient(connectionSettings, projectId).getLowLevelClient();
    }

    private RestClientBuilder buildLowLevelRestClient(final EsSettingsPOJO connectionSettings) {
        ESHostPOJO esHost = new ESHostPOJO(connectionSettings);
        RestClientBuilder builder = RestClient.builder(new HttpHost(
                esHost.getDomain(),
                esHost.getPort(),
                esHost.getProtocol()));

        // Set request configuration with proper timeouts for proxy connections
        builder.setRequestConfigCallback(requestConfigBuilder ->
            requestConfigBuilder
                .setConnectTimeout(30000)  // 30 seconds connection timeout
                .setSocketTimeout(60000)   // 60 seconds socket timeout
                .setConnectionRequestTimeout(30000)  // 30 seconds connection request timeout
        );

        return builder;
    }

    private RestClientBuilder addSslToClientBuilder(final EsSettingsPOJO connectionSettings,
                                                    final RestClientBuilder clientBuilder, String sslFile) throws ClusterConnectionException {
        if (connectionSettings.getSsl_file() != null) {
            try {
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(connectionSettings.getUsername(), connectionSettings.getPassword()));
                Path caCertificatePath = Paths.get(sslFile);
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                Certificate trustedCa;
                try (InputStream is = Files.newInputStream(caCertificatePath)) {
                    trustedCa = factory.generateCertificate(is);
                }
                KeyStore trustStore = KeyStore.getInstance("pkcs12");
                trustStore.load(null, null);
                trustStore.setCertificateEntry("ca", trustedCa);
                SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                        .loadTrustMaterial(trustStore, null);
                final SSLContext sslContext = sslContextBuilder.build();

                clientBuilder.setRequestConfigCallback(requestConfigBuilder ->
                    requestConfigBuilder
                        .setConnectTimeout(30000)
                        .setSocketTimeout(60000)
                        .setConnectionRequestTimeout(30000)
                );

                clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        // Set up proxy routing if configured
                        if (appSettings != null && appSettings.getApp() != null) {
                            String proxyHost = appSettings.getApp().getHttp_proxy();
                            if (proxyHost != null && !proxyHost.isEmpty() && !proxyHost.equalsIgnoreCase("null")) {
                                int proxyPort = 8080;
                                if (proxyHost.contains(":")) {
                                    String[] parts = proxyHost.split(":");
                                    proxyHost = parts[0];
                                    try {
                                        proxyPort = Integer.parseInt(parts[1]);
                                    } catch (NumberFormatException e) {
                                        logger.warn("Invalid proxy port in configuration");
                                    }
                                }

                                final String finalProxyHost = proxyHost;
                                final int finalProxyPort = proxyPort;

                                HttpHost proxy = new HttpHost(finalProxyHost, finalProxyPort, "http");
                                HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                                httpClientBuilder.setRoutePlanner(routePlanner);

                                // Add proxy credentials if available (to the specific proxy scope)
                                String proxyUser = appSettings.getApp().getProxy_user();
                                String proxyPassword = appSettings.getApp().getProxy_password();
                                if (proxyUser != null && !proxyUser.isEmpty() && !proxyUser.equalsIgnoreCase("null")
                                        && proxyPassword != null && !proxyPassword.isEmpty() && !proxyPassword.equalsIgnoreCase("null")) {
                                    credentialsProvider.setCredentials(
                                            new AuthScope(finalProxyHost, finalProxyPort),
                                            new UsernamePasswordCredentials(proxyUser, proxyPassword)
                                    );
                                }
                            }
                        }

                        return httpClientBuilder.setSSLContext(sslContext)
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setSSLHostnameVerifier((s, sslSession) -> true);
                    }
                });
                return clientBuilder;
            } catch (NoSuchAlgorithmException | KeyManagementException | IOException | CertificateException | KeyStoreException e) {
                logger.warn("error in the connection to the cluster\n" + e);
                throw new ClusterConnectionException(e.getMessage(), e);
            }
        } else {
            return addSslToClientBuilder(connectionSettings, clientBuilder);
        }
    }

    private RestClientBuilder addSslToClientBuilder(final EsSettingsPOJO connectionSettings,
                                                    final RestClientBuilder clientBuilder) throws ClusterConnectionException {
        try {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(connectionSettings.getUsername(), connectionSettings.getPassword()));
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{UnsafeX509ExtendedTrustManager.INSTANCE}, null);

            clientBuilder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                    .setConnectTimeout(30000)
                    .setSocketTimeout(60000)
                    .setConnectionRequestTimeout(30000)
            );

            clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                        HttpAsyncClientBuilder httpClientBuilder) {
                    // Set up proxy routing if configured
                    if (appSettings != null && appSettings.getApp() != null) {
                        String proxyHost = appSettings.getApp().getHttp_proxy();
                        if (proxyHost != null && !proxyHost.isEmpty() && !proxyHost.equalsIgnoreCase("null")) {
                            int proxyPort = 8080;
                            if (proxyHost.contains(":")) {
                                String[] parts = proxyHost.split(":");
                                proxyHost = parts[0];
                                try {
                                    proxyPort = Integer.parseInt(parts[1]);
                                } catch (NumberFormatException e) {
                                    logger.warn("Invalid proxy port in configuration");
                                }
                            }

                            final String finalProxyHost = proxyHost;
                            final int finalProxyPort = proxyPort;

                            HttpHost proxy = new HttpHost(finalProxyHost, finalProxyPort, "http");
                            HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                            httpClientBuilder.setRoutePlanner(routePlanner);

                            // Add proxy credentials if available (to the specific proxy scope)
                            String proxyUser = appSettings.getApp().getProxy_user();
                            String proxyPassword = appSettings.getApp().getProxy_password();
                            if (proxyUser != null && !proxyUser.isEmpty() && !proxyUser.equalsIgnoreCase("null")
                                    && proxyPassword != null && !proxyPassword.isEmpty() && !proxyPassword.equalsIgnoreCase("null")) {
                                credentialsProvider.setCredentials(
                                        new AuthScope(finalProxyHost, finalProxyPort),
                                        new UsernamePasswordCredentials(proxyUser, proxyPassword)
                                );
                            }
                        }
                    }

                    return httpClientBuilder.setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(credentialsProvider)
                            //This supposed to disable certificate verification
                            .setSSLHostnameVerifier((s, sslSession) -> true);
                }
            });
            return clientBuilder;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.warn("error in the connection to the cluster\n" + e);
            throw new ClusterConnectionException(e.getMessage(), e);
        }
    }

    private RestClientBuilder addBasicAuthenticationToClientBuilder(final EsSettingsPOJO connectionSettings,
                                                                    final RestClientBuilder clientBuilder) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // Set Elasticsearch credentials for ANY scope (matches any server)
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(connectionSettings.getUsername(), connectionSettings.getPassword()));

        clientBuilder.setRequestConfigCallback(requestConfigBuilder ->
            requestConfigBuilder
                .setConnectTimeout(30000)
                .setSocketTimeout(60000)
                .setConnectionRequestTimeout(30000)
        );

        clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                // Set up proxy routing if configured
                if (appSettings != null && appSettings.getApp() != null) {
                    String proxyHost = appSettings.getApp().getHttp_proxy();
                    if (proxyHost != null && !proxyHost.isEmpty() && !proxyHost.equalsIgnoreCase("null")) {
                        int proxyPort = 8080;
                        if (proxyHost.contains(":")) {
                            String[] parts = proxyHost.split(":");
                            proxyHost = parts[0];
                            try {
                                proxyPort = Integer.parseInt(parts[1]);
                            } catch (NumberFormatException e) {
                                logger.warn("Invalid proxy port in configuration");
                            }
                        }

                        final String finalProxyHost = proxyHost;
                        final int finalProxyPort = proxyPort;

                        HttpHost proxy = new HttpHost(finalProxyHost, finalProxyPort, "http");
                        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                        httpClientBuilder.setRoutePlanner(routePlanner);

                        // Add proxy credentials if available (to the specific proxy scope)
                        String proxyUser = appSettings.getApp().getProxy_user();
                        String proxyPassword = appSettings.getApp().getProxy_password();
                        if (proxyUser != null && !proxyUser.isEmpty() && !proxyUser.equalsIgnoreCase("null")
                                && proxyPassword != null && !proxyPassword.isEmpty() && !proxyPassword.equalsIgnoreCase("null")) {
                            credentialsProvider.setCredentials(
                                    new AuthScope(finalProxyHost, finalProxyPort),
                                    new UsernamePasswordCredentials(proxyUser, proxyPassword)
                            );
                            logger.debug("Proxy authentication configured for {}:{}", finalProxyHost, finalProxyPort);
                        }
                    }
                }

                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });
        return clientBuilder;
    }

    private RestClientBuilder addProxyToClientBuilder(final RestClientBuilder clientBuilder) {
        logger.info("Attempting to configure proxy for plain connection");

        if (appSettings == null) {
            logger.warn("AppSettings is NULL - proxy cannot be configured. Create provider with: new ElasticsearchDbProvider(appSettings)");
            return clientBuilder;
        }

        if (appSettings.getApp() == null) {
            logger.warn("AppSettings.App is NULL - proxy cannot be configured");
            return clientBuilder;
        }

        String proxyHost = appSettings.getApp().getHttp_proxy();
        int proxyPort = 0;
        final String proxyUser = appSettings.getApp().getProxy_user();
        final String proxyPassword = appSettings.getApp().getProxy_password();

        logger.info("Proxy config - host: {}, user: {}, password: {}",
            proxyHost, proxyUser, (proxyPassword != null && !proxyPassword.isEmpty() ? "***" : "null"));

        // Try to parse proxy host and port
        if (proxyHost != null && !proxyHost.isEmpty() && !proxyHost.equalsIgnoreCase("null")) {
            logger.info("Proxy host is configured: {}", proxyHost);

            // Parse proxy host and port (format: host:port)
            if (proxyHost.contains(":")) {
                String[] parts = proxyHost.split(":");
                proxyHost = parts[0];
                try {
                    proxyPort = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid proxy port in configuration: " + parts[1]);
                    return clientBuilder;
                }
            } else {
                proxyPort = 8080; // Default proxy port
            }

            final String finalProxyHost = proxyHost;
            final int finalProxyPort = proxyPort;

            // Check if proxy credentials are provided
            final boolean hasProxyAuth = proxyUser != null && !proxyUser.isEmpty() && !proxyUser.equalsIgnoreCase("null")
                    && proxyPassword != null && !proxyPassword.isEmpty() && !proxyPassword.equalsIgnoreCase("null");

            logger.info("Configuring proxy: {}:{}, auth enabled: {}", finalProxyHost, finalProxyPort, hasProxyAuth);

            clientBuilder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                    .setConnectTimeout(30000)  // 30 seconds for proxy connection
                    .setSocketTimeout(60000)   // 60 seconds for socket
                    .setConnectionRequestTimeout(30000)
            );

            clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    HttpHost proxy = new HttpHost(finalProxyHost, finalProxyPort, "http");
                    HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                    httpClientBuilder.setRoutePlanner(routePlanner);

                    logger.info("Proxy route planner configured for {}:{}", finalProxyHost, finalProxyPort);

                    // Add proxy credentials ONLY if provided
                    if (hasProxyAuth) {
                        CredentialsProvider proxyCredentialsProvider = new BasicCredentialsProvider();
                        // Set credentials for the proxy itself (not for target Elasticsearch)
                        proxyCredentialsProvider.setCredentials(
                                new AuthScope(finalProxyHost, finalProxyPort),
                                new UsernamePasswordCredentials(proxyUser, proxyPassword)
                        );
                        httpClientBuilder.setDefaultCredentialsProvider(proxyCredentialsProvider);
                        logger.info("Proxy authentication configured for user: {}", proxyUser);
                    } else {
                        logger.debug("No proxy credentials configured - using anonymous proxy access");
                    }

                    return httpClientBuilder;
                }
            });
        } else {
            logger.warn("Proxy not configured: http_proxy is null or empty in configuration");
        }

        return clientBuilder;
    }

    public void closeConnection() {
    }
}



