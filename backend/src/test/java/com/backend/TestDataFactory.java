package com.backend;

import com.backend.project.domain.Project;
import com.backend.user.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class TestDataFactory {

    public static User user(UUID id, String email, String displayName, String password) {
        User user =  new User(email,displayName, password);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static Project project(UUID id, String name, String description) {
        Project project = new Project(name,description);
        ReflectionTestUtils.setField(project, "id", id);
        return project;
    }

}
