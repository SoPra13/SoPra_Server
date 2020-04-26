package ch.uzh.ifi.seal.soprafs20.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordFileHandler {


    //reads random Block of 5 words from txt file and puts them into a list
    public static List<String> getMysteryWords(){
        List<String> clues = new ArrayList<>();
        try {

            while (clues.size()<65) {
                int startLine = new Random().nextInt(54);
                startLine = startLine*6;
                for (int i = startLine; i <= startLine + 4; i++) {
                    String specific_line_text = Files.readAllLines(Paths.get("src/cards-EN.txt")).get(i);
                    if(clues.contains(specific_line_text)){
                        break;
                    }
                    clues.add(specific_line_text);
                }
            }

            System.out.println(clues);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return clues;
    }



}
