package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.helpers.WordFileHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordServiceTest {

    @Test
    void isSimilar() {
        boolean yes = WordService.isSimilar("similar", "similarity");
        boolean no = WordService.isSimilar("something", "different");

        assertTrue(yes);
        assertFalse(no);

    }

    @Test
    void checkSimilarityInArray() {
        String[] wordList = new String[]{"similar", "similarity", "different"};
        boolean[] expected = new boolean[]{true, true, false};

        boolean[] similarWords = WordService.checkSimilarityInArray(wordList);

        assertArrayEquals(expected, similarWords);
    }

    @Test
    void getGoodWord() {
        String word = WordService.getGoodWord("word");

        assertNotNull(word);
    }

    @Test
    void getBadWord() {
        String word = WordService.getBadWord("word");

        assertNotNull(word);
    }

    @Test
    void isValidWord() {
        String valid = "english";
        String animal = "pygmy";
        String science = "apoptosis";
        String odd = "kerfuffle";
        String foreign = "fremdsprache";
        String number = "1337";
        String specialChar = "(*__*)";
        String twoWords = "hallo world";
        String concatenated = "nospaces";
        String space = " ";
        String empty = "";
        String format = "\t";
        String end = "\n";

        assertTrue(WordService.isValidWord(valid));
        assertTrue(WordService.isValidWord(animal));
        assertTrue(WordService.isValidWord(science));
        assertTrue(WordService.isValidWord(odd));
        assertFalse(WordService.isValidWord(foreign));
        assertFalse(WordService.isValidWord(number));
        assertFalse(WordService.isValidWord(specialChar));
        assertFalse(WordService.isValidWord(twoWords));
        assertFalse(WordService.isValidWord(concatenated));
        assertFalse(WordService.isValidWord(space));
        assertFalse(WordService.isValidWord(empty));
        assertFalse(WordService.isValidWord(format));
        assertFalse(WordService.isValidWord(end));
    }

    @Test
    void constructor(){

        Exception exception = assertThrows(IllegalStateException.class,
                () -> WordService.testWordService());

    }
}