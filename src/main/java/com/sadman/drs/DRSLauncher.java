package com.sadman.drs;

import com.sadman.drs.client.MainApp;
import com.sadman.drs.server.DRSServer;

import java.sql.SQLException;

/**
 * Single application entry point for IDEs such as NetBeans.
 */
public class DRSLauncher {

    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> {
            try {
                DRSServer.start();
            } catch (SQLException exception) {
                System.err.println("Failed to start DRS server: " + exception.getMessage());
                exception.printStackTrace();
            }
        }, "drs-server");
        serverThread.setDaemon(true);
        serverThread.start();

        MainApp.main(args);
    }
}
