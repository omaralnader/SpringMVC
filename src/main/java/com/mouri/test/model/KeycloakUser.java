package com.mouri.test.model;

import java.io.Serializable;
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
 * The Class KeycloakUser.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "createdTimestamp", "username", "enabled", "totp", "emailVerified", "firstName", "lastName",
    "email", "attributes", "disableableCredentialTypes", "requiredActions", "notBefore", "access"})
public class KeycloakUser implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -5580861153329531358L;

    /** The id. */
    @JsonProperty("id")
    private String id;

    /** The created timestamp. */
    @JsonProperty("createdTimestamp")
    private Long createdTimestamp;

    /** The username. */
    @JsonProperty("username")
    private String username;

    /** The enabled. */
    @JsonProperty("enabled")
    private Boolean enabled;

    /** The totp. */
    @JsonProperty("totp")
    private Boolean totp;

    /** The email verified. */
    @JsonProperty("emailVerified")
    private Boolean emailVerified;

    /** The first name. */
    @JsonProperty("firstName")
    private String firstName;

    /** The last name. */
    @JsonProperty("lastName")
    private String lastName;

    /** The email. */
    @JsonProperty("email")
    private String email;

    /** The attributes. */
    @JsonProperty("attributes")
    private KeycloakAttributes attributes;

    /** The disableable credential types. */
    @JsonProperty("disableableCredentialTypes")
    private List<Object> disableableCredentialTypes = null;

    /** The required actions. */
    @JsonProperty("requiredActions")
    private List<Object> requiredActions = null;

    /** The not before. */
    @JsonProperty("notBefore")
    private Long notBefore;

    /** The access. */
    @JsonProperty("access")
    private KeycloakAccess access;

    /** The groups. */
    @JsonProperty("groups")
    private List<KeycloakGroup> groups = null;

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
     * Gets the created timestamp.
     *
     * @return the created timestamp
     */
    @JsonProperty("createdTimestamp")
    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the created timestamp.
     *
     * @param createdTimestamp the new created timestamp
     */
    @JsonProperty("createdTimestamp")
    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the enabled.
     *
     * @return the enabled
     */
    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled.
     *
     * @param enabled the new enabled
     */
    @JsonProperty("enabled")
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the totp.
     *
     * @return the totp
     */
    @JsonProperty("totp")
    public Boolean getTotp() {
        return totp;
    }

    /**
     * Sets the totp.
     *
     * @param totp the new totp
     */
    @JsonProperty("totp")
    public void setTotp(Boolean totp) {
        this.totp = totp;
    }

    /**
     * Gets the email verified.
     *
     * @return the email verified
     */
    @JsonProperty("emailVerified")
    public Boolean getEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets the email verified.
     *
     * @param emailVerified the new email verified
     */
    @JsonProperty("emailVerified")
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    @JsonProperty("attributes")
    public KeycloakAttributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the attributes.
     *
     * @param attributes the new attributes
     */
    @JsonProperty("attributes")
    public void setAttributes(KeycloakAttributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets the disableable credential types.
     *
     * @return the disableable credential types
     */
    @JsonProperty("disableableCredentialTypes")
    public List<Object> getDisableableCredentialTypes() {
        return disableableCredentialTypes;
    }

    /**
     * Sets the disableable credential types.
     *
     * @param disableableCredentialTypes the new disableable credential types
     */
    @JsonProperty("disableableCredentialTypes")
    public void setDisableableCredentialTypes(List<Object> disableableCredentialTypes) {
        this.disableableCredentialTypes = disableableCredentialTypes;
    }

    /**
     * Gets the required actions.
     *
     * @return the required actions
     */
    @JsonProperty("requiredActions")
    public List<Object> getRequiredActions() {
        return requiredActions;
    }

    /**
     * Sets the required actions.
     *
     * @param requiredActions the new required actions
     */
    @JsonProperty("requiredActions")
    public void setRequiredActions(List<Object> requiredActions) {
        this.requiredActions = requiredActions;
    }

    /**
     * Gets the not before.
     *
     * @return the not before
     */
    @JsonProperty("notBefore")
    public Long getNotBefore() {
        return notBefore;
    }

    /**
     * Sets the not before.
     *
     * @param notBefore the new not before
     */
    @JsonProperty("notBefore")
    public void setNotBefore(Long notBefore) {
        this.notBefore = notBefore;
    }

    /**
     * Gets the access.
     *
     * @return the access
     */
    @JsonProperty("access")
    public KeycloakAccess getAccess() {
        return access;
    }

    /**
     * Sets the access.
     *
     * @param access the new access
     */
    @JsonProperty("access")
    public void setAccess(KeycloakAccess access) {
        this.access = access;
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
     * Gets the groups.
     *
     * @return the groups
     */
    public List<KeycloakGroup> getGroups() {
        return groups;
    }

    /**
     * Sets the groups.
     *
     * @param groups the groups to set
     */
    public void setGroups(List<KeycloakGroup> groups) {
        this.groups = groups;
    }


}
