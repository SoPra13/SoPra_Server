package ch.uzh.ifi.seal.soprafs20.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public final class WordFileHandler {
    static final Logger log = LoggerFactory.getLogger(WordFileHandler.class);

    private WordFileHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static void testWordFileHandler() {
        new WordFileHandler();
    }

    //reads random Block of 5 words from txt file and puts them into a list
    public static List<String> getMysteryWords() {
        List<String> clues = new ArrayList<>();
        List<String> fileLines = null;
        try {
            fileLines = Files.readAllLines(Paths.get("src/Cards_serious_words-EN.txt"));
        }
        catch (IOException ex) {
            log.error("file could not be read", ex);
            return clues;
        }
        List<Integer> cards = new ArrayList<>();
        while (cards.size() < 13) cards.add(new SecureRandom().nextInt(fileLines.size() / 6));
        for (int i : cards) {
            for (int j = i * 6; j <= i * 6 + 4; j++) clues.add(fileLines.get(j));
        }
        if (clues.size() != 65) log.error("src/Cards_serious_words-EN.txt", new EOFException());
        String msg = clues.toString();
        log.info(msg);
        return clues;
    }
}
