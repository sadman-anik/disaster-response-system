package com.sadman.drs.protocol;

import java.io.Serializable;

/**
 * Request payload used by the client to authenticate with the server.
 */
public class AuthenticationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
