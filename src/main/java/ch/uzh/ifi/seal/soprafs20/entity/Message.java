package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.MessageType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = true)
    private String username;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private MessageType messageType;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
