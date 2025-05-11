package edu.hassan;
import java.util.Map;
public interface WordCountService {
    Map<String, Integer> countWords(String textChunk);
}