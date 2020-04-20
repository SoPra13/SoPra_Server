package ch.uzh.ifi.seal.soprafs20.entity;

import java.util.LinkedList;

public class Chat {
    private Lobby lobby;
    private LinkedList<Message> messages;
    Chat(Lobby lobby) {
        this.lobby = lobby;
        this.messages = new LinkedList<Message>();
    }
}
