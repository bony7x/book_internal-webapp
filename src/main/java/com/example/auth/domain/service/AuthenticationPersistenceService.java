package com.example.auth.domain.service;

import com.example.auth.domain.entity.Login;
import com.example.auth.domain.repository.LoginRepository;
import com.example.auth.exception.UserConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthenticationPersistenceService {

    @Inject
    LoginRepository repository;

    public Login persist(Login login){
        log.debug("persist: {}", login);

        repository.persist(login);
        return login;
    }

    public Optional<Login> getLogin(Login login){
        log.debug("getLogin: {}", login);

        return repository.find("Select e from Login e where e.name = ?1", login.getName()).stream().findFirst();
    }

    public List<Login> getLogged(){
        log.debug("getLogged");

        return repository.find("Select e from Login e where e.logged = ?1", true).stream().toList();
    }

    public Login update(Login login){
        log.debug("update: {}", login);

        return repository.getEntityManager().merge(login);
    }
}
