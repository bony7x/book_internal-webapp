package com.example.auth.domain.service;

import com.example.auth.controller.dto.UserAndCountDto;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.entity.UserFilter;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.domain.service.CustomerPersistenceService;
import com.example.customer.exception.CustomerConflictException;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.request.ExtendedRequest;
import com.example.utils.CalculateIndex.CalculateIndex;
import com.example.utils.CalculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
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

    Encoder encoder = Base64.getEncoder();

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

    public UserAndCountDto getAllUsers(ExtendedRequest request) {
        log.debug("getAllUsers");

        List<User> sublist;
        UserAndCountDto filteredUsers;
        filteredUsers = filterUsers(request);
        if (filteredUsers == null) {
            CalculateIndex calculateIndex = new CalculateIndex();
            List<User> users = request.getSortable().isAscending() ? repository.listAll(
                    Sort.by(request.getSortable().getColumn()).ascending())
                    : repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
            Index indexes = calculateIndex.calculateIndex(request, users.size());
            sublist = users.subList(indexes.getFromIndex(), indexes.getToIndex());
            return new UserAndCountDto(users.size(), sublist);
        }
        CalculateIndex calculateIndex = new CalculateIndex();
        Index indexes = calculateIndex.calculateIndex(request, filteredUsers.getUsers().size());
        sublist = filteredUsers.getUsers().subList(indexes.getFromIndex(), indexes.getToIndex());
        return new UserAndCountDto(filteredUsers.getUsers().size(), sublist);
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

    public User updateUser(User update) throws UserNotFoundException {
        log.debug("updateUser: {}", update);

        User user = getUser(update.getId());
        if (!update.getEmail().isEmpty() && !emailExists(update.getEmail())) {
            user.setEmail(update.getEmail());
        }
        if (!update.getName().isEmpty() && !nameExists(update.getName())) {
            user.setName(update.getName());
        }
        if (!update.getPassword().isEmpty()) {
            byte[] password = decoder.decode(update.getPassword());
            user.setPassword(new String(password));
        }
        return repository.getEntityManager().merge(user);
    }

    @Transactional
    public User updateUserNameEmail(User update, Integer id) throws UserNotFoundException, UserConflictException {
        log.debug("updateUserNameEmail: {}", update);

        String email = "%" + update.getEmail().toLowerCase() + "%";
        Optional<User> userEmail = repository.list("Select e from User e where lower(e.email) like ?1", email).stream()
                .findFirst();
        Optional<Customer> customerEmail = customerRepository.list(
                "Select e from Customer e where lower(e.email) like ?1", email).stream().findFirst();
        if (userEmail.isPresent() || customerEmail.isPresent()) {
            throw new UserConflictException("Email address is already used!");
        }
        User user = getUser(id);
        user.setName(update.getName());
        user.setEmail(update.getEmail());
        if (user.getCustomer() != null) {
            user.getCustomer().setEmail(update.getEmail());
        }
        return repository.getEntityManager().merge(user);
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


    private boolean nameExists(String name) {
        log.debug("nameExists: {}", name);

        Optional<User> user = repository.list("Select e from User e where e.name = ?1", name).stream().findFirst();
        return user.isPresent();
    }

    private boolean emailExists(String email) {
        log.debug("emailExists: {}", email);

        Optional<User> user = repository.list("Select e from User e where e.email = ?1", email).stream().findFirst();
        Optional<Customer> customer = customerRepository.list("Select e from Customer e where e.email = ?1", email)
                .stream()
                .findFirst();
        return user.isPresent() || customer.isPresent();
    }

    private UserAndCountDto filterUsers(ExtendedRequest request) {
        log.debug("filterUsers: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        if (request.getFilter() != null) {
            UserFilter filter = objectMapper.convertValue(request.getFilter(), UserFilter.class);
            List<User> users;
            if (filter.getName() != null) {
                filter.setName("%" + filter.getName() + "%");
            }
            if (filter.getEmail() != null) {
                filter.setEmail("%" + filter.getEmail() + "%");
            }
            if (filter.getName() != null && filter.getEmail() != null) {
                users = repository.list("Select e from User e where lower(e.name) like ?1 and lower(e.email) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName(),
                        filter.getEmail());
                return new UserAndCountDto(users.size(), users);
            }
            if (filter.getName() != null) {
                users = repository.list("Select e from User e where lower(e.name) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName());
                return new UserAndCountDto(users.size(), users);
            }
            if (filter.getEmail() != null) {
                users = repository.list("Select e from User e where lower(e.email) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getEmail());
                return new UserAndCountDto(users.size(), users);
            }
        }
        return null;
    }
}
