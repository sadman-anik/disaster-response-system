package com.sadman.drs.protocol;

import java.io.Serializable;

/**
 * Request payload used by the client to register a new DRS user.
 */
public class UserRegistrationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final String role;

    public UserRegistrationRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
