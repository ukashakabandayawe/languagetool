package org.languagetool.rules.lg;

import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.Luganda;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LugandaPossessiveSpellerRuleTest {

  @Test
  public void testPossessivePrefixBeforeVowelInitialWordIsAccepted() throws IOException {
    Language language = new Luganda();
    SpellingCheckRule spellingRule = language.getDefaultSpellingRule();

    assertFalse(spellingRule.isMisspelled("ow'oluganda"));
    assertFalse(spellingRule.isMisspelled("Eby'okulya"));
    assertFalse(spellingRule.isMisspelled("n'amata"));
    assertFalse(spellingRule.isMisspelled("ez'abaana"));
  }

  @Test
  public void testPossessivePrefixBeforeVowelInitialWordIsAcceptedInSentenceCheck() throws IOException {
    JLanguageTool lt = new JLanguageTool(new Luganda());
    String sentence = "Ow'oluganda yaleese eby'okulya eri ez'abaana.";
    List<RuleMatch> matches = lt.check(sentence);

    for (RuleMatch match : matches) {
      if (match.getRule().getId().contains("SPELL")) {
        throw new AssertionError("Unexpected spelling error for: "
            + sentence.substring(match.getFromPos(), match.getToPos()));
      }
    }
  }

  @Test
  public void testInvalidSuffixIsStillRejected() throws IOException {
    Language language = new Luganda();
    SpellingCheckRule spellingRule = language.getDefaultSpellingRule();

    assertTrue(spellingRule.isMisspelled("ow'zzzz"));
    assertTrue(spellingRule.isMisspelled("eby'azzzzz"));
  }
}
