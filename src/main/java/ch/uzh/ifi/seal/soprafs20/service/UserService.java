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
// if one want to run in dev mode set this code in total score Arrays.asList(new String[]{"Simon", "Thanh", "Chris",
// "Marc", "Ivan"}).contains(newUser.getUsername()) ? 1000 : 0 to start with some points inited
        newUser.setTotalScore(0);
        newUser.setGuessesCorrect(0);
        newUser.setGamesPlayed(0);
        newUser.setGaveClue(false);
        newUser.setGuessesMadeLife(0);
        newUser.setGuessesCorrectLife(0);
        newUser.setTotalCluesLife(0);
        newUser.setInvalidCluesLife(0);
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
     * @param userToBeCreated this Entity will be check if it exist in the database
     * @throws org.springframework.web.server.ResponseStatusException if nothing can be found throw exception
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
    public String loginUser(User user) {

        User repoUser = userRepository.findByUsername(user.getUsername());

        String baseErrorMessage = "Login unsuccessful.";
        if (repoUser == null || repoUser.getStatus().equals(UserStatus.ONLINE)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, baseErrorMessage);
        }
        else {
            repoUser.setStatus(UserStatus.ONLINE);
            leaveGame(repoUser);
            leaveLobby(repoUser);
            return repoUser.getToken();
        }
    }


    //logut a user
    public void logoutUser(String token) {

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
        if (updatedUser.getAvatar() != null) repoUser.setAvatar(updatedUser.getAvatar());
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
        user.setTotalClues(0);
        user.setInvalidClues(0);
        user.setGuessesCorrect(0);
        user.setGuessesMade(0);
        user.setLobbyReady(false);
    }

    public void leaveLobby(User user) {
        user.setLobby(null);
        user.setLobbyReady(false);
    }

    private long getUserCurrentTabCycle(String userToken) {
        return getUserFromToken(userToken).getIsInGameTabCycle();
    }

    public void setUserInGameTab(String userToken, boolean b) {
        User user = getUserFromToken(userToken);
        user.setInGameTab(b);
        userRepository.save(user);
    }

    public void updateIsInGameTab(String userToken) {
        long currentCycle = getUserCurrentTabCycle(userToken);
        currentCycle++;
        User user = getUserFromToken(userToken);
        user.setIsInGameTabCycle(currentCycle);
        user.setInGameTab(true);
        long finalCurrentCycle = currentCycle;
        new Thread(() -> {
            try {
                Thread.sleep(15000);
                if (finalCurrentCycle >= getUserCurrentTabCycle(userToken)) setUserInGameTab(userToken, false);
            }
            catch (InterruptedException e) {
                log.error("Thread interrupted: ", e);
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cant be updated anymore.");
            }
        }).start();
    }

}
