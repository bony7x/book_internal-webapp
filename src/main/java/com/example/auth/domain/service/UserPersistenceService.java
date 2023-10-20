package com.example.auth.domain.service;

import com.example.auth.domain.entity.User;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.exception.UserNotFoundException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.domain.service.CustomerPersistenceService;
import com.example.customer.exception.CustomerConflictException;
import com.example.customer.exception.CustomerNotFoundException;
import io.quarkus.panache.common.Sort;
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
public class UserPersistenceService {

    @Inject
    AuthenticationPersistenceService authenticationPersistenceService;

    @Inject
    UserRepository repository;

    @Inject
    CustomerPersistenceService customerPersistenceService;

    @Inject
    CustomerRepository customerRepository;

    Decoder decoder = Base64.getDecoder();

    public User persist(User login) {
        log.debug("persist: {}", login);

        repository.persist(login);
        return login;
    }

    public User getCurrentUser(String token) {
        log.debug("getCurrentUser: {}", token);

        token = token.replace("Basic ", "");
        String[] split2 = token.split("\\[");
        String token2 = split2[0];
        String decoded = new String(decoder.decode(token2));
        String[] split = decoded.split(":");
        Optional<User> exists = authenticationPersistenceService.getLogin(new User(split[0], split[1]));
        return exists.orElse(null);
    }

    public User getUserByCustomerId(Integer id) {
        log.debug("getUser: {}", id);

        Optional<User> user = repository.list("Select e from User e where e.customer.id =?1", id).stream().findFirst();
        return user.orElse(null);
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

    public List<User> getAllUsers() {
        log.debug("getAllUsers");

        return repository.listAll(Sort.by("id").ascending());
    }

    public User getUser(Integer id) throws UserNotFoundException {
        log.debug("getUser: {}", id);

        Optional<User> exists = repository.findByIdOptional(Long.valueOf(id));
        if (exists.isPresent()) {
            return exists.get();
        } else {
            throw new UserNotFoundException("User not found!");
        }
    }

    @Transactional
    public void deleteUser(Integer id)
            throws CustomerConflictException, UserNotFoundException, CustomerNotFoundException {
        log.debug("deleteUser: {}", id);

        User user = getUser(id);
        if (user.getCustomer() != null) {
            Customer customer = customerPersistenceService.getCustomerById(user.getCustomer().getId());
            if (customer.getBorrowings().isEmpty()) {
                customerRepository.delete(customer);
            } else {
                throw new CustomerConflictException("Customer with active borrowings can't be deleted!");
            }
        }
        repository.delete(user);
    }
}
