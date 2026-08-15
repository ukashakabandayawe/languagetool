package org.languagetool.tokenizers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Word tokenizer for Luganda.
 * 
 * In Luganda, the apostrophe (') is part of words:
 * 1. As part of the velar nasal consonant digraph "ng'" (e.g. Ebbiring'anya, eng'aali, eng'aano, eng'amiya, eng'uumi).
 * 2. In contractions and elisions (e.g. eky'okunywa, ow'oluganda, n'amata, y'ono, w'ali).
 * 
 * This tokenizer ensures internal apostrophes (and their typographic variants ’, ʼ, ‘, `)
 * are preserved as part of the word and normalized to ASCII single quote ('), while preserving punctuation and quotes.
 */
public class LugandaWordTokenizer extends WordTokenizer {

  private static final String APOSTROPHE_PLACEHOLDER = "\u0001\u0001LG@APOS\u0001\u0001";
  private static final Pattern APOSTROPHE_PATTERN = Pattern.compile("(?<=\\p{L})['’‘ʼ`´](?=\\p{L})");
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(APOSTROPHE_PLACEHOLDER, Pattern.LITERAL);

  @Override
  public List<String> tokenize(String text) {
    if (text == null || text.isEmpty()) {
      return super.tokenize(text);
    }
    
    // Protect word-internal apostrophes (between letters) before calling WordTokenizer
    String protectedText = APOSTROPHE_PATTERN.matcher(text).replaceAll(APOSTROPHE_PLACEHOLDER);
    
    List<String> rawTokens = super.tokenize(protectedText);
    List<String> tokens = new ArrayList<>(rawTokens.size());
    
    for (String token : rawTokens) {
      if (token.contains(APOSTROPHE_PLACEHOLDER)) {
        tokens.add(PLACEHOLDER_PATTERN.matcher(token).replaceAll("'"));
      } else {
        tokens.add(token);
      }
    }
    
    return tokens;
  }
}

