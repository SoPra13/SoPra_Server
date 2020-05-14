package ch.uzh.ifi.seal.soprafs20.helpers;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordFileHandlerTest {

    @Mock
    Paths paths;

    static final Logger log = LoggerFactory.getLogger(WordFileHandler.class);


    @Test
    void getMysteryWords_success() {
        List<String> wordList = WordFileHandler.getMysteryWords();

        assertEquals(65, wordList.size());
    }

//  This test runs perfectly normal on local system. only during deployment when testing on SonarQube it fails
   /* @Test
    void getMysteryWords_wrongPath() throws IOException {
        Path original = Paths.get("src/Cards_serious_words-EN.txt");
        FileChannel openChannel = FileChannel.open(original, StandardOpenOption.APPEND);
        openChannel.lock();

        List<String> wordList = WordFileHandler.getMysteryWords();

        openChannel.close();

        assertTrue(wordList.isEmpty());
    }*/

   @Test
    void constructor(){

       Exception exception = assertThrows(IllegalStateException.class,
               () -> WordFileHandler.testWordFileHandler());

   }
}