package com.sap.sss.vendorfake.datastore;

import com.sap.sss.vendorfake.models.OAuthPendingRequestContext;
import com.sap.sss.vendorfake.models.SapConnectedUsers;
import com.sap.sss.vendorfake.models.SapSetupNewTenantPayload;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDataStore {

    private Map<String, SapConnectedUsers> savedConnectedUsers = new HashMap<>();
    private Map<String, SapSetupNewTenantPayload> savedTenantPayloads = new HashMap<>();
    private Map<String, OAuthPendingRequestContext> pendingRequestContexts = new HashMap<>();
    private static InMemoryDataStore instance = null;

    public static InMemoryDataStore getInstance() {
        if(instance == null) {
            instance = new InMemoryDataStore();
        }

        return instance;
    }

    private InMemoryDataStore() {

    }

    public void saveConnectedUsers(SapConnectedUsers connectedUsers) {
        String key = this.buildKey(connectedUsers.getSapUserId(), connectedUsers.getSapTenantId());
        if(!savedConnectedUsers.containsKey(key)) {
            savedConnectedUsers.put(key, connectedUsers);
        }
    }

    public void removeConnectedUsers(String sapUserId, String sapTenantId) {
        String key = this.buildKey(sapUserId, sapTenantId);
        savedConnectedUsers.remove(key);
    }

    public SapConnectedUsers getConnectedUsers(String sapUserId, String sapTenantId) {
        String key = this.buildKey(sapUserId, sapTenantId);
        return savedConnectedUsers.get(key);
    }

    private String buildKey(String sapUserId, String sapTenantId) {
        return sapUserId + ":" + sapTenantId;
    }

    public void saveTenant(SapSetupNewTenantPayload setupNewTenantPayload) {
        if(!savedTenantPayloads.containsKey(setupNewTenantPayload.getTenantId())) {
            savedTenantPayloads.put(setupNewTenantPayload.getTenantId(), setupNewTenantPayload);
        }
    }

    public void removeTenant(String sapTenantId) {
        savedTenantPayloads.remove(sapTenantId);
    }

    public SapSetupNewTenantPayload getTenant(String sapTenantId) {
        return savedTenantPayloads.get(sapTenantId);
    }

    public void savePendingRequestContext(String uuid, OAuthPendingRequestContext oAuthPendingRequestContext) {
        if(!pendingRequestContexts.containsKey(uuid)) {
            pendingRequestContexts.put(uuid, oAuthPendingRequestContext);
        }
    }

    public void removePendingRequestContext(String uuid) {
        pendingRequestContexts.remove(uuid);
    }

    public OAuthPendingRequestContext getPendingRequestContext(String uuid) {
        return pendingRequestContexts.get(uuid);
    }
}
