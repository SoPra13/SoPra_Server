package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.service.LeaderboardService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderboardService leaderboardService;

    private String dummyLeaderboard(){
        ArrayList<TreeMap<String, String>> leaderboard = new ArrayList<>();
        TreeMap<String, String> jsonUser = new TreeMap<>();
        jsonUser.put("rank", Integer.toString(1));
        jsonUser.put("username", "Best");
        jsonUser.put("result", "1000");
        leaderboard.add(jsonUser);
        jsonUser = new TreeMap<>();
        jsonUser.put("rank", Integer.toString(2));
        jsonUser.put("username", "secondBest");
        jsonUser.put("result", "500");
        leaderboard.add(jsonUser);
        return new Gson().toJson(leaderboard);
    }

    @Test
    void get_getLeaderboard() throws Exception {
        given(leaderboardService.getJsonLeaderboardByAttribute(any()))
                .willReturn(dummyLeaderboard());

        MockHttpServletRequestBuilder getRequest = get("/leaderboard?by=TOTALSCORE")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank", is("1")))
                .andExpect(jsonPath("$[0].result", is("1000")))
                .andExpect(jsonPath("$[0].username", is("Best")))
                .andExpect(jsonPath("$[1].rank", is("2")))
                .andExpect(jsonPath("$[1].result", is("500")))
                .andExpect(jsonPath("$[1].username", is("secondBest")));
    }
}