package org.languagetool.rules;

import java.io.IOException;
import java.util.ResourceBundle;

import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.Language;
import org.languagetool.UserConfig;
import org.languagetool.rules.spelling.morfologik.MorfologikSpellerRule;

public class LugandaProperNounAssumptionRule extends MorfologikSpellerRule {

    private final MorfologikLugandaSpellerRule lugandaSpeller;

    public LugandaProperNounAssumptionRule(
            ResourceBundle messages,
            Language language,
            UserConfig userConfig,
            MorfologikLugandaSpellerRule lugandaSpeller)
            throws IOException {

        super(messages, language, userConfig);
        this.lugandaSpeller = lugandaSpeller;
    }

    @Override
    public String getFileName() {
        return "/lg/hunspell/lg_UG.dict";
    }

    @Override
    public String getId() {
        return "LUGANDA_PROPER_NOUN_ASSUMPTION_RULE";
    }

    @Override
    protected boolean ignoreToken(
            AnalyzedTokenReadings[] tokens,
            int idx) throws IOException {

        String word = tokens[idx].getToken();

        if (word.isEmpty()) {
            return false;
        }

        /*
         * Always check obvious gibberish, even when capitalized.
         */
        if (isObviousGibberish(word)) {
            return false;
        }

        /*
         * Let the Luganda spelling rule handle known words and
         * possessive compounds.
         */
        if (lugandaSpeller.isKnownWord(word)) {
            return true;
        }

        /*
         * Handle possessive compounds such as:
         *
         *     Eby'okulya
         *     Eky'okuna
         *     Ez'abaana
         *
         * without requiring them to be present in the finite
         * dictionary.
         */
        String[] split = PossessivePrefixes.splitPossessive(word);

        if (split != null) {
            String stem = split[1];

            if (lugandaSpeller.isKnownWord(stem)) {
                return true;
            }
        }

        /*
         * Non-capitalized words are checked normally.
         */
        if (!Character.isUpperCase(word.charAt(0))) {
            return false;
        }

        /*
         * Capitalized words in the middle of a sentence are
         * assumed to be proper nouns.
         */
        return !isAtSentenceStart(tokens, idx);
    }

    private static boolean isAtSentenceStart(
            AnalyzedTokenReadings[] tokens,
            int idx) {

        for (int i = idx - 1; i >= 0; i--) {

            if (tokens[i].isSentenceStart()) {
                return true;
            }

            String t = tokens[i].getToken();

            if (t.trim().isEmpty()) {
                continue;
            }

            return t.equals(".")
                    || t.equals("!")
                    || t.equals("?")
                    || t.equals("…");
        }

        return true;
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