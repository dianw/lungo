package xyz.mainapi.dashboard.core.security;

import java.time.Duration;

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

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

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

        // refresh access token every 23h
        Flux.interval(Duration.ZERO, Duration.ofHours(23))
                .doOnNext(i -> logger.info("Refreshing management API"))
                .map(i -> {
                    try {
                        return requestToken(authAPI, managementApiId);
                    } catch (Auth0Exception e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .doOnNext(i -> logger.info("Management API refreshed"))
                .retryWhen(Retry.fixedDelay(20, Duration.ofSeconds(3)))
                .map(TokenHolder::getAccessToken)
                .subscribe(managementAPI::setApiToken);

        return managementAPI;
    }

    private TokenHolder requestToken(AuthAPI authAPI, String managementApiId) throws Auth0Exception {
        TokenRequest tokenRequest = authAPI.requestToken(managementApiId);
        return tokenRequest.execute();
    }
}
