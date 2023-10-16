package com.example.auth.domain.service;

import com.example.auth.domain.entity.Roles;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.exception.UserNotFoundException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.domain.service.CustomerPersistenceService;
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
public class AuthenticationPersistenceService {

    @Inject
    UserRepository repository;

    @Inject
    CustomerPersistenceService customerPersistenceService;

    Decoder decoder = Base64.getDecoder();

    public User persist(User login) {
        log.debug("persist: {}", login);

        repository.persist(login);
        return login;
    }

    public Optional<User> getLogin(User login) {
        log.debug("getLogin: {}", login);

        return repository.find("Select e from User e where e.name = ?1 or e.email = ?2", login.getName(),
                login.getEmail()).stream().findFirst();
    }

    public List<User> getAllUsers() {
        log.debug("getAllUsers");

        return repository.findAll().stream().toList();
    }

    @Transactional
    public void updateUserRole(User update) throws UserNotFoundException {
        log.debug("updateUserRole: {}", update);

        Optional<User> user = repository.findByIdOptional(Long.valueOf(update.getId()));
        if (user.isEmpty()) {
            throw new UserNotFoundException("User was not found!");
        }
        user.get().setRole(update.getRole());
        repository.getEntityManager().merge(user.get());
    }

    public User getCurrentUser(String token){
        log.debug("getCurrentUser: {}", token);

        token = token.replace("Basic ","");
        String[] split2= token.split("\\[");
        String token2 = split2[0];
        String decoded = new String(decoder.decode(token2));
        String[] split = decoded.split(":");
        Optional<User> exists = getLogin(new User(split[0],split[1]));
        return exists.orElse(null);
    }

    @Transactional
    public void registerUserAsCustomer(User userFromDto, String firstName, String lastName, String address){
        log.debug("registerUserAsCustomer: {} {} {} {}", userFromDto,firstName,lastName,address);

        User user = repository.findById(Long.valueOf(userFromDto.getId()));
        Customer customer = new Customer();
        customer.setEmail(user.getEmail());
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setAddress(address);
        Customer persisted = customerPersistenceService.persist(customer);
        user.setCustomer(persisted);
        user.setRole(userFromDto.getRole());
        repository.getEntityManager().merge(user);
    }
}
