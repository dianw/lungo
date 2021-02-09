package xyz.mainapi.dashboard.core.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;

@Configuration
public class Auth0Config {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${auth0.domain}")
    private String domain;
    @Value("${auth0.client-id}")
    private String clientId;
    @Value("${auth0.client-secret}")
    private String clientSecret;
    @Value("${auth0.management-api-id}")
    private String managementApiId;
    @Value("${auth0.logging-enabled:false}")
    private boolean loggingEnabled;

    @Bean
    public AuthAPI authAPI() {
        AuthAPI authAPI = new AuthAPI(domain, clientId, clientSecret);
        authAPI.setLoggingEnabled(loggingEnabled);
        return authAPI;
    }

    @Bean
    public ManagementAPI managementAPI(AuthAPI authAPI) {
        ManagementAPI managementAPI = new ManagementAPI(domain, "");
        managementAPI.setLoggingEnabled(loggingEnabled);
        return managementAPI;
    }

    @Bean
    public ScheduleRenewAccessToken scheduleRenewAccessToken(AuthAPI authApi, ManagementAPI managementApi) {
        return new ScheduleRenewAccessToken(authApi, managementApi, managementApiId);
    }

    /**
     * refresh access token every 23h
     */
    static class ScheduleRenewAccessToken {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final AuthAPI authApi;
        private final ManagementAPI managementApi;
        private final String managementApiId;

        private ScheduleRenewAccessToken(AuthAPI authApi, ManagementAPI managementApi, String managementApiId) {
            this.authApi = authApi;
            this.managementApi = managementApi;
            this.managementApiId = managementApiId;
        }

        @Scheduled(fixedRate = 82_800_000)
        public void updateManagementAPIToken() throws Auth0Exception {
            logger.info("Refreshing management API");
            TokenRequest tokenRequest = authApi.requestToken(managementApiId);
            TokenHolder tokenHolder = tokenRequest.execute();
            managementApi.setApiToken(tokenHolder.getAccessToken());
            logger.info("Management API refreshed");
        }
    }
}
