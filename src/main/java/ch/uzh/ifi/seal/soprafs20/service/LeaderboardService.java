package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.LeaderboardBy;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

@Service
public class LeaderboardService {

    UserRepository userRepository;

    @Autowired
    LeaderboardService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String getJsonLeaderboardByAttribute(LeaderboardBy by) {
        List<User> users = userRepository.findAll();
        users.sort(getComparator(by).reversed());
        ArrayList<TreeMap<String, String>> leaderboard = new ArrayList<>();
        int rank = 1;
        for (User user : users) {
            TreeMap<String, String> jsonUser = new TreeMap<>();
            jsonUser.put("rank", Integer.toString(rank));
            jsonUser.put("username", user.getUsername());
            jsonUser.put("result", getScore(user, by).toString());
            leaderboard.add(jsonUser);
            rank++;
        }
        return new Gson().toJson(leaderboard);
    }

    Comparator<User> getComparator(LeaderboardBy by) {
        switch (by) {
            case TOTALSCORE:
                return Comparator.comparing(User::getTotalScore);
            case GAMESPLAYED:
                return Comparator.comparing(User::getGamesPlayed);
            case GUESSESMADE:
                return Comparator.comparing(User::getGuessesMadeLife);
            case TOTALCLUES:
                return Comparator.comparing(User::getTotalCluesLife);
            case INVALIDCLUES:
                return Comparator.comparing(User::getInvalidCluesLife);
            case DUPLICATECLUES:
                return Comparator.comparing(User::getDuplicateCluesLife);
            case GUESSESCORRECT:
                return Comparator.comparing(User::getGuessesCorrectLife);
            default:
                throw new IllegalStateException("Unexpected value: " + by);
        }
    }

    Integer getScore(User user, LeaderboardBy by) {
        switch (by) {
            case TOTALSCORE:
                return user.getTotalScore();
            case GAMESPLAYED:
                return user.getGamesPlayed();
            case GUESSESMADE:
                return user.getGuessesMade();
            case TOTALCLUES:
                return user.getTotalClues();
            case INVALIDCLUES:
                return user.getInvalidClues();
            case DUPLICATECLUES:
                return user.getDuplicateClues();
            case GUESSESCORRECT:
                return user.getGuessesCorrect();
            default:
                throw new IllegalStateException("Unexpected value: " + by);
        }
    }
}

