package org.languagetool.rules;

import org.languagetool.Language;
import org.languagetool.UserConfig;
import org.languagetool.rules.spelling.morfologik.MorfologikMultiSpeller;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Luganda spelling rule that extends the base MorfologikLugandaSpellerRule to
 * accept possessive prefixes followed by a valid
 * vowel-initial dictionary word, e.g. {@code eby'okulya}, {@code ow'oluganda},
 * {@code n'amata}.
 * So far, it does not support suggestions.
 */
public class MorfologikLugandaPossessiveSpellerRule extends MorfologikLugandaSpellerRule {

  private static final String[] POSSESSIVE_PREFIXES = {
      "ow'", "ab'", "ogw'", "egy'", "ey'", "ez'", "eky'", "eby'", "ely'", "ew'",
      "ag'", "ak'", "obw'", "olw'", "okw'", "otw'", "w'", "b'", "gw'", "gy'",
      "y'", "z'", "ky'", "by'", "ly'", "g'", "k'", "bw'", "lw'", "kw'", "tw'", "n'"
  };

  public MorfologikLugandaPossessiveSpellerRule(ResourceBundle messages, Language language, UserConfig userConfig)
      throws IOException {
    super(messages, language, userConfig);
  }

  @Override
  protected boolean isMisspelled(MorfologikMultiSpeller speller, String word) {
    if (word != null) {
      String normalizedWord = normalizeApostrophes(word);
      String lowerWord = normalizedWord.toLowerCase(Locale.ROOT);

      for (String prefix : POSSESSIVE_PREFIXES) {
        if (lowerWord.startsWith(prefix) && normalizedWord.length() > prefix.length()) {
          String suffix = normalizedWord.substring(prefix.length());
          if (startsWithVowel(suffix) && !super.isMisspelled(speller, suffix)) {
            return false;
          }
        }
      }

      return super.isMisspelled(speller, normalizedWord);
    }

    return false;
  }

  private static boolean startsWithVowel(String word) {
    if (word == null || word.isEmpty()) {
      return false;
    }
    switch (Character.toLowerCase(word.charAt(0))) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
      case 'u':
        return true;
      default:
        return false;
    }
  }

  private static String normalizeApostrophes(String word) {
    return word.replace('\u2019', '\'')
        .replace('\u02BC', '\'')
        .replace('\u2018', '\'')
        .replace('`', '\'')
        .replace('\u00B4', '\'');
  }
}
