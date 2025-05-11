package edu.hassan;

import java.util.HashMap;
import java.util.Map;
public class WordCountServiceImpl implements WordCountService {
    @Override
    public Map<String, Integer> countWords(String textChunk) {
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = textChunk.split("\\W+");
        for (String word : words) {
            if (word.isEmpty()) continue;
            word = word.toLowerCase();
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        return wordCount;
    }
}