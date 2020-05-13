package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setUnityReady(false);
        newUser.setLobbyReady(false);
        newUser.setVoted(false);
        newUser.setInvalidClues(0);
        newUser.setTotalClues(0);
        newUser.setInvalidClues(0);
        newUser.setGuessesMade(0);
        newUser.setDuplicateClues(0);
        newUser.setTotalScore(0);
        newUser.setGuessesCorrect(0);
        newUser.setGamesPlayed(0);
        newUser.setGaveClue(false);
        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */

    //checks if user exists
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }


    //get user from token
    public User getUserFromToken(String token) {
        User userByToken = userRepository.findByToken(token);

        String baseErrorMessage = "There is no User with requested Token";
        if (userByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }
        return userByToken;
    }


    //log in a user
    public String LoginUser(User user) {

        User repoUser = userRepository.findByUsername(user.getUsername());

        String baseErrorMessage = "Login unsuccessful.";
        if ((repoUser.getUsername().equals(user.getUsername())) && (repoUser.getPassword().equals(user.getPassword()))) {
            repoUser.setStatus(UserStatus.ONLINE);
            return repoUser.getToken();
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseErrorMessage);
        }
    }


    //logut a user
    public void LogoutUser(String token) {

        User repoUser = userRepository.findByToken(token);

        String baseErrorMessage = "Logout unsuccessful.";
        if (repoUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }
        repoUser.setStatus(UserStatus.OFFLINE);
    }

    //update attributes of user
    public void updateUser(User updatedUser) {
        User repoUser = userRepository.findByToken(updatedUser.getToken());
        if (repoUser == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        if (updatedUser.getUsername() != null) repoUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getPassword() != null) repoUser.setPassword(updatedUser.getPassword());
    }

    public void resetUser(User user) {
        user.setGaveClue(false);
        user.setVoted(false);

    }

    public void leaveGame(User user) {

        user.setGame(null);
        user.setUnityReady(false);
        user.setGaveClue(false);
        user.setVoted(false);
        user.setDuplicateClues(0);
        user.setTotalClues(0);
        user.setInvalidClues(0);
        user.setGuessesCorrect(0);
        user.setGuessesMade(0);
        user.setDuplicateClues(0);
    }

    public void leaveLobby(User user) {
        user.setLobby(null);
        user.setLobbyReady(false);
    }

}
