package ch.uzh.ifi.seal.soprafs20.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordFileHandler {


    //reads random Block of 5 words from txt file and puts them into a list
    public static List<String> getMysteryWords() {
        List<String> clues = new ArrayList<>();
        try {
            List<String> file_lines = Files.readAllLines(Paths.get("src/Cards_serious_words-EN.txt"));
            while (clues.size() < 65) {
                int startLine = new Random().nextInt(936);
                startLine = startLine * 6;
                for (int i = startLine; i <= startLine + 4; i++) {
                    String word = file_lines.get(i);
                    if (clues.contains(word)) {
                        break;
                    }
                    clues.add(word);
                }
            }

            System.out.println(clues);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return clues;
    }


}
