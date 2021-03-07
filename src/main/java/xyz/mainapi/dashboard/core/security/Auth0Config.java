package xyz.mainapi.dashboard.core.security;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    @Bean
    public AuthAPI authAPI(Auth0ConfigProperties props) {
        AuthAPI authAPI = new AuthAPI(props.getDomain(), props.getClientId(), props.getClientSecret());
        authAPI.setLoggingEnabled(props.getLoggingEnabled());
        return authAPI;
    }

    @Bean
    public ManagementAPI managementAPI(Auth0ConfigProperties props) {
        ManagementAPI managementAPI = new ManagementAPI(props.getDomain(), "");
        managementAPI.setLoggingEnabled(props.getLoggingEnabled());
        return managementAPI;
    }

    @Bean
    public ScheduleRenewAccessToken scheduleRenewAccessToken(AuthAPI authApi, ManagementAPI managementApi, Auth0ConfigProperties props) {
        return new ScheduleRenewAccessToken(authApi, managementApi, props.getManagementApiId());
    }

    @Bean
    @ConfigurationProperties("auth0")
    public Auth0ConfigProperties auth0ConfigProperties() {
        return ModifiableAuth0ConfigProperties.create();
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

    @Value.Modifiable
    interface Auth0ConfigProperties {
        String getDomain();

        String getClientId();

        String getClientSecret();

        String getManagementApiId();

        @Value.Default
        default boolean getLoggingEnabled() {
            return false;
        }
    }
}
