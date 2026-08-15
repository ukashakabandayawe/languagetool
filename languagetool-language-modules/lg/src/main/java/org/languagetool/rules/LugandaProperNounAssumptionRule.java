package org.languagetool.rules;

import java.io.IOException;
import java.util.ResourceBundle;

import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.Language;
import org.languagetool.UserConfig;
import org.languagetool.rules.spelling.morfologik.MorfologikSpellerRule;

public class LugandaProperNounAssumptionRule extends MorfologikSpellerRule {

    public LugandaProperNounAssumptionRule(ResourceBundle messages, Language language, UserConfig userConfig)
            throws IOException {
        super(messages, language, userConfig);
    }

    @Override
    public String getFileName() {
        return "/lg/hunspell/lg_UG.dict";
    }
    @Override
    public String getId() {
        return "LUGANDA_PROPER_NOUN_ASSUMPTION_RULE";
    }

    /**
     * Proper-noun heuristic for capitalised words.
     *
     * <ul>
     * <li><b>Mid-sentence</b> capital → silently ignored (assumed proper
     * noun).</li>
     * <li><b>Sentence-start</b> capital → always spell-checked normally.
     * Unrecognised words (including proper nouns not in the dictionary)
     * will be flagged.</li>
     * </ul>
     */
    @Override
    protected boolean ignoreToken(AnalyzedTokenReadings[] tokens, int idx) throws IOException {
        if (super.ignoreToken(tokens, idx)) {
            return true;
        }

        String word = tokens[idx].getToken();

        if (word.isEmpty()) {
            return false;
        }

        // Always check obvious gibberish, even if it starts with a capital letter.
        if (isObviousGibberish(word)) {
            return false;
        }

        // Non-capitalized words are handled normally.
        if (!Character.isUpperCase(word.charAt(0))) {
            return false;
        }

        // Capitalized words in the middle of a sentence are assumed
        // to be proper nouns.
        return !isAtSentenceStart(tokens, idx);
    }

    /**
     * Returns {@code true} when the token at {@code idx} is the first real word
     * of a sentence — i.e., the preceding non-whitespace token is the
     * SENTENCE_START marker or a sentence-ending punctuation character.
     */
    private static boolean isAtSentenceStart(AnalyzedTokenReadings[] tokens, int idx) {
        for (int i = idx - 1; i >= 0; i--) {
            if (tokens[i].isSentenceStart()) {
                return true;
            }
            String t = tokens[i].getToken();
            if (t.trim().isEmpty()) {
                continue;
            }
            // Previous real token is sentence-ending punctuation
            return t.equals(".") || t.equals("!") || t.equals("?") || t.equals("…");
        }
        return true; // No previous token found — treat as sentence start
    }

    private static boolean isObviousGibberish(String word) {
        if (word.length() < 6) {
            return false;
        }

        int consecutiveVowels = 0;
        int consecutiveConsonants = 0;

        for (int i = 0; i < word.length(); i++) {
            char c = Character.toLowerCase(word.charAt(i));

            if (!Character.isLetter(c)) {
                consecutiveVowels = 0;
                consecutiveConsonants = 0;
                continue;
            }

            if (isVowel(c)) {
                consecutiveVowels++;
                consecutiveConsonants = 0;

                if (consecutiveVowels >= 5) {
                    return true;
                }
            } else {
                consecutiveConsonants++;
                consecutiveVowels = 0;

                if (consecutiveConsonants >= 5) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isVowel(char c) {
        return c == 'a'
                || c == 'e'
                || c == 'i'
                || c == 'o'
                || c == 'u';
    }
}