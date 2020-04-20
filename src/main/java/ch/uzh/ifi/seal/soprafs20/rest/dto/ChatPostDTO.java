package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class ChatPostDTO {

    private String username;
    private String message;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
