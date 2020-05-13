package ch.uzh.ifi.seal.soprafs20.helpers;

import ch.uzh.ifi.seal.soprafs20.service.WordService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {

    @Test
    void testWordsInWordList() throws IOException {
        List<String> file_lines = Files.readAllLines(Paths.get("src/Cards_serious_words-EN.txt"));

//        Enable this loop to check the whole word list
/*        System.out.println("This may take a few minutes!");
        for(String word : file_lines){
            if(!word.isEmpty()) {
                if(WordService.isValidWord(word.toLowerCase())) assertTrue(WordService.isValidWord(word.toLowerCase()));
            }
        }*/
        assertTrue(WordService.isValidWord(file_lines.get(0).toLowerCase()));
    }
}