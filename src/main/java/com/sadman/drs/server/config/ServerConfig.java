package com.sadman.drs.server.config;

/**
 * Configuration constants for the DRS server.
 */
public final class ServerConfig {
    public static final String HOST = "localhost";
    public static final int PORT = 9090;
    public static final int THREAD_POOL_SIZE = 10;
    public static final String ENCRYPTION_KEY = "DRS16ByteSecret!";

    private ServerConfig() {
    }
}
