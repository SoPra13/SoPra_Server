package ch.uzh.ifi.seal.soprafs20.helpers;

import ch.uzh.ifi.seal.soprafs20.service.WordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordFileHandlerTest {

    @Mock
    Paths paths;

    @Test
    void getMysteryWords_success() {
        List<String> wordlist = WordFileHandler.getMysteryWords();
        assertTrue(65 <= wordlist.size());
    }

/*  not possible to mock Paths.class but is also not needed anymore

    @Test
    void getMysteryWords_wrongPath() {
        Mockito.when(Mockito.mock(Paths.class).get("src/Cards_serious_words-EN.txt")).
                thenReturn(Paths.get("IOException_dummy.txt"));

        Exception exception = Assertions.assertThrows(IOException.class,
                () -> WordFileHandler.getMysteryWords());
    }*/
}