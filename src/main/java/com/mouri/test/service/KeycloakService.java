package com.mouri.test.service;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mouri.test.model.KeycloakGroup;
import com.mouri.test.model.KeycloakUser;

/**
 * Keycloak API.
 *
 * @author firtha
 *
 */
public class KeycloakService {

    /** Logger. */
    private static final Log LOG = LogFactory.getLog(KeycloakService.class.getName());

    /** where %s is the realm name. */
    private static final String USERS_PATH_PATTERN = "admin/realms/%s/users";

    /** where %s is the realm name. */
    private static final String USER_ID_PATH_PATTERN = "admin/realms/%s/users/%s";

    /***/
    private static final String MEMBERS_PATH_PATTERN = "admin/realms/%s/groups/%s/members";

    // private static final String ROLES_PATH_PATTERN = "admin/realms/%s/clients/%s/roles";

    /** DELETE /{realm}/attack-detection/brute-force/users/{userId}. */
    @SuppressWarnings("unused")
    private static final String CLEAR_LOGIN_FAILURES_PATH_PATTERN = "admin/realms/%s/attack-detection/brute-force/users/%s";

    /** where 1st %s is the realm name, 2nd the user Id. */
    private static final String NEW_USER_PASSCODE = "realms/%s/gc-ext/passcodes/%s/newAccount";

    /** where %s is the realm name. */
    private static final String GROUPS_PATH_PATTERN = "admin/realms/%s/groups";

    /** where %s is the user_id. */
    private static final String USER_GROUPS_PATH_PATTERN = USERS_PATH_PATTERN + "/%s/groups";

    /** where %s is the group_id. */
    private static final String USER_ALTER_GROUPS_PATH_PATTERN = USER_GROUPS_PATH_PATTERN + "/%s";

    /** Logout redirect URL: first %s is realm and the second %s is redirect url. */
    private static final String LOGOUT_PATH_PATTERN = "realms/%s/protocol/openid-connect/logout?redirect_uri=%s";

    /** Keycloak adapterConfig. */
    private static AdapterConfig adapterConfig;

    /** Token Service. */
    private static TokenService tokenService;

    /** rest template. */
    protected static RestTemplate restTemplate;

    /** The client root group name. */
    private static String clientRootGroupName = null;

    /** Client Root Group UUID, will be intialized when necessary. */
    private static String clientRootGroupUUID = null;

    /** Client sub groups. */
    private static Map<String, KeycloakGroup> clientSubGroups = null;

    /** The Constant MEMBERS_RESPONSE_TYPE. */
    private static final ParameterizedTypeReference<Collection<KeycloakUser>> MEMBERS_RESPONSE_TYPE = //
        new ParameterizedTypeReference<Collection<KeycloakUser>>() {
        };

    /** The Constant MEMBER_RESPONSE_TYPE. */
    private static final ParameterizedTypeReference<KeycloakUser> MEMBER_RESPONSE_TYPE = //
        new ParameterizedTypeReference<KeycloakUser>() {
        };

    /** The Constant GROUPS_RESPONSE_TYPE. */
    private static final ParameterizedTypeReference<Collection<KeycloakGroup>> GROUPS_RESPONSE_TYPE = //
        new ParameterizedTypeReference<Collection<KeycloakGroup>>() {
        };

    /**
     * Instantiates a dummy keycloak service for API.
     */
    public KeycloakService() {
    }

    /**
     * Default constructor.
     *
     * @param pAdapterConfig the adapter config
     * @param pTokenService the token service
     * @param pRestTemplate the rest template
     * @param pClientRootGroupName the client root group name
     */
    public KeycloakService(final AdapterConfig pAdapterConfig, final TokenService pTokenService,
                           final RestTemplate pRestTemplate, final String pClientRootGroupName) {
        adapterConfig = pAdapterConfig;
        tokenService = pTokenService;
        restTemplate = pRestTemplate;
        clientRootGroupName = pClientRootGroupName;
    }

    /**
     * Return the logout URL.
     *
     * @param redirectURI The redirect URL (must not be null or empty)
     *
     * @return The URL
     */
    public String getLogoutURL(final String redirectURI) {
        return String.format(adapterConfig.getAuthServerUrl() + LOGOUT_PATH_PATTERN, adapterConfig.getRealm(),
            redirectURI);
    }

    /**
     * get the entire sub groups.
     *
     * @return groups
     */
    public static Collection<KeycloakGroup> getClientSubGroups() {
        if (clientSubGroups == null) {
            initSubGroups();
        }
        return clientSubGroups.values();
    }

    /**
     * Init Groups.
     *
     * @return groups
     */
    private static Map<String, KeycloakGroup> initSubGroups() {
        if (clientSubGroups != null) {
            return clientSubGroups;
        }
        try {
            if (clientRootGroupUUID == null) {
                initializeGroupUUID();
            }
            String url = String.format(
                adapterConfig.getAuthServerUrl() + GROUPS_PATH_PATTERN + "/" + clientRootGroupUUID,
                adapterConfig.getRealm());
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity httpEntity = new HttpEntity(headers);
            KeycloakGroup group = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KeycloakGroup.class).getBody();
            clientSubGroups = group.getSubGroups().stream().collect(Collectors.toMap(KeycloakGroup::getId, e -> e));
        } catch (Exception e) {
            LOG.error(e);
        }
        return clientSubGroups;
    }

    /**
     * Users for given group.
     *
     * @param group group
     * @return collection
     */
    public Collection<KeycloakUser> getUsers(KeycloakGroup group) {
        if (group == null) {
            return new ArrayList<>();
        }
        try {
            String url = String.format(adapterConfig.getAuthServerUrl() + MEMBERS_PATH_PATTERN,
                adapterConfig.getRealm(), group.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity httpEntity = new HttpEntity(headers);
            Collection<KeycloakUser> users = restTemplate
                .exchange(url, HttpMethod.GET, httpEntity, MEMBERS_RESPONSE_TYPE).getBody();
            return users;
        } catch (Exception e) {
            LOG.error(e);
        }
        return new ArrayList<>();
    }

    /**
     * Initialize Group UUID.
     *
     * @throws IOException error
     */
    private static void initializeGroupUUID() throws IOException {
        String url = String.format(adapterConfig.getAuthServerUrl() + GROUPS_PATH_PATTERN, adapterConfig.getRealm());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity httpEntity = new HttpEntity(headers);
        Collection<KeycloakGroup> groups = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GROUPS_RESPONSE_TYPE)
            .getBody();
        for (KeycloakGroup group : groups) {
            if (group.getName().equalsIgnoreCase(clientRootGroupName)) {
                clientRootGroupUUID = group.getId();
                return;
            }
        }
    }

    /**
     * Update user.
     *
     * @param keycloakUser the keycloak user
     * @param role the role
     * @return the keycloak user
     * @throws Exception the exception
     */
    public KeycloakUser updateUser(KeycloakUser keycloakUser, String role) throws Exception {
        keycloakUser.setEnabled(true);
        updateUser(keycloakUser);
        Collection<String> idmRoles = getUserRoles(keycloakUser.getId());

        for (String idmRole : idmRoles) {
            removeUserFromRole(keycloakUser.getId(), idmRole);
        }

        addUserToRole(keycloakUser.getId(), role);

        return keycloakUser;
    }

    /**
     * Create an user and add the user to the given role.
     *
     * @param userName The username (must be unique, such as IDM UUID)
     * @param firstName The first name
     * @param lastName The last name
     * @param email The email
     * @param role The role (e.g., ROLE_IDM_USER)
     *
     * @return The keycloak user created, and null if not
     *
     * @throws Exception (May throw)
     */
    public KeycloakUser createUser(String userName, String firstName, String lastName, String email, String role)
        throws Exception {

        String usersPath = String.format(USERS_PATH_PATTERN, adapterConfig.getRealm());
        try {

            KeycloakUser usr = new KeycloakUser();
            if (!StringUtils.isEmpty(userName)) {
                usr.setUsername(userName);
            } else {
                usr.setUsername(UUID.randomUUID().toString());
            }
            usr.setFirstName(firstName);
            usr.setLastName(lastName);
            usr.setEmail(email);
            usr.setEnabled(true);

            ObjectMapper mapper = new ObjectMapper();
            String userString = mapper.writeValueAsString(usr);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<String> httpEntity = new HttpEntity<>(userString, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(adapterConfig.getAuthServerUrl() + usersPath,
                httpEntity, String.class);

            // created
            if (response.getStatusCode().is2xxSuccessful()) {
                usr = findUserByEmail(email);

                // Add the user to the role
                addUserToRole(usr.getId(), role);

                // Send the invitation
//                sendPasscodeEmail(usr.getId());

                return usr;
            } else {
                throw new Exception("User Not Created.");
            }

        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + usersPath);
            throw e;
        } catch (Exception e) {
            LOG.error("Unable to create user.");
            throw e;
        }
    }

    /**
     * Creates a user based on the provided email address.
     *
     * @param email - the email/username for the Keycloak account
     * @return true/false if user account creation successful
     * @throws Exception err
     */
    public KeycloakUser createUser(String email) throws Exception {

        String usersPath = String.format(USERS_PATH_PATTERN, adapterConfig.getRealm());
        try {

            KeycloakUser usr = new KeycloakUser();
            usr.setEmail(email);
            usr.setUsername(email);
            usr.setEnabled(true);

            ObjectMapper mapper = new ObjectMapper();
            String userString = mapper.writeValueAsString(usr);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<String> httpEntity = new HttpEntity<>(userString, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(adapterConfig.getAuthServerUrl() + usersPath,
                httpEntity, String.class);

            // created
            if (response.getStatusCode().is2xxSuccessful()) {
                return findUserByEmail(email);
            } else {
                throw new Exception("User Not Created.");
            }

        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + usersPath);
            throw e;
        } catch (IOException e) {
            LOG.error("Unable to create user.");
            throw e;
        }
    }

    /**
     * Update a keycloak user.
     *
     * @param usr The keycloak user
     *
     * @return The keycloak user after update
     *
     * @throws Exception (May throw)
     */
    public KeycloakUser updateUser(final KeycloakUser usr) throws Exception {
        String usersPath = String.format(adapterConfig.getAuthServerUrl() + USER_ID_PATH_PATTERN,
            adapterConfig.getRealm(), usr.getId());

        try {
            ObjectMapper mapper = new ObjectMapper();
            String userString = mapper.writeValueAsString(usr);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<String> httpEntity = new HttpEntity<>(userString, headers);

            restTemplate.put(usersPath, httpEntity);

            return findUserByIdmId(usr.getId());

        } catch (RestClientException e) {
            String error = String.format("Unable to update user (%s)", e.toString());
            LOG.error(error);
            throw e;
        } catch (IOException e) {
            String error = String.format("Unable to update user (%s)", e.toString());
            LOG.error(error);
            throw e;
        }
    }

    /**
     * Creates a user based on the provided email address.
     *
     * @param userId - the for the Keycloak account
     * @return true/false if user account creation successful
     * @throws Exception err
     */
    public boolean sendPasscodeEmail(String userId) throws Exception {

        String passcodePath = String.format(NEW_USER_PASSCODE, adapterConfig.getRealm(), userId);
        try {
            URIBuilder builder = new URIBuilder(adapterConfig.getAuthServerUrl() + passcodePath);

            builder.setParameter(KeycloakConstants.IP_PARAM_KEY, "true");

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity httpEntity = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.postForEntity(builder.build(), httpEntity, String.class);

            // created
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            } else {
                throw new Exception("User Not Created.");
            }

        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + passcodePath);
            throw e;
        } catch (IOException e) {
            LOG.error("Unable to send passcode email.");
            throw e;
        }
    }

    /**
     * Retrieve the role names from the given groups.
     *
     * @param groups Collection of groups
     *
     * @return List of roles (e.g., ROLE_IDM_USER)
     */
    public List<String> getClientRoles(Collection<KeycloakGroup> groups) {
        List<String> retVal = new ArrayList<>();
        if (groups == null || groups.isEmpty()) {
            // Return an empty list
            return retVal;
        }

        // Loop through each group and accumulate all roles
        for (KeycloakGroup grp : groups) {
            retVal.addAll(grp.getClientRoles(adapterConfig.getResource()));
        }

        return retVal;
    }

    /**
     * Get the sub groups that contain a specific role (e.g., ROLE_IDM_USER).
     *
     * @param roleName The role name
     *
     * @return List of groups
     */
    public Collection<KeycloakGroup> getClientSubGroupsWithRole(String roleName) {
        Collection<KeycloakGroup> retVal = new ArrayList<>();

        Collection<KeycloakGroup> subGroups = getClientSubGroups();
        for (KeycloakGroup grp : subGroups) {
            if (grp.getClientRoles(adapterConfig.getResource()).contains(roleName)) {
                retVal.add(grp);
            }
        }

        return retVal;
    }

    /**
     * Add user to a specific role (e.g., ROLE_IDM_USER).
     *
     * @param idmId The user id
     * @param roleName The role name
     * @throws URISyntaxException (May throw)
     * @throws IOException (May throw)
     */
    public void addUserToRole(String idmId, String roleName) throws URISyntaxException, IOException {

        // Get the list of groups that contains this role
        Collection<KeycloakGroup> groups = getClientSubGroupsWithRole(roleName);

        // Add user to all these groups
        for (KeycloakGroup group : groups) {
            addUserToGroup(idmId, group.getId());
        }
    }

    /**
     * Remove user from a specific role (e.g., ROLE_IDM_USER).
     *
     * @param idmId The user id
     * @param roleName The role name
     * @throws URISyntaxException (May throw)
     * @throws IOException (May throw)
     */
    public void removeUserFromRole(String idmId, String roleName) throws URISyntaxException, IOException {

        // Get the list of groups that contains this role
        Collection<KeycloakGroup> groups = getClientSubGroupsWithRole(roleName);

        // Remove user from all these groups
        for (KeycloakGroup group : groups) {
            removeUserFromGroup(idmId, group.getId());
        }
    }

    /**
     * Adds the keycloak user identified by the provided id to the keycloak group identified by the provided
     * applicantGroupId.
     *
     * @param idmId - the keycloak user id
     * @param groupUUID - the ibc applicant user group id
     * @return - true/false on adding user to group
     * @throws URISyntaxException err
     * @throws IOException err
     */
    public KeycloakGroup addUserToGroup(String idmId, String groupUUID) throws URISyntaxException, IOException {

        KeycloakUser kcUser = findUserByIdmId(idmId);
        if (kcUser == null) {
            LOG.error("Invalid User : " + idmId);
            return null;
        }

        String addUserToGroupPath = String.format(USER_ALTER_GROUPS_PATH_PATTERN, adapterConfig.getRealm(),
            kcUser.getId(), groupUUID);

        try {
            URIBuilder builder = new URIBuilder(adapterConfig.getAuthServerUrl() + addUserToGroupPath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(builder.build(), HttpMethod.PUT, httpEntity,
                String.class);

            // created
            if (response.getStatusCode().is2xxSuccessful()) {
                if (clientSubGroups == null) {
                    initSubGroups();
                }
                return clientSubGroups.get(groupUUID);
            }

        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + addUserToGroupPath);
            throw e;
        } catch (IOException e) {
            LOG.error("Unable to add user to group.");
            throw e;
        }
        LOG.error("Unable to add user to group.");
        return null;

    }

    /**
     * Remove role.
     *
     * @param idmId id
     * @param groupUUID id
     * @return bool
     * @throws URISyntaxException err
     * @throws IOException err
     */
    public KeycloakGroup removeUserFromGroup(String idmId, String groupUUID) throws URISyntaxException, IOException {
        KeycloakUser kcUser = findUserByIdmId(idmId);
        if (kcUser == null) {
            LOG.error("Invalid User : " + idmId);
            return null;
        }

        String removeUserToGroupPath = String.format(USER_ALTER_GROUPS_PATH_PATTERN, adapterConfig.getRealm(),
            kcUser.getId(), groupUUID);

        try {
            URIBuilder builder = new URIBuilder(adapterConfig.getAuthServerUrl() + removeUserToGroupPath);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(builder.build(), HttpMethod.DELETE, httpEntity,
                String.class);

            // created
            if (response.getStatusCode().is2xxSuccessful()) {
                if (clientSubGroups == null) {
                    initSubGroups();
                }
                return clientSubGroups.get(groupUUID);
            }

        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + removeUserToGroupPath);
            throw e;
        } catch (IOException e) {
            LOG.error("Unable to remove user from group.");
            throw e;
        }
        LOG.error("Unable to remove user from group.");
        return null;
    }

    /**
     * Find a user by email address.
     *
     * @param email email
     * @return user
     * @throws URISyntaxException err
     * @throws IOException err
     */
    public KeycloakUser findUserByEmail(String email) throws URISyntaxException, IOException {
        String trimmedEmail = StringUtils.trim(email);

        if (StringUtils.isEmpty(email)) {
            return null;
        }
        try {
            String url = String.format(adapterConfig.getAuthServerUrl() + USERS_PATH_PATTERN, adapterConfig.getRealm());
            URIBuilder builder = new URIBuilder(url);
            builder.setParameter("email", trimmedEmail);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity httpEntity = new HttpEntity(headers);
            Collection<KeycloakUser> users = restTemplate
                .exchange(builder.build(), HttpMethod.GET, httpEntity, MEMBERS_RESPONSE_TYPE).getBody();
            for (KeycloakUser user : users) {
                if (trimmedEmail.equalsIgnoreCase(user.getEmail())) {
                    return user;
                }
            }
        } catch (Exception e) {
            LOG.error("Error fetching user by email:" + e);
            throw e;
        }
        return null;
    }

    /**
     * Find a user by user name.
     *
     * @param userName user name
     * @return user
     * @throws URISyntaxException err
     * @throws IOException err
     */
    public KeycloakUser findUserByUserName(String userName) throws URISyntaxException, IOException {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }
        try {
            String url = String.format(adapterConfig.getAuthServerUrl() + USERS_PATH_PATTERN, adapterConfig.getRealm());
            URIBuilder builder = new URIBuilder(url);
            builder.setParameter("username", userName);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity httpEntity = new HttpEntity(headers);
            Collection<KeycloakUser> users = restTemplate
                .exchange(builder.build(), HttpMethod.GET, httpEntity, MEMBERS_RESPONSE_TYPE).getBody();
            for (KeycloakUser user : users) {
                if (userName.equalsIgnoreCase(user.getUsername())) {
                    return user;
                }
            }
        } catch (Exception e) {
            LOG.error("Error fetching user by username:" + e);
            throw e;
        }
        return null;
    }

    /**
     * Find a user by id.
     *
     * @param idmId email
     *
     * @return user or null if not found
     */
    public KeycloakUser findUserByIdmId(String idmId) {
        if (StringUtils.isEmpty(idmId)) {
            return null;
        }

        try {
            String url = String.format(adapterConfig.getAuthServerUrl() + USER_ID_PATH_PATTERN,
                adapterConfig.getRealm(), idmId);
            URIBuilder builder = new URIBuilder(url);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.ACCEPT, "application/json");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity httpEntity = new HttpEntity(headers);

            KeycloakUser user = restTemplate.exchange(builder.build(), HttpMethod.GET, httpEntity, MEMBER_RESPONSE_TYPE)
                .getBody();

            return user;
        } catch (Exception e) {
            LOG.error("Error fetching user by Idm id:" + e);
            return null;
        }
    }

    /**
     * Get roles.
     *
     * @param idmId id
     * @return bool
     * @throws URISyntaxException err
     * @throws IOException err
     */
    public Collection<KeycloakGroup> getUserGroups(String idmId) throws URISyntaxException, IOException {
        KeycloakUser kcUser = findUserByIdmId(idmId);
        if (kcUser == null) {
            LOG.error("Invalid User : " + idmId);
            // Sonar fix
            return new ArrayList<>();
        }

        String path = String.format(USER_GROUPS_PATH_PATTERN, adapterConfig.getRealm(), kcUser.getId());

        try {
            URIBuilder builder = new URIBuilder(adapterConfig.getAuthServerUrl() + path);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessTokenString());
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Collection<KeycloakGroup>> response = restTemplate.exchange(builder.build(), HttpMethod.GET,
                httpEntity, GROUPS_RESPONSE_TYPE);
            // created
            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() == null) {
                    return new ArrayList<>();
                }
                // Groups that come back lack the roles object, because of course they do.
                initSubGroups();
                ArrayList<KeycloakGroup> userGroups = new ArrayList<>();
                for (KeycloakGroup g : response.getBody()) {
                    userGroups.add(clientSubGroups.get(g.getId()));
                }
                return userGroups;
            }

        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + path);
            throw e;
        } catch (IOException e) {
            LOG.error("Unable to get user's groups.");
            throw e;
        }
        LOG.error("Unable to get user's groups.");
        return new ArrayList<>();
    }

    /**
     * Get the roles that the user is assigned in.
     *
     * @param idmId The id
     * @return List of roles
     */
    public Collection<String> getUserRoles(String idmId) {
        Collection<String> retVal = new ArrayList<>();

        try {
            Collection<KeycloakGroup> userGroups = getUserGroups(idmId);

            if (!CollectionUtils.isEmpty(userGroups)) {
                collectAllRoles(retVal, userGroups);
            }
        } catch (URISyntaxException e) {
            LOG.error("Unable to get user's roles.", e);
            return retVal;
        } catch (IOException e) {
            LOG.error("Unable to get user's roles.", e);
            return retVal;
        }

        return retVal;
    }

    /**
     * Collect all roles.
     *
     * @param retVal the ret val
     * @param userGroups the user groups
     */
    private void collectAllRoles(Collection<String> retVal, Collection<KeycloakGroup> userGroups) {
        for (KeycloakGroup grp : userGroups) {
            Set<String> keys = grp.getClientRoles().keySet();
            for (String key : keys) {
                retVal.addAll(grp.getClientRoles().get(key));
            }
        }
    }

    /**
     * Get the roles that the user is assigned in.
     *
     * @param username the username
     * @return List of roles
     */
    public Collection<String> getUserRolesByUsername(String username) {
        Collection<String> retVal = new ArrayList<>();
        try {
            KeycloakUser keycloakUser = findUserByUserName(username);
            if (null != keycloakUser) {
                retVal = getUserRoles(keycloakUser.getId());
            }
        } catch (URISyntaxException | IOException e) {
            LOG.error("Unable to get user's roles.", e);
        }
        return retVal;
    }

    /**
     * Checks if is client sub groups empty.
     *
     * @return the boolean
     */
    public Boolean isClientSubGroupsEmpty() {
        return MapUtils.isEmpty(clientSubGroups);
    }
}