package com.javaspringboot_tutorial.security.service;

import com.javaspringboot_tutorial.security.model.Role;
import com.javaspringboot_tutorial.security.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public record RoleService(RoleRepository roleRepository) {

    @PostConstruct
    public List<Role> findAll() {

        List<Role> roles = roleRepository.findAll();

        return roles;
    }
}