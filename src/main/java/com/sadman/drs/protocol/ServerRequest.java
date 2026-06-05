package com.sadman.drs.protocol;

import java.io.Serializable;

/**
 * A serializable request sent from the client to the DRS server.
 */
public class ServerRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ServerAction action;
    private final Object payload;

    public ServerRequest(ServerAction action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

    public ServerAction getAction() {
        return action;
    }

    public Object getPayload() {
        return payload;
    }
}
