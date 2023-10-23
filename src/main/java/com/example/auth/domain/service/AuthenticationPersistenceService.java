package com.example.auth.domain.service;

import com.example.auth.domain.entity.Roles;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.exception.UserConflictException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.service.CustomerPersistenceService;
import com.example.customer.exception.CustomerConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthenticationPersistenceService {

    @Inject
    UserRepository repository;

    @Inject
    CustomerPersistenceService customerPersistenceService;

    Encoder encoder = Base64.getEncoder();

    public Optional<User> getLogin(User login) {
        log.debug("getLogin: {}", login);

        return repository.find("Select e from User e where e.name = ?1 or e.email = ?2", login.getName(),
                login.getEmail()).stream().findFirst();
    }

    @Transactional
    public String registerUserAsCustomer(User userFromDto, String firstName, String lastName, String address)
            throws UserConflictException, CustomerConflictException {
        log.debug("registerUserAsCustomer: {} {} {} {}", userFromDto, firstName, lastName, address);

        User user = repository.findById(Long.valueOf(userFromDto.getId()));
        if (user.getCustomer() != null) {
            throw new UserConflictException("User is already a registered customer!");
        }
        Customer customer = new Customer();
        customer.setEmail(user.getEmail());
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setAddress(address);
        Customer persisted = customerPersistenceService.persist(customer);
        user.setCustomer(persisted);
        user.setRole(Roles.CUSTOMER);
        user = repository.getEntityManager().merge(user);
        return sendAuthString(user);

    }

    public String sendAuthString(User user) {
        log.debug("sendAuthString: {}", user);

        String authString = user.getName() + ":" + user.getPassword();
        authString = encoder.encodeToString(authString.getBytes());
        authString = authString + "[" + encoder.encodeToString(user.getRole().toString().getBytes()) + "]";
        return authString;
    }
}
