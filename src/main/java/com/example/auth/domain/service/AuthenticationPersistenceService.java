package com.example.auth.domain.service;

import com.example.auth.domain.entity.User;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.exception.UserNotFoundException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthenticationPersistenceService {

    @Inject
    UserRepository repository;

    public User persist(User login){
        log.debug("persist: {}", login);

        repository.persist(login);
        return login;
    }

    public Optional<User> getLogin(User login){
        log.debug("getLogin: {}", login);

        return repository.find("Select e from User e where e.name = ?1", login.getName()).stream().findFirst();
    }

    public List<User> getAllUsers(){
        log.debug("getAllUsers");

        return repository.findAll().stream().toList();
    }

    @Transactional
    public void updateUserRole(User update) throws UserNotFoundException {
        log.debug("updateUserRole: {}", update);

        Optional<User> user =  repository.findByIdOptional(Long.valueOf(update.getId()));
        if(user.isEmpty()){
            throw new UserNotFoundException("User was not found!");
        }
        user.get().setRole(update.getRole());
        repository.getEntityManager().merge(user.get());
    }
}
