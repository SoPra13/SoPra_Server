package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.LeaderboardBy;
import ch.uzh.ifi.seal.soprafs20.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LeaderboardController {
    LeaderboardService leaderboardService;

    LeaderboardController(LeaderboardService leaderboardService){
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getLeaderboard(@RequestParam LeaderboardBy by) {
        return leaderboardService.getJsonLeaderboardByAttribute(by);
    }
}
