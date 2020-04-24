package main.domain.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserSecurity {
    private ConcurrentMap<String, Integer> authorizedUsers = new ConcurrentHashMap<>();

    public void authorizeUser(String sessionId, int userId) {
        authorizedUsers.put(sessionId, userId);
    }

    public boolean checkUserAuthorization(String sessionId) {
        return authorizedUsers.containsKey(sessionId);
    }

    public int getAuthorizedUserId(String sessionId) {
        return authorizedUsers.get(sessionId);
    }

    public void removeUserAuthorization(String sessionId) {
        authorizedUsers.remove(sessionId);
    }
}