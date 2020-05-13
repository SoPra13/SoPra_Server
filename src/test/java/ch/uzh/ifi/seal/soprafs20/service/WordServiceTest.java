package ch.uzh.ifi.seal.soprafs20.service;

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
        String valid = "mathematics";
        String animal = "pygmy";
        String foreign = "fremdschämen";
        String number = "1337";
        String specialChar = "(*__*)";
        String twoWords = "hallo world";
        String concatenated = "nospaces";
        String space = " ";
        String empty = "";

//        assertTrue(WordService.isValidWord());
        assertTrue(WordService.isValidWord(valid));
        assertTrue(WordService.isValidWord(animal));
        assertFalse(WordService.isValidWord(foreign));
        assertFalse(WordService.isValidWord(number));
        assertFalse(WordService.isValidWord(specialChar));
        assertFalse(WordService.isValidWord(twoWords));
        assertFalse(WordService.isValidWord(concatenated));
        assertFalse(WordService.isValidWord(space));
        assertFalse(WordService.isValidWord(empty));

    }
}