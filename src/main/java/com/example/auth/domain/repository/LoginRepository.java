package com.example.auth.domain.repository;

import com.example.auth.domain.entity.Login;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginRepository implements PanacheRepository<Login> {

}
