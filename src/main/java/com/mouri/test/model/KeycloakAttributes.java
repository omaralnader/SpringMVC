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
 * The Class KeycloakAttributes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"mobile", "question", "locale", "loa"})
public class KeycloakAttributes implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -3395847438293772191L;

    /** The mobile. */
    @JsonProperty("mobile")
    private List<String> mobile = null;

    /** The question. */
    @JsonProperty("question")
    private List<String> question = null;

    /** The locale. */
    @JsonProperty("locale")
    private List<String> locale = null;

    /** The loa. */
    @JsonProperty("loa")
    private List<String> loa = null;

    /** The additional properties. */
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * Gets the mobile.
     *
     * @return the mobile
     */
    @JsonProperty("mobile")
    public List<String> getMobile() {
        return mobile;
    }

    /**
     * Sets the mobile.
     *
     * @param mobile the new mobile
     */
    @JsonProperty("mobile")
    public void setMobile(List<String> mobile) {
        this.mobile = mobile;
    }

    /**
     * Gets the question.
     *
     * @return the question
     */
    @JsonProperty("question")
    public List<String> getQuestion() {
        return question;
    }

    /**
     * Sets the question.
     *
     * @param question the new question
     */
    @JsonProperty("question")
    public void setQuestion(List<String> question) {
        this.question = question;
    }

    /**
     * Gets the locale.
     *
     * @return the locale
     */
    @JsonProperty("locale")
    public List<String> getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    @JsonProperty("locale")
    public void setLocale(List<String> locale) {
        this.locale = locale;
    }

    /**
     * Gets the loa.
     *
     * @return the loa
     */
    @JsonProperty("loa")
    public List<String> getLoa() {
        return loa;
    }

    /**
     * Sets the loa.
     *
     * @param loa the new loa
     */
    @JsonProperty("loa")
    public void setLoa(List<String> loa) {
        this.loa = loa;
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