package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "messageType", target = "messageType")
    ChatGetDTO convertEntitytoChatGetDTO(Message message);

    @Mapping(source = "message", target = "message")
    Message convertChatPostDTOtoEntity(ChatPostDTO chatPostDTO);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "avatar", target = "avatar")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "lobby", target = "lobby")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "lobbyname", target = "lobbyname")
    Lobby convertLobbyPostDTOtoEntity(LobbyPostDTO lobbyPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "lobbyname", target = "lobbyname")
    @Mapping(source = "lobbyToken", target = "lobbyToken")
    @Mapping(source = "lobbyState", target = "lobbyState")
    @Mapping(source = "lobbyType", target = "lobbyType")
    @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
    @Mapping(source = "playerList", target = "playerList")
    @Mapping(source = "adminToken", target = "adminToken")
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "currentRound", target = "currentRound")
    @Mapping(source = "guesser", target = "guesser")
    @Mapping(source = "voteList", target = "voteList")
    GameGetDTO convertEntityToGameGetDTO(Game game);


}
