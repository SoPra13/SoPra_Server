# Just One Web App - SoPra Group 13

This project started as part of the SoPra course. We wanted to create a web application for the 
popular board game Just One. The goal of the project is to allow people all over the world to play with each other.
additionally we wanted to create a competitive environment. To achieve that we implemented a automated scoring system
that uses NLP to check the validity of clues and a JPA database to store all scores of every player over all played 
games. The scores of all players are ranked on a public leaderboard. If a group wants more players they can add bots
to the game. The bots use NLP to give either good or bad clues.

## Getting started with Spring Boot and JPA

- Spring Boot
    -   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
    -   Guides: http://spring.io/guides
        -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
        -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/
- Java Persistence API (JPA)
    - Documentation and Guides: https://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm

## Setup and Run this Project
1. Clone and import the repository to your local machine with your IDE of choice
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`


### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

## Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing

Have a look here: https://www.baeldung.com/spring-boot-testing

## High-level components
### These are our main high-level components
[WordService](src/test/java/ch/uzh/ifi/seal/soprafs20/service/WordServiceTest.java): Datamuse API connection to handle all NLP requierments used in this Project.
[GameService](src/main/java/ch/uzh/ifi/seal/soprafs20/service/GameService.java): Responsible for handling parts of the gamelogic that is not handled in Unity(Frontend)
[BotSericve](src/main/java/ch/uzh/ifi/seal/soprafs20/service/BotService.java): Respnsible for the logic of the bots
[LobbyService](src/main/java/ch/uzh/ifi/seal/soprafs20/service/LobbyService.java): Responsible for handling the logic for the lobbies.

### Relation between High-level Components
The most important component is our LobbyService. It is used to bring players together, in one Lobby entity. After this it handles the generation of a Game entity together with the GameService. The GameService also handles everthing relating to the Game entities. From leaving players to, receiving guesses or clues, to ending the game and setting the stats for each user. The BotService handles the creation of different Bot entities, as well as their clue giving and voting. For checking the words sent to the backend, we used the WordService class. If sends the guesses and clues to DataMuse for further processing. 

## RoadMap
Possible features:
- additional language NLP
- rejoin session after disconnect
- guest mode for spectactors
- WebSockets instead of RESTAPI

## Authors 
Simon Padua

Chris Aeberhard

Thanh Huynh
   
Ivan Allinckx
   
Marc Kramer
    
## License
Copyright (c) [2020] [Sopra Group 13]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
