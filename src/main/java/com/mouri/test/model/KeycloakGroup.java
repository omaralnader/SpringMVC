package com.mouri.test.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Keycloak Group Object.
 *
 * @author LunnD
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "path", "clientRoles", "subGroups"})
public class KeycloakGroup implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -7925996188941075096L;

    /** The id. */
    @JsonProperty("id")
    private String id;

    /** The name. */
    @JsonProperty("name")
    private String name;

    /** The path. */
    @JsonProperty("path")
    private String path;

    /** The client roles. */
    @JsonProperty("clientRoles")
    private Map<String, List<String>> clientRoles;

    /** The sub groups. */
    @JsonProperty("subGroups")
    private List<KeycloakGroup> subGroups = null;

    /** The additional properties. */
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * Gets the id.
     *
     * @return the id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the new path
     */
    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the sub groups.
     *
     * @return the sub groups
     */
    @JsonProperty("subGroups")
    public List<KeycloakGroup> getSubGroups() {
        return subGroups;
    }

    /**
     * Sets the sub groups.
     *
     * @param subGroups the new sub groups
     */
    @JsonProperty("subGroups")
    public void setSubGroups(List<KeycloakGroup> subGroups) {
        this.subGroups = subGroups;
    }

    /**
     * Gets the additional properties.
     *
     * @return the additional properties
     */
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * Sets the additional property.
     *
     * @param name the name
     * @param value the value
     */
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * Get the roles that belong to the given client.
     *
     * @param clientName The client name
     *
     * @return List of roles
     */
    public List<String> getClientRoles(String clientName) {
        if (clientRoles == null || clientRoles.get(clientName) == null) {
            return new ArrayList<>();
        }
        return clientRoles.get(clientName);
    }

    /**
     * @return the clientRoles
     */
    public Map<String, List<String>> getClientRoles() {
        return clientRoles;
    }

    /**
     * @param clientRoles the clientRoles to set
     */
    public void setClientRoles(Map<String, List<String>> clientRoles) {
        this.clientRoles = clientRoles;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        // Sonar fix
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        KeycloakGroup other = (KeycloakGroup) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}