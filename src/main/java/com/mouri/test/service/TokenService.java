package com.mouri.test.service;

import java.io.IOException;

import org.keycloak.OAuth2Constants;
import org.keycloak.common.util.Time;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TokenService {

    /** the default validity for a token before expiration. */
    private static final long DEFAULT_MIN_VALIDITY = 30;

    /** the keycloak API path. */
    private static final String ACCESS_TOKEN_PATH = "realms/individual/protocol/openid-connect/token";

    /** Keycloak adapterConfig. */
    private final AdapterConfig adapterConfig;

    /** the current in-scope token. */
    private AccessTokenResponse currentToken;

    /** the time the current token expires. */
    private long expirationTime;

    /** the minimum token validity. */
    private static final long MIN_TOKEN_VALIDITY = DEFAULT_MIN_VALIDITY;

    /** rest template. */
    private final RestTemplate restTemplate;

    /**
     * TokenService Constructor.
     *
     * @param adapterConfig The adapter config
     * @param restTemplate The rest template
     *
     */
    public TokenService(final AdapterConfig adapterConfig, final RestTemplate restTemplate) {
        this.adapterConfig = adapterConfig;
        this.restTemplate = restTemplate;
    }

    /**
     * Returns the access token as a string.
     *
     * @return - the access token
     * @throws IOException error
     */
    public String getAccessTokenString() throws IOException {
        return getAccessToken().getToken();
    }

    /**
     * Brokers the request to get an access token.
     *
     * @return a valid accessToken response
     * @throws IOException error
     */
    public AccessTokenResponse getAccessToken() throws IOException {
    	synchronized (this) {
	        if (currentToken == null || tokenExpired()) {
	            grantToken();
	        } else if (shouldRefresh()) {
	            refreshToken();
	        }
    	}
        return currentToken;
    }

    /**
     * Brokers the grant token request.
     *
     * @throws IOException error
     */
    private void grantToken() throws IOException {
        MultiValueMap<String, String> nvps = new LinkedMultiValueMap<>();
        nvps.add(KeycloakConstants.CLIENT_ID_PROP, adapterConfig.getResource());
        nvps.add(KeycloakConstants.CLIENT_SECRET_PROP, (String) adapterConfig.getCredentials().get("secret"));
        nvps.add(KeycloakConstants.GRANT_TYPE_PROP, OAuth2Constants.CLIENT_CREDENTIALS);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> httpEntity = new HttpEntity<>(nvps, headers);
        int requestTime = Time.currentTime();
        ResponseEntity<AccessTokenResponse> resp = restTemplate
            .postForEntity(adapterConfig.getAuthServerUrl() + ACCESS_TOKEN_PATH, httpEntity, AccessTokenResponse.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            currentToken = resp.getBody();
            expirationTime = requestTime + currentToken.getExpiresIn();
        }
    }

    /**
     * Brokers the refresh token request.
     *
     * @throws IOException error
     */
    private void refreshToken() throws IOException {
        MultiValueMap<String, String> nvps = new LinkedMultiValueMap<>();
        nvps.add(KeycloakConstants.CLIENT_ID_PROP, adapterConfig.getResource());
        nvps.add(KeycloakConstants.CLIENT_SECRET_PROP, (String) adapterConfig.getCredentials().get("secret"));
        nvps.add(KeycloakConstants.GRANT_TYPE_PROP, KeycloakConstants.REFRESH_TOKEN_PROP);
        nvps.add(KeycloakConstants.REFRESH_TOKEN_PROP, currentToken.getRefreshToken());

        int requestTime = Time.currentTime();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<Object> httpEntity = new HttpEntity<>(nvps, headers);
        ResponseEntity<AccessTokenResponse> resp = restTemplate
            .postForEntity(adapterConfig.getAuthServerUrl() + ACCESS_TOKEN_PATH, httpEntity, AccessTokenResponse.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            currentToken = resp.getBody();
            expirationTime = requestTime + currentToken.getExpiresIn();
        } else {
            // Failed, try to get a new token.
            grantToken();
        }
    }

    /**
     * Determines whether to refresh the current token.
     *
     * @return true/false
     */
    private boolean shouldRefresh() {
        return (Time.currentTime() + MIN_TOKEN_VALIDITY) >= expirationTime - 10;
    }

    /**
     * Determines whether the current token has expired.
     *
     * @return true/false
     */
    private boolean tokenExpired() {
        return (Time.currentTime() + MIN_TOKEN_VALIDITY) >= expirationTime;
    }

    /**
     * Invalidates the current token, but only when it is equal to the token passed as an argument.
     *
     * @param token the token to invalidate (cannot be null)
     */
    public void invalidate(String token) {
    	synchronized (this) {
	        if (currentToken == null) {
	            return; // There's nothing to invalidate.
	        }
	        if (token.equals(currentToken.getToken())) {
	            // When used next, this cause a refresh attempt, that in turn will cause a grant attempt if refreshing
	            // fails.
	            expirationTime = -1;
	        }
    	}
    }
}