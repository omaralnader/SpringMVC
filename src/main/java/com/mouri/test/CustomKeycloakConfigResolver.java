package com.mouri.test;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class CustomKeycloakConfigResolver implements KeycloakConfigResolver {

    /**
     * Field keycloakDeployment.
     */
    private KeycloakDeployment keycloakDeployment;

    /**
     * Field adapterConfig.
     */
    private AdapterConfig adapterConfig;

    /**
     * Determine the keycloakConfigFileResource according to the environment.
     */
    public void initKeycloakResourceFile() throws Exception {

        String keycloakConfigFileName = "keycloak-local.json";
//        if (EnvironmentUtil.isProd()) {
//            keycloakConfigFileName = "keycloak-prod.json";
//        } else if (EnvironmentUtil.isUat()) {
//            keycloakConfigFileName = "keycloak-uat.json";
//        } else if (EnvironmentUtil.isQa()) {
//            keycloakConfigFileName = "keycloak-qa.json";
//        } else if (EnvironmentUtil.isDev()) {
//            keycloakConfigFileName = "keycloak-dev.json";
//        }

        // Create the resource.
        Resource keycloakConfigFileResource = new ClassPathResource(keycloakConfigFileName);

        adapterConfig = KeycloakDeploymentBuilder.loadAdapterConfig(keycloakConfigFileResource.getInputStream());
        keycloakDeployment = KeycloakDeploymentBuilder.build(adapterConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeycloakDeployment resolve(HttpFacade.Request facade) {
        return keycloakDeployment;
    }

    /**
     * Retrieve the adaptor config.
     *
     * @return The adaptor config
     */
    public AdapterConfig getAdapterConfig() {
        return adapterConfig;
    }
    
}
