package org.languagetool.tokenizers;

import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Luganda;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LugandaWordTokenizerTest {

  @Test
  public void testTokenizeApostropheWords() {
    LugandaWordTokenizer tokenizer = new LugandaWordTokenizer();

    // Test words with ng' digraph and elisions
    List<String> tokens = tokenizer.tokenize("Ebbiring'anya eky'okunywa eng'aali eng'aano eng'amiya eng'uumi ow'oluganda n'amata");
    
    // Check that each word is kept as a single whole token
    assertTrue("Should contain Ebbiring'anya", tokens.contains("Ebbiring'anya"));
    assertTrue("Should contain eky'okunywa", tokens.contains("eky'okunywa"));
    assertTrue("Should contain eng'aali", tokens.contains("eng'aali"));
    assertTrue("Should contain eng'aano", tokens.contains("eng'aano"));
    assertTrue("Should contain eng'amiya", tokens.contains("eng'amiya"));
    assertTrue("Should contain eng'uumi", tokens.contains("eng'uumi"));
    assertTrue("Should contain ow'oluganda", tokens.contains("ow'oluganda"));
    assertTrue("Should contain n'amata", tokens.contains("n'amata"));
  }

  @Test
  public void testTypographicApostropheNormalization() {
    LugandaWordTokenizer tokenizer = new LugandaWordTokenizer();

    // Test with curly apostrophe (U+2019) and modifier letter apostrophe (U+02BC)
    List<String> tokens1 = tokenizer.tokenize("Ebbiring’anya eky’okunywa eng’aano");
    assertTrue("Should normalize curly quote in Ebbiring'anya", tokens1.contains("Ebbiring'anya"));
    assertTrue("Should normalize curly quote in eky'okunywa", tokens1.contains("eky'okunywa"));
    assertTrue("Should normalize curly quote in eng'aano", tokens1.contains("eng'aano"));

    List<String> tokens2 = tokenizer.tokenize("engʼaali engʼuumi");
    assertTrue("Should normalize modifier letter in eng'aali", tokens2.contains("eng'aali"));
    assertTrue("Should normalize modifier letter in eng'uumi", tokens2.contains("eng'uumi"));
  }

  @Test
  public void testQuotesAroundWordsAreSeparated() {
    LugandaWordTokenizer tokenizer = new LugandaWordTokenizer();

    // Quotes around words should still be separated
    List<String> tokens = tokenizer.tokenize("'Ebbiring'anya'");
    assertEquals("'", tokens.get(0));
    assertEquals("Ebbiring'anya", tokens.get(1));
    assertEquals("'", tokens.get(2));
  }

  @Test
  public void testSpellCheckingWithApostrophes() throws IOException {
    JLanguageTool lt = new JLanguageTool(new Luganda());

    // Valid Luganda sentence containing words with apostrophes present in lg_UG.dict
    String validSentence = "Ebbiring'anya ne eky'okunywa.";
    List<RuleMatch> matches = lt.check(validSentence);
    
    // Valid words should not be flagged as spelling errors
    for (RuleMatch match : matches) {
      if (match.getRule().getId().contains("SPELL")) {
        throw new AssertionError("Unexpected spelling error: " + match.getMessage() + " for " + validSentence.substring(match.getFromPos(), match.getToPos()));
      }
    }

    // Individual words check for ng' words and contractions requested by the user
    String words = "Ebbiring'anya eky'okunywa eng'aali eng'aano eng'amiya eng'uumi";
    List<RuleMatch> wordsMatches = lt.check(words);
    for (RuleMatch match : wordsMatches) {
      if (match.getRule().getId().contains("SPELL")) {
        throw new AssertionError("Unexpected spelling error for valid word: " + words.substring(match.getFromPos(), match.getToPos()));
      }
    }
  }
}
