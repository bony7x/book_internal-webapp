package com.example.auth.service;

import com.example.auth.controller.dto.UserAndCountDto;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.entity.UserFilter;
import com.example.auth.domain.repository.UserRepository;
import com.example.auth.domain.service.AuthenticationPersistenceService;
import com.example.auth.domain.service.UserPersistenceService;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.utils.requests.ExtendedRequest;
import com.example.utils.calculateIndex.CalculateIndex;
import com.example.utils.calculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
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
public class UserService {

    @Inject
    UserRepository repository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    UserPersistenceService userPersistenceService;

    @Inject
    AuthenticationPersistenceService authenticationPersistenceService;

    Decoder decoder = Base64.getDecoder();


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
        User user = userPersistenceService.getUser(id);
        user.setName(update.getName());
        user.setEmail(update.getEmail());
        if (user.getCustomer() != null) {
            user.getCustomer().setEmail(update.getEmail());
        }
        return repository.getEntityManager().merge(user);
    }

    @Transactional
    public String updateUserName(String name, String token, String password)
            throws UserNotFoundException, UserConflictException {
        log.debug("updateUserName: {}", name);

        User user = userPersistenceService.getCurrentUser(token);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }
        if (!user.getPassword().equals(new String(decoder.decode(password)))) {
            throw new UserConflictException("Wrong password!");
        }
        String search = "%" + name.toLowerCase() + "%";
        Optional<User> exists = repository.list("Select e from User e where lower(e.name) like ?1", search).stream()
                .findFirst();
        if (exists.isPresent()) {
            throw new UserConflictException("Username is already taken!");
        }
        user.setName(name);
        return authenticationPersistenceService.sendAuthString(user);
    }

    @Transactional
    public void updateUserEmail(String email, String token, String password)
            throws UserNotFoundException, UserConflictException {
        log.debug("updateUserEmail: {}", email);

        User user = userPersistenceService.getCurrentUser(token);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }
        if (!user.getPassword().equals(new String(decoder.decode(password)))) {
            throw new UserConflictException("Wrong password!");
        }
        String search = "%" + email.toLowerCase() + "%";
        Optional<User> exists = repository.list("Select e from User e where lower(e.email) like ?1", search).stream()
                .findFirst();
        Optional<Customer> existsC = customerRepository.list("Select e from Customer e where lower(e.email) like ?1",
                search).stream().findFirst();
        if (exists.isPresent() || existsC.isPresent()) {
            throw new UserConflictException("Email is already taken!");
        }
        user.setEmail(email);
        if (user.getCustomer() != null) {
            user.getCustomer().setEmail(email);
        }
    }

    @Transactional
    public void updateUserAddress(String address, String token, String password)
            throws UserNotFoundException, UserConflictException {
        log.debug("updateUserAddress: {}", address);

        User user = userPersistenceService.getCurrentUser(token);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }
        if (!user.getPassword().equals(new String(decoder.decode(password)))) {
            throw new UserConflictException("Wrong password!");
        }
        user.getCustomer().setAddress(address);
    }

    @Transactional
    public String updateUserPassword(String current, String newP, String token)
            throws UserNotFoundException, UserConflictException {
        log.debug("updateUserPassword: {} {} {}", current, newP, token);

        User user = userPersistenceService.getCurrentUser(token);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }
        if (!user.getPassword().equals(new String(decoder.decode(current)))) {
            throw new UserConflictException("Wrong password!");
        }
        user.setPassword(new String(decoder.decode(newP)));
        return authenticationPersistenceService.sendAuthString(user);
    }

    private UserAndCountDto filterUsers(ExtendedRequest request) {
        log.debug("filterUsers: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        if (!request.getFilter().isEmpty()) {
            UserFilter filter = objectMapper.convertValue(request.getFilter(), UserFilter.class);
            List<User> users;
            if (!filter.getName().isEmpty()) {
                filter.setName("%" + filter.getName() + "%");
            }
            if (!filter.getEmail().isEmpty()) {
                filter.setEmail("%" + filter.getEmail() + "%");
            }
            if (!filter.getName().isEmpty() && !filter.getEmail().isEmpty()) {
                users = repository.list("Select e from User e where lower(e.name) like ?1 and lower(e.email) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName(),
                        filter.getEmail());
                return new UserAndCountDto(users.size(), users);
            }
            if (!filter.getName().isEmpty()) {
                users = repository.list("Select e from User e where lower(e.name) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName());
                return new UserAndCountDto(users.size(), users);
            }
            if (!filter.getEmail().isEmpty()) {
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
