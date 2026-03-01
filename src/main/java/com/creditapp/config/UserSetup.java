package com.creditapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Permissions;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSetup implements CommandLineRunner {

    private final IdentityService identityService;
    private final AuthorizationService authorizationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking Camunda users and authorizations...");

        // Create Groups if they don't exist
        ensureGroupExists("camundaadmin", "Camunda Administrators", "SYSTEM");
        ensureGroupExists("managers", "Managers", "WORKFLOW");
        ensureGroupExists("creditanalysts", "Credit Analysts", "WORKFLOW");
        ensureGroupExists("riskanalysts", "Risk Analysts", "WORKFLOW");

        // Create Users if they don't exist
        ensureUserExists("admin", "admin", "Admin", "User", "admin@creditapp.com", "camundaadmin");
        ensureUserExists("manager", "password", "Manager", "User", "manager@creditapp.com", "managers");
        ensureUserExists("analyst", "password", "Credit", "Analyst", "analyst@creditapp.com", "creditanalysts");
        ensureUserExists("risk", "password", "Risk", "Analyst", "risk@creditapp.com", "riskanalysts");

        // Ensure camunda-admin group has access to ALL resources
        ensureGroupAuthorization("camundaadmin");

        log.info("Camunda user and authorization setup completed.");
    }

    private void ensureGroupExists(String id, String name, String type) {
        if (identityService.createGroupQuery().groupId(id).count() == 0) {
            Group group = identityService.newGroup(id);
            group.setName(name);
            group.setType(type);
            identityService.saveGroup(group);
            log.info("Created group: {}", id);
        }
    }

    private void ensureUserExists(String id, String password, String firstName, String lastName, String email,
            String groupId) {
        if (identityService.createUserQuery().userId(id).count() == 0) {
            User user = identityService.newUser(id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            identityService.saveUser(user);
            log.info("Created user: {}", id);
        }

        // Ensure membership
        if (groupId != null && identityService.createGroupQuery().groupId(groupId).count() > 0) {
            if (identityService.createUserQuery().userId(id).memberOfGroup(groupId).count() == 0) {
                identityService.createMembership(id, groupId);
                log.info("Added user {} to group {}", id, groupId);
            }
        }
    }

    private void ensureGroupAuthorization(String groupId) {
        // Grant ALL permissions on ALL resources to the group
        if (authorizationService.createAuthorizationQuery().groupIdIn(groupId).resourceType(Resources.APPLICATION)
                .resourceId("*").count() == 0) {
            Authorization auth = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
            auth.setGroupId(groupId);
            auth.setResource(Resources.APPLICATION);
            auth.setResourceId("*");
            auth.addPermission(Permissions.ALL);
            authorizationService.saveAuthorization(auth);
            log.info("Granted ALL APPLICATION permissions to group: {}", groupId);
        }

        if (authorizationService.createAuthorizationQuery().groupIdIn(groupId).resourceType(Resources.USER)
                .resourceId("*").count() == 0) {
            Authorization auth = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
            auth.setGroupId(groupId);
            auth.setResource(Resources.USER);
            auth.setResourceId("*");
            auth.addPermission(Permissions.ALL);
            authorizationService.saveAuthorization(auth);
            log.info("Granted ALL USER permissions to group: {}", groupId);
        }

        if (authorizationService.createAuthorizationQuery().groupIdIn(groupId).resourceType(Resources.GROUP)
                .resourceId("*").count() == 0) {
            Authorization auth = authorizationService.createNewAuthorization(Authorization.AUTH_TYPE_GRANT);
            auth.setGroupId(groupId);
            auth.setResource(Resources.GROUP);
            auth.setResourceId("*");
            auth.addPermission(Permissions.ALL);
            authorizationService.saveAuthorization(auth);
            log.info("Granted ALL GROUP permissions to group: {}", groupId);
        }
    }
}
