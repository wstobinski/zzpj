package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Player;
import com.handballleague.model.Referee;
import com.handballleague.model.User;
import com.handballleague.repositories.PlayerRepository;
import com.handballleague.repositories.RefereeRepository;
import com.handballleague.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements HandBallService<User> {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PlayerRepository playerRepository;
    private final RefereeRepository refereeRepository;

    @Autowired
    public UserService(UserRepository userRepository, JWTService jwtService, PlayerRepository playerRepository, RefereeRepository refereeRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.playerRepository = playerRepository;
        this.refereeRepository = refereeRepository;
    }

    public int generateCode() {
        Random random = new Random();

        int randomNumber = 100000 + random.nextInt(900000);

        return randomNumber;
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
                entity.getRole().isEmpty())
            throw new InvalidArgumentException("At least one of user parameters is invalid.");

        if (entity.getRole().equals("captain")) {
            Player captain = playerRepository.findByEmail(entity.getEmail());
            entity.setModelId(captain.getUuid());
        } else if (entity.getRole().equals("arbiter")) {
            Referee referee = refereeRepository.findByEmail(entity.getEmail());
            entity.setModelId(referee.getUuid());
        }
        entity.setCode(generateCode());
        entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));
        return userRepository.save(entity);
    }

    public String logInUser(User entity) {
        User u = userRepository.findByEmail(entity.getEmail()).orElseThrow(() -> new ObjectNotFoundInDataBaseException("User with given email does not exist in database"));
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

    public User changePassword(String email, String oldPassword, String newPassword){
        if (email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) throw new InvalidArgumentException("At least one of parameters is invalid.");
        User userToChange = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("User with given email was not found in database."));
        if (BCrypt.checkpw(oldPassword, userToChange.getPassword())) {
            userToChange.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            return userRepository.save(userToChange);
        } else {
            throw new InvalidArgumentException("Old password is invalid.");
        }
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

    public User getByEmail(String email) {
        if (email == null || email.isEmpty())
            throw new InvalidArgumentException("Passed email is invalid.");

        Optional<User> optionalUser = userRepository.findByEmail(email);
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
