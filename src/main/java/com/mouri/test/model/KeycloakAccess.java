package com.mouri.test.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class KeycloakAccess.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"manageGroupMembership", "view", "mapRoles", "impersonate", "manage"})
public class KeycloakAccess implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7873814816480414089L;

    /** The manage group membership. */
    @JsonProperty("manageGroupMembership")
    private Boolean manageGroupMembership;

    /** The view. */
    @JsonProperty("view")
    private Boolean view;

    /** The map roles. */
    @JsonProperty("mapRoles")
    private Boolean mapRoles;

    /** The impersonate. */
    @JsonProperty("impersonate")
    private Boolean impersonate;

    /** The manage. */
    @JsonProperty("manage")
    private Boolean manage;

    /** The additional properties. */
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * Gets the manage group membership.
     *
     * @return the manage group membership
     */
    @JsonProperty("manageGroupMembership")
    public Boolean getManageGroupMembership() {
        return manageGroupMembership;
    }

    /**
     * Sets the manage group membership.
     *
     * @param manageGroupMembership the new manage group membership
     */
    @JsonProperty("manageGroupMembership")
    public void setManageGroupMembership(Boolean manageGroupMembership) {
        this.manageGroupMembership = manageGroupMembership;
    }

    /**
     * Gets the view.
     *
     * @return the view
     */
    @JsonProperty("view")
    public Boolean getView() {
        return view;
    }

    /**
     * Sets the view.
     *
     * @param view the new view
     */
    @JsonProperty("view")
    public void setView(Boolean view) {
        this.view = view;
    }

    /**
     * Gets the map roles.
     *
     * @return the map roles
     */
    @JsonProperty("mapRoles")
    public Boolean getMapRoles() {
        return mapRoles;
    }

    /**
     * Sets the map roles.
     *
     * @param mapRoles the new map roles
     */
    @JsonProperty("mapRoles")
    public void setMapRoles(Boolean mapRoles) {
        this.mapRoles = mapRoles;
    }

    /**
     * Gets the impersonate.
     *
     * @return the impersonate
     */
    @JsonProperty("impersonate")
    public Boolean getImpersonate() {
        return impersonate;
    }

    /**
     * Sets the impersonate.
     *
     * @param impersonate the new impersonate
     */
    @JsonProperty("impersonate")
    public void setImpersonate(Boolean impersonate) {
        this.impersonate = impersonate;
    }

    /**
     * Gets the manage.
     *
     * @return the manage
     */
    @JsonProperty("manage")
    public Boolean getManage() {
        return manage;
    }

    /**
     * Sets the manage.
     *
     * @param manage the new manage
     */
    @JsonProperty("manage")
    public void setManage(Boolean manage) {
        this.manage = manage;
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

}