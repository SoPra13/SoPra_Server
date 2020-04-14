package ch.uzh.ifi.seal.soprafs20.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WordService<Booelan> {
    private static ArrayList<LinkedTreeMap<String, String>> getRequest(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), new TypeToken<ArrayList<LinkedTreeMap<String, Object>>>() {}.getType());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<LinkedTreeMap<String, String>>();
        }
    }

    private static ArrayList<LinkedTreeMap<String, String>> trimArrayList(ArrayList<LinkedTreeMap<String, String>> oldList, int trimTo) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<LinkedTreeMap<String, String>>();
        if (oldList.size() > trimTo) {
            for (int i = 0; i < trimTo; i++) {
                newList.add(oldList.get(i));
            }
            return newList;
        }
        return oldList;
    }

    private static ArrayList<LinkedTreeMap<String, String>> removeAllSimilarWordsFromRequest(String similarWordToRemove, ArrayList<LinkedTreeMap<String, String>> oldList) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<LinkedTreeMap<String, String>>();
        for (LinkedTreeMap<String, String> word : oldList) {
            if (!isSimilar(word.get("word"), similarWordToRemove)) {
                newList.add(word);
            }
        }
        return newList;
    }

    private static ArrayList<LinkedTreeMap<String, String>> removeMultiWords(ArrayList<LinkedTreeMap<String, String>> oldList) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<LinkedTreeMap<String, String>>();
        for (LinkedTreeMap<String, String> word : oldList) {
            if (!word.get("word").contains(" ")) {
                newList.add(word);
            }
        }
        return newList;
    }


    public static boolean isSimilar(String word1, String word2) {
        return (word1.startsWith(word2) || word2.startsWith(word1) || isPlural(word1, word2) || isSameFamily(word1, word2));
    }

    private static boolean isSameFamily(String word1,String word2) {
        return (word1.length() >= 5 && word2.length() >= 5 && word1.substring(0, 4).equals(word2.substring(0, 4)));
    }
    private static boolean isPlural(String word1, String word2) {
        ArrayList<LinkedTreeMap<String, String>> word1request = getRequest(("https://api.datamuse.com/words?md=d&max=1&sp=" + word1));
        ArrayList<LinkedTreeMap<String, String>> word2request = getRequest("https://api.datamuse.com/words?md=d&max=1&sp=" + word2);
        if (word1request.get(0).containsKey("defHeadword") && word1request.get(0).get("defHeadword").equals(word2)) {
            return true;
        }
        else if (word2request.get(0).containsKey("defHeadword") && word2request.get(0).get("defHeadword").equals(word1)) {
            return true;
        }
        else
            return word1request.get(0).containsKey("defHeadword") && word2request.get(0).containsKey("defHeadword") &&
                    word1request.get(0).get("defHeadword").equals(word2request.get(0).get("defHeadword"));
    }


    public static boolean[] checkSimilarityInArray(String[] words) {
        boolean[] result = new boolean[words.length];
        for (int i = 0; i < words.length; i++) {
            if (!result[i]) {
                for (int j = i + 1; j < words.length; j++) {
                    if (!result[j]) {
                        if (isSimilar(words[i], words[j])) {
                            result[i] = true; result[j] = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String getGoodWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> wordList = new ArrayList<LinkedTreeMap<String, String>>();
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_spc=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_trg=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_gen=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_com=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_par=" + word));
        wordList = removeMultiWords(wordList);
        String goodWord = wordList.get(new Random().nextInt(wordList.size())).get("word");
        while (isSimilar(goodWord, word)) {
            goodWord = wordList.get(new Random().nextInt(wordList.size())).get("word");
        }
        return wordList.get(new Random().nextInt(wordList.size())).get("word");
    }

    public static String getBadWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> wordList =
                new ArrayList<LinkedTreeMap<String, String>>(getRequest("http://api.datamuse.com/words?max=5&rel_spc=" + word));
        wordList = removeMultiWords(wordList);
        String badWord = wordList.get(new Random().nextInt(wordList.size())).get("word");
        while (isSimilar(badWord, word)) {
            badWord = wordList.get(new Random().nextInt(wordList.size())).get("word");
        }
        return wordList.get(new Random().nextInt(wordList.size())).get("word");

    }

    public static void test() {

    }

}
