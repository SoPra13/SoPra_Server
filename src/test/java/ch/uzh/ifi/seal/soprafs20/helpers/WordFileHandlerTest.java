package ch.uzh.ifi.seal.soprafs20.helpers;

import ch.uzh.ifi.seal.soprafs20.service.WordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordFileHandlerTest {

    @Mock
    Paths paths;

    static final Logger log = LoggerFactory.getLogger(WordFileHandler.class);


    @Test
    void getMysteryWords_success() {
        List<String> wordlist = WordFileHandler.getMysteryWords();
        assertTrue(65 <= wordlist.size());
    }
    

    @Test
    void getMysteryWords_wrongPath() throws IOException {
        Path original = Paths.get("src/Cards_serious_words-EN.txt");
        FileChannel openChannel = FileChannel.open(original, StandardOpenOption.APPEND);
        openChannel.lock();

        List<String> wordlist = WordFileHandler.getMysteryWords();

        openChannel.close();

        assertTrue(wordlist.isEmpty());
    }
}