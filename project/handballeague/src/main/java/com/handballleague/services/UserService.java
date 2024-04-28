package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.User;
import com.handballleague.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements HandBallService<User> {
    private final UserRepository userRepository;
    private final JWTService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public User create(User entity) {
        if (entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if (checkIfEntityExistsInDb(entity))
            throw new EntityAlreadyExistsException("User with given data already exists in database");
        if (userRepository.existsByEmail(entity.getEmail())) {
            throw new EntityAlreadyExistsException("User with given data already exists in database");
        }
        if (entity.getEmail().isEmpty() ||
                entity.getPassword().isEmpty() ||
                entity.getRole().isEmpty())
            throw new InvalidArgumentException("At least one of user parameters is invalid.");
        entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));
        return userRepository.save(entity);
    }

    public String logInUser(User entity) {
        User u = userRepository.findByEmail(entity.getEmail());
        if (u == null) {
            throw new ObjectNotFoundInDataBaseException("User does not exist.");
        }
        String password = u.getPassword();
        if (BCrypt.checkpw(entity.getPassword(), password)) {
            return jwtService.generateToken(u);
        } else {
            throw new InvalidArgumentException("Wrong password!");
        }
    }

    @Override
    public boolean delete(Long id) {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundInDataBaseException("User with id: " + id + " not found in database.");
        }
        return true;
    }

    @Override
    public User update(Long id, User entity) {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");
        if (entity == null)
            throw new InvalidArgumentException("New user is null.");
        if (entity.getEmail().isEmpty() ||
                entity.getPassword().isEmpty() ||
                entity.getRole().isEmpty())
            throw new InvalidArgumentException("At least one of user parameters is invalid.");

        User userToChange = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("User with given id was not found in database."));

        userToChange.setEmail(entity.getEmail());
        userToChange.setRole(entity.getRole());
        userToChange.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));

        return userRepository.save(userToChange);
    }

    @Override
    public User getById(Long id) {
        if (id <= 0)
            throw new InvalidArgumentException("Passed id is invalid.");

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ObjectNotFoundInDataBaseException("Object with given id was not found in database.");

        return optionalUser.get();
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean checkIfEntityExistsInDb(User entity) {
        Iterable<User> users = userRepository.findAll();

        for (User u : users) {
            if (users.equals(u)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return userRepository.findAll().stream().filter(user -> user.getUuid().equals(entityID)).toList().size() == 1;
    }

}
