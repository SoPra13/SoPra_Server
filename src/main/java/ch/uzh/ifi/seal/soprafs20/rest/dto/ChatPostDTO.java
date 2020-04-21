package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class ChatPostDTO {

    private String userToken;
    private String message;

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
