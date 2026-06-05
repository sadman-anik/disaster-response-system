package com.sadman.drs.server.config;

import com.sadman.drs.server.repository.DepartmentRepository;
import com.sadman.drs.server.repository.ResourceRepository;
import com.sadman.drs.server.repository.UserRepository;

import java.sql.SQLException;

/**
 * Seeds default users, departments, and resources after schema creation.
 */
public class DatabaseSeeder {

    public void seedDefaults() throws SQLException {
        new UserRepository().createDefaultUsers();
        new DepartmentRepository().seedDefaultDepartments();
        new ResourceRepository().seedDefaultResources();
    }
}
