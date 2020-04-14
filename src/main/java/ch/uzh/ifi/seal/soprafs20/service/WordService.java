package ch.uzh.ifi.seal.soprafs20.service;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class WordService {
    private static ArrayList<LinkedTreeMap<String, String>> getRequest(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return new Gson().fromJson(response.body(), new TypeToken<ArrayList<LinkedTreeMap<String, String>>>(){}.getType());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<LinkedTreeMap<String, String>>();
        }
    }
    private static ArrayList<LinkedTreeMap<String, String>> trimArraylist(ArrayList<LinkedTreeMap<String, String>> oldList, int trimTo) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<LinkedTreeMap<String, String>>();
        if (oldList.size() > trimTo) {
            for (int i = 0; i < trimTo; i++) {
                newList.add(oldList.get(i));
            }
            return newList;
        }
        return oldList;
    }

    private static ArrayList<LinkedTreeMap<String, String>> removeMultipleWords(ArrayList<LinkedTreeMap<String, String>> oldList) {
        ArrayList<LinkedTreeMap<String, String>> newList = new ArrayList<LinkedTreeMap<String, String>>();
        for (LinkedTreeMap<String, String> word :oldList) {
            if (!word.get("word").contains(" ")) {
                newList.add(word);
            }
        }
        return newList;
    }


    private static boolean isSimilar(String word1, String word2) {
        if (word1.contains(word2) || word2.contains(word1)) {
            return true;
        }

    }

    private static ArrayList<LinkedTreeMap<String, String>> getPossibleWords(ArrayList<LinkedTreeMap<String, String>> oldList) {
        return trimArraylist(removeMultipleWords(oldList),5);
    }

    public static String getGoodWord(String word) {
        ArrayList<LinkedTreeMap<String, String>> wordList = new ArrayList<LinkedTreeMap<String, String>>();
        String domain = "http://api.datamuse.com/words?rel_spc=";
        System.out.println(removeMultipleWords(getRequest(domain + word)));

        return "dora";
    }

    public static void getBadWord(String word) {

    }

    public static void test() {
        System.out.println(getGoodWord("sun"));
    }


}
