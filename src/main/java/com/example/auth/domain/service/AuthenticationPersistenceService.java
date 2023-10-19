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
import java.util.Base64.Decoder;
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

    Decoder decoder = Base64.getDecoder();

    Encoder encoder = Base64.getEncoder();

/*    public User persist(User login) {
        log.debug("persist: {}", login);

        repository.persist(login);
        return login;
    }*/

    public Optional<User> getLogin(User login) {
        log.debug("getLogin: {}", login);

        return repository.find("Select e from User e where e.name = ?1 or e.email = ?2", login.getName(),
                login.getEmail()).stream().findFirst();
    }

/*    public List<User> getAllUsers() {
        log.debug("getAllUsers");

        return repository.findAll().stream().toList();
    }*/

/*    @Transactional
    public void updateUserRole(User update) throws UserNotFoundException {
        log.debug("updateUserRole: {}", update);

        Optional<User> user = repository.findByIdOptional(Long.valueOf(update.getId()));
        if (user.isEmpty()) {
            throw new UserNotFoundException("User was not found!");
        }
        user.get().setRole(update.getRole());
        repository.getEntityManager().merge(user.get());
    }*/

/*    public User getCurrentUser(String token) {
        log.debug("getCurrentUser: {}", token);

        token = token.replace("Basic ", "");
        String[] split2 = token.split("\\[");
        String token2 = split2[0];
        String decoded = new String(decoder.decode(token2));
        String[] split = decoded.split(":");
        Optional<User> exists = getLogin(new User(split[0], split[1]));
        return exists.orElse(null);
    }*/

/*    public User getUserByCustomerId(Integer id) {
        log.debug("getUser: {}", id);

        Optional<User> user = repository.list("Select e from User e where e.customer.id =?1", id).stream().findFirst();
        return user.orElse(null);
    }*/

    @Transactional
    public String registerUserAsCustomer(User userFromDto, String firstName, String lastName, String address, String email)
            throws UserConflictException, CustomerConflictException {
        log.debug("registerUserAsCustomer: {} {} {} {}", userFromDto, firstName, lastName, address);

        User user = repository.findById(Long.valueOf(userFromDto.getId()));
        if (user.getCustomer() == null) {
            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddress(address);
            Customer persisted = customerPersistenceService.persist(customer);
            user.setEmail(email);
            user.setCustomer(persisted);
            user.setRole(Roles.CUSTOMER);
            user = repository.getEntityManager().merge(user);
            return sendAuthString(user);
        } else {
            throw new UserConflictException("User is already a registered customer!");
        }
    }

    public String sendAuthString(User user) {
        log.debug("sendAuthString: {}", user);

        String authString = user.getName()+ ":" + user.getPassword();
        authString = encoder.encodeToString(authString.getBytes());
        authString = authString + "[" + encoder.encodeToString(user.getRole().toString().getBytes()) + "]";
        return authString;
    }
}
