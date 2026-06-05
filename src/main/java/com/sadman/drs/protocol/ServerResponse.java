package com.sadman.drs.protocol;

import java.io.Serializable;

/**
 * A serializable response from the DRS server to the client.
 */
public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final Object payload;

    public ServerResponse(boolean success, String message, Object payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public static ServerResponse success(String message, Object payload) {
        return new ServerResponse(true, message, payload);
    }

    public static ServerResponse failure(String message) {
        return new ServerResponse(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }
}
