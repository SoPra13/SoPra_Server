package ch.uzh.ifi.seal.soprafs20.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;

public class WordService {
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
            return new ArrayList<>();
        }
    }

    private static ArrayList<LinkedTreeMap<String, String>> trimArrayList(ArrayList<LinkedTreeMap<String, String>> oldList, int trimTo) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<>();
        if (oldList.size() > trimTo) {
            for (int i = 0; i < trimTo; i++) {
                newList.add(oldList.get(i));
            }
            return newList;
        }
        return oldList;
    }

    private static ArrayList<LinkedTreeMap<String, String>> removeAllSimilarWordsFromRequest(String similarWordToRemove, ArrayList<LinkedTreeMap<String, String>> oldList) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<>();
        for (LinkedTreeMap<String, String> word : oldList) {
            if (!isSimilar(word.get("word"), similarWordToRemove)) {
                newList.add(word);
            }
        }
        return newList;
    }

    private static ArrayList<LinkedTreeMap<String, String>> removeMultiWords(ArrayList<LinkedTreeMap<String, String>> oldList) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<>();
        for (LinkedTreeMap<String, String> word : oldList) {
            if (!word.get("word").contains(" ")) {
                newList.add(word);
            }
        }
        return newList;
    }


    public static boolean isSimilar(String word1, String word2, ArrayList<LinkedTreeMap<String, String>> word1request, ArrayList<LinkedTreeMap<String, String>> word2request) {
        return (word1.startsWith(word2) || word2.startsWith(word1) || isPlural(word1, word2, word1request, word2request) || isSameFamily(word1, word2));
    }

    public static boolean isSimilar(String word1, String word2) {
        return (word1.startsWith(word2) || word2.startsWith(word1) || isPlural(word1, word2) || isSameFamily(word1, word2));
    }

    private static boolean isSameFamily(String word1,String word2) {
        return (word1.length() >= 5 && word2.length() >= 5 && word1.substring(0, 5).equals(word2.substring(0, 5)));
    }

    private static boolean isPlural(String word1, String word2) {
        ArrayList<LinkedTreeMap<String, String>> word1request = getRequest(("https://api.datamuse.com/words?md=d&max=1&sp=" + word1));
        ArrayList<LinkedTreeMap<String, String>> word2request = getRequest(("https://api.datamuse.com/words?md=d&max=1&sp=" + word2));
        return isPluralTest(word1, word2, word1request, word2request);
    }

    private static boolean isPlural(String word1, String word2, ArrayList<LinkedTreeMap<String, String>> word1request, ArrayList<LinkedTreeMap<String, String>> word2request) {
        return isPluralTest(word1, word2, word1request, word2request);
    }

    private static boolean isPluralTest(String word1, String word2, ArrayList<LinkedTreeMap<String, String>> word1request, ArrayList<LinkedTreeMap<String, String>> word2request) {
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
        ArrayList<ArrayList<LinkedTreeMap<String, String>>> wordDefsAsList = new ArrayList<>();
        for (String word :words) {
            wordDefsAsList.add(getRequest(("https://api.datamuse.com/words?md=d&max=1&sp=" + word)));
        }
        for (int i = 0; i < words.length; i++) {
            if (!result[i]) {
                for (int j = i + 1; j < words.length; j++) {
                    if (!result[j]) {
                        if (isSimilar(words[i], words[j], wordDefsAsList.get(i), wordDefsAsList.get(j))) {
                            result[i] = true; result[j] = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String getGoodWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> wordList = new ArrayList<>();
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_spc=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_trg=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_gen=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_com=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=2&rel_par=" + word));
        System.out.println(wordList);
        return getWord(word, wordList);
    }

    public static String getBadWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> wordList =
                new ArrayList<>(getRequest("http://api.datamuse.com/words?max=5&rel_ant=" + word));
        return getWord(word, wordList);

    }

    private static String getWord(String word, ArrayList<LinkedTreeMap<String, String>> wordList) {
        wordList = removeMultiWords(wordList);
        String selectedWord = wordList.get(new Random().nextInt(wordList.size())).get("word");
        while (isSimilar(word, selectedWord)) {
            selectedWord = wordList.get(new Random().nextInt(wordList.size())).get("word");
        }
        return selectedWord;
    }

    public static boolean isValidWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> compareWord = getRequest("https://api.datamuse.com/words?max=1&sp=" + word);
        return !compareWord.isEmpty() && compareWord.get(0).get("word").equals(word);
    }

}
