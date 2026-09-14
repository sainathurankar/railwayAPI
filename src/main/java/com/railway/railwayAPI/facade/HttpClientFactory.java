package com.railway.railwayAPI.facade;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Builds {@link RestTemplate} instances backed by a pooled Apache HttpClient 5
 * connection manager with explicit connect / read timeouts, shared by every
 * outbound redBus call so a slow upstream can neither hang a request nor
 * exhaust threads. Timeouts and pool sizes are overridable via system
 * properties (see the {@code redbus.http.*} keys).
 */
public final class HttpClientFactory {

    private static final int CONNECT_TIMEOUT_MS =
            Integer.getInteger("redbus.http.connectTimeoutMs", 5_000);
    private static final int READ_TIMEOUT_MS =
            Integer.getInteger("redbus.http.readTimeoutMs", 15_000);
    private static final int MAX_TOTAL_CONNECTIONS =
            Integer.getInteger("redbus.http.maxTotal", 50);
    private static final int MAX_CONNECTIONS_PER_ROUTE =
            Integer.getInteger("redbus.http.maxPerRoute", 20);

    private HttpClientFactory() {
    }

    public static RestTemplate pooledRestTemplate() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT_MS))
                .setSocketTimeout(Timeout.ofMilliseconds(READ_TIMEOUT_MS))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT_MS))
                .setResponseTimeout(Timeout.ofMilliseconds(READ_TIMEOUT_MS))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .evictExpiredConnections()
                .build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
