package com.example.auth.service;

import com.example.auth.domain.entity.Login;
import com.example.auth.domain.service.AuthenticationPersistenceService;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthenticationService {

    @Inject
    AuthenticationPersistenceService service;

    Decoder decoder = Base64.getDecoder();

    @Transactional
    public Login register(Login login) throws UserConflictException {
        log.debug("register: {}", login);

        if(login.getName().isEmpty() || login.getPassword().isEmpty()){
            throw new UserConflictException("Username and password can't be empty!");
        }
        Login log = decode(login);
        Optional<Login> logged = service.getLogin(log);
        if (logged.isPresent()) {
            throw new UserConflictException("User with the name \"" + login.getName() + "\" already exists!");
        } else {
            log.setLogged(false);
            return service.persist(log);
        }
    }

    @Transactional
    public Login login(Login login) throws UserNotFoundException, UserConflictException {
        log.debug("login: {}", login);

        Login log = decode(login);
        Optional<Login> exists = service.getLogin(log);
        List<Login> loggedList = service.getLogged();
        if (!loggedList.isEmpty()) {
            throw new UserConflictException("Another user is already logged in!");
        }
        if (exists.isEmpty()) {
            throw new UserNotFoundException("User with the name \"" + log.getName() + "\" doesn't exist");
        } else {
            if (!(exists.get().getPassword().equals(log.getPassword()))) {
                throw new UserConflictException("Wrong password!");
            }
            exists.get().setLogged(true);
            return service.update(exists.get());
        }
    }

    @Transactional
    public Login logout(Login login) throws UserConflictException {
        log.debug("logout: {}", login);

        Login log = decode(login);
        Optional<Login> exists = service.getLogin(log);
        if (exists.isEmpty()) {
            throw new UserConflictException("User with the name \"" + log.getName() + "\" doesn't exist");
        }
        if (!(exists.get().getPassword().equals(log.getPassword()))) {
            throw new UserConflictException("Wrong password!");
        }
        exists.get().setLogged(false);
        return service.update(exists.get());
    }

    private Login decode(Login login) {
        log.debug("decode: {}", login);

        Decoder decoder = Base64.getDecoder();
        byte[] nameBytes = (decoder.decode(login.getName()));
        byte[] passwordBytes = (decoder.decode(login.getPassword()));
        login.setName(new String(nameBytes));
        login.setPassword(new String(passwordBytes));
        return login;
    }
}
