package com.sadman.drs.server;

import com.sadman.drs.server.config.DatabaseConnection;
import com.sadman.drs.server.config.ServerConfig;
import com.sadman.drs.server.repository.DepartmentRepository;
import com.sadman.drs.server.repository.ResourceRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A minimal multi-threaded server skeleton for the DRS application.
 */
public class DRSServer {

    public static void main(String[] args) {
        try {
            DatabaseConnection.initializeDatabase();
            new DepartmentRepository().seedDefaultDepartments();
            new ResourceRepository().seedDefaultResources();
            startServer();
        } catch (SQLException exception) {
            System.err.println("Failed to initialize database: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static void startServer() {
        ExecutorService executorService = Executors.newFixedThreadPool(ServerConfig.THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(ServerConfig.PORT)) {
            System.out.println("DRS server is running on port " + ServerConfig.PORT);
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException exception) {
            System.err.println("Server socket error: " + exception.getMessage());
            exception.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
