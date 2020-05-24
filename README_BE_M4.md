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

## Contributing
When contributing to this repository, please first discuss the change you wish to make via issue, email, or any other method with the owners of this repository before making a change.

Please note we have a code of conduct, please follow it in all your interactions with the project.

#### Pull Request Process
1. Ensure any install or build dependencies are removed before the end of the layer when doing a build.
2. Update the README.md with details of changes to the interface, this includes new environment variables, exposed ports, useful file locations and container parameters.
3. Increase the version numbers in any examples files and the README.md to the new version that this Pull Request would represent. The versioning scheme we use is [SemVer](https://semver.org/).
4.You may merge the Pull Request in once you have the sign-off of two other developers, or if you do not have permission to do that, you may request the second reviewer to merge it for you.
Code of Conduct
#### Our Pledge
In the interest of fostering an open and welcoming environment, we as contributors and maintainers pledge to making participation in our project and our community a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity and expression, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

#### Our Standards
Examples of behavior that contributes to creating a positive environment include:

- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members
Examples of unacceptable behavior by participants include:

- The use of sexualized language or imagery and unwelcome sexual attention or advances
- Trolling, insulting/derogatory comments, and personal or political attacks
- Public or private harassment
- Publishing others' private information, such as a physical or electronic address, without explicit permission
- Other conduct which could reasonably be considered inappropriate in a professional setting
#### Our Responsibilities
Project maintainers are responsible for clarifying the standards of acceptable behavior and are expected to take appropriate and fair corrective action in response to any instances of unacceptable behavior.

Project maintainers have the right and responsibility to remove, edit, or reject comments, commits, code, wiki edits, issues, and other contributions that are not aligned to this Code of Conduct, or to ban temporarily or permanently any contributor for other behaviors that they deem inappropriate, threatening, offensive, or harmful.

#### Scope
This Code of Conduct applies both within project spaces and in public spaces when an individual is representing the project or its community. Examples of representing a project or community include using an official project e-mail address, posting via an official social media account, or acting as an appointed representative at an online or offline event. Representation of a project may be further defined and clarified by project maintainers.

#### Enforcement
Instances of abusive, harassing, or otherwise unacceptable behavior may be reported by contacting the project team at sopra13group@gmail.com. All complaints will be reviewed and investigated and will result in a response that is deemed necessary and appropriate to the circumstances. The project team is obligated to maintain confidentiality with regard to the reporter of an incident. Further details of specific enforcement policies may be posted separately.

Project maintainers who do not follow or enforce the Code of Conduct in good faith may face temporary or permanent repercussions as determined by other members of the project's leadership.

#### Attribution
This Code of Conduct is adapted from the [Contributor Covenant](https://www.contributor-covenant.org/), version 1.4, available at https://www.contributor-covenant.org/version/1/4/code-of-conduct/


## Authors 
Simon Padua (IsaacTheII)

Chris Aeberhard (niddhog)

Thanh Huynh (Tharoxes)
   
Ivan Allinckx   (iallin)
   
Marc Kramer (Makram95)
    
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
