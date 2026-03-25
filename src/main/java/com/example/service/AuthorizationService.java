package com.example.service;

import com.example.model.Role;
import com.example.model.UserAccount;
import java.util.List;

public class AuthorizationService {

    public boolean hasPermission(UserAccount account, Role role, String permission) {
        if (!account.isActive()) return false;
        if (account.isLocked()) return false;
        return role.hasPermission(permission);
    }

    public boolean canAccessResource(UserAccount account, List<Role> roles, String resource) {
        if (!account.isActive() || account.isLocked()) return false;
        for (Role role : roles) {
            if (role.hasPermission(resource)) return true;
        }
        return false;
    }

    public boolean isAdmin(List<Role> roles) {
        if (roles == null) return false;
        for (Role role : roles) {
            if ("ADMIN".equalsIgnoreCase(role.getName())) return true;
        }
        return false;
    }
}
