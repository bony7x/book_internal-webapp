package com.example.auth.service;

import com.example.auth.domain.entity.Roles;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.service.AuthenticationPersistenceService;
import com.example.auth.domain.service.UserPersistenceService;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthenticationService {

    @Inject
    AuthenticationPersistenceService authenticationPersistenceService;

    @Inject
    UserPersistenceService userPersistenceService;

    Decoder decoder = Base64.getDecoder();
    Encoder encoder = Base64.getEncoder();

    @Transactional
    public User register(User login) throws UserConflictException {
        log.debug("register: {}", login);

        if(login.getName().isEmpty() || login.getPassword().isEmpty() || login.getEmail().isEmpty()){
            throw new UserConflictException("Username, email and password can't be empty!");
        }
        User log = decode(login);
        Optional<User> logged = authenticationPersistenceService.getLogin(log);
        if (logged.isPresent()) {
            throw new UserConflictException("User with chosen username or email already exists!");
        } else {
            log.setRole(Roles.USER);
            return userPersistenceService.persist(log);
        }
    }

    @Transactional
    public String login(String authString) throws UserNotFoundException, UserConflictException {
        log.debug("login: {}", authString);

        authString = authString.replace("Basic ","");
        String decoded = new String(decoder.decode(authString));
        String[] split = decoded.split(":");
        Optional<User> exists = authenticationPersistenceService.getLogin(new User(split[0],split[1]));
        if (exists.isEmpty()) {
            throw new UserNotFoundException("User with the name \"" + split[0] + "\" doesn't exist");
        } else {
            if (!(exists.get().getPassword().equals(split[1]))) {
                throw new UserConflictException("Wrong password!");
            }
        }
        String encodedRole;
        encodedRole = "[" + encoder.encodeToString(exists.get().getRole().toString().getBytes()) + "]";
        authString = authString + encodedRole;
        return authString;
    }

    private User decode(User login) {
        log.debug("decode: {}", login);

        Decoder decoder = Base64.getDecoder();
        byte[] nameBytes = decoder.decode(login.getName());
        byte[] passwordBytes = decoder.decode(login.getPassword());
        byte[] emailBytes = decoder.decode(login.getEmail());
        login.setName(new String(nameBytes));
        login.setPassword(new String(passwordBytes));
        login.setEmail(new String(emailBytes));
        return login;
    }
}
