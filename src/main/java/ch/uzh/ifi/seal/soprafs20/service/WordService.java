package ch.uzh.ifi.seal.soprafs20.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WordService {
    private static final String GET_WORD = "https://api.datamuse.com/words?md=d&max=1&sp=";
    private static final String DEF_HEADWORD = "defHeadword";
    private static final Logger log = LoggerFactory.getLogger(WordService.class);

    private WordService() {
        throw new IllegalStateException("Utility class");
    }

    public static void testWordService() {
        new WordService();
    }

    private static ArrayList<LinkedTreeMap<String, String>> getRequest(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), new TypeToken<ArrayList<LinkedTreeMap<String, Object>>>() {
            }.getType());
        }
        catch (Exception e) {
            String err = e.getMessage();
            log.error(err);
            return new ArrayList<>();
        }
    }

/*
The two methodes trimArrayList() and removeAllSimilarWordsFromRequest() where not use. the can both be retrieved
from the last functional commit: 81a7cd97dfaa31a4c50e7ccb42a535b80c3fb941
*/

    private static ArrayList<LinkedTreeMap<String, String>> removeMultiWords(ArrayList<LinkedTreeMap<String, String>> oldList) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<>();
        for (LinkedTreeMap<String, String> word : oldList) {
            if (!word.get("word").contains(" ")) {
                newList.add(word);
            }
        }
        return newList;
    }


    public static boolean isSimilar(String word1, String word2, List<LinkedTreeMap<String, String>> word1request, List<LinkedTreeMap<String, String>> word2request) {
        return (word1.startsWith(word2) || word2.startsWith(word1) || isPlural(word1, word2, word1request, word2request) || isSameFamily(word1, word2));
    }

    public static boolean isSimilar(String word1, String word2) {
        return (word1.startsWith(word2) || word2.startsWith(word1) || isPlural(word1, word2) || isSameFamily(word1, word2));
    }

    private static boolean isSameFamily(String word1, String word2) {
        return (word1.length() >= 5 && word2.length() >= 5 && word1.substring(0, 5).equals(word2.substring(0, 5)));
    }

    private static boolean isPlural(String word1, String word2) {
        ArrayList<LinkedTreeMap<String, String>> word1request = getRequest((GET_WORD + word1));
        ArrayList<LinkedTreeMap<String, String>> word2request = getRequest((GET_WORD + word2));
        return isPluralTest(word1, word2, word1request, word2request);

    }

    private static boolean isPlural(String word1, String word2, List<LinkedTreeMap<String, String>> word1request, List<LinkedTreeMap<String, String>> word2request) {
        return isPluralTest(word1, word2, word1request, word2request);
    }

    private static boolean isPluralTest(String word1, String word2, List<LinkedTreeMap<String, String>> word1request, List<LinkedTreeMap<String, String>> word2request) {
        if (word1request.get(0).containsKey(DEF_HEADWORD) && word1request.get(0).get(DEF_HEADWORD).equals(word2)) {
            return true;
        }
        else if (word2request.get(0).containsKey(DEF_HEADWORD) && word2request.get(0).get(DEF_HEADWORD).equals(word1)) {
            return true;
        }
        else
            return (word1request.get(0).containsKey(DEF_HEADWORD) && word2request.get(0).containsKey(DEF_HEADWORD) &&
                    word1request.get(0).get(DEF_HEADWORD).equals(word2request.get(0).get(DEF_HEADWORD)));
    }


    public static boolean[] checkSimilarityInArray(String[] words) {
        boolean[] result = new boolean[words.length];
        ArrayList<ArrayList<LinkedTreeMap<String, String>>> wordDefsAsList = new ArrayList<>();
        for (String word : words) {
            wordDefsAsList.add(getRequest((GET_WORD + word)));
        }
        for (int i = 0; i < words.length; i++) {
            if (!result[i]) {
                for (int j = i + 1; j < words.length; j++) {
                    if (!result[j] && isSimilar(words[i], words[j], wordDefsAsList.get(i), wordDefsAsList.get(j))) {
                        result[i] = true;
                        result[j] = true;
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
        return getWord(word, wordList);
    }

    public static String getBadWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> wordList =
                new ArrayList<>(getRequest("http://api.datamuse.com/words?max=5&rel_ant=" + word));
        wordList.addAll(getRequest("http://api.datamuse.com/words?max=5&rel_nry=" + word));
        return getWord(word, wordList);

    }

    private static String getWord(String word, ArrayList<LinkedTreeMap<String, String>> wordList) {
        if (wordList.isEmpty()) return word;

        wordList = removeMultiWords(wordList);
        String wordGet = wordList.get(new SecureRandom().nextInt(wordList.size())).get("word");
        while (isSimilar(word, wordGet))
            wordGet = wordList.get(new SecureRandom().nextInt(wordList.size())).get("word");


        return wordGet;
    }

    public static boolean isValidWord(String word) {
        if (Pattern.compile("([^\\x41-\\x5A\\x61-\\x7A])").matcher(word).find())
            return false; // needed since datamuse accepts pure numbers
        ArrayList<LinkedTreeMap<String, String>> compareWord = getRequest(
                "https://api.datamuse.com/words?max=1&sp=" + word);
        return !compareWord.isEmpty() && compareWord.get(0).get("word").equals(word);
    }

}
