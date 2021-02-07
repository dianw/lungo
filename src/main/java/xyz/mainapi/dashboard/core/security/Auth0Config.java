package xyz.mainapi.dashboard.core.security;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;

import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

@Configuration
public class Auth0Config {
    @Value("${auth0.domain}")
    private String domain;
    @Value("${auth0.client-id}")
    private String clientId;
    @Value("${auth0.client-secret}")
    private String clientSecret;
    @Value("${auth0.management-api-id}")
    private String managementApiId;

    @Bean
    public AuthAPI authAPI() {
        return new AuthAPI(domain, clientId, clientSecret);
    }

    @Bean
    public ManagementAPI managementAPI(AuthAPI authAPI) throws InterruptedException {
        ManagementAPIHolder managementAPIHolder = new ManagementAPIHolder(authAPI, managementApiId, domain);
        while (managementAPIHolder.getManagementAPI() == null) {
            Thread.sleep(100);
        }
        return managementAPIHolder.getManagementAPI();
    }

    /**
     * Periodically refresh ManagementAPI
     */
    private static class ManagementAPIHolder {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final AuthAPI authAPI;
        private final AtomicReference<ManagementAPI> managementAPIReference = new AtomicReference<>();
        private final String audience;
        private final String domain;

        private ManagementAPIHolder(AuthAPI authAPI, String audience, String domain) {
            this.authAPI = authAPI;
            this.audience = audience;
            this.domain = domain;

            refreshPeriodically();
        }

        /**
         * Refresh every 23h
         */
        private void refreshPeriodically() {
            Flux.interval(Duration.ZERO, Duration.ofHours(23))
                    .handle((i, sink) -> {
                        try {
                            refresh();
                            sink.next(i);
                        } catch (Exception e) {
                            sink.error(e);
                        }
                    })
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(3)))
                    .subscribe();
        }


        private void refresh() throws Auth0Exception {
            logger.info("Refreshing management API");
            TokenRequest tokenRequest = authAPI.requestToken(audience);
            TokenHolder tokenHolder = tokenRequest.execute();
            ManagementAPI managementAPI = new ManagementAPI(domain, tokenHolder.getAccessToken());
            managementAPIReference.set(managementAPI);
            logger.info("Management API refreshed [{}]", tokenHolder.getAccessToken());
        }

        private ManagementAPI getManagementAPI() {
            return managementAPIReference.get();
        }
    }
}
