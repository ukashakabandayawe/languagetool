package org.languagetool.rules;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.Language;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.UserConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.ResourceBundle;

public class LugandaPossessiveRules extends Rule {

    private static final String RULE_ID = "LUGANDA_POSSESSIVE_RULE";

    private static final Set<String> POSSESSIVE_PREFIXES =
            new HashSet<>(Arrays.asList(
                    "ow'",
                    "ab'",
                    "ogw'",
                    "egy'",
                    "ey'",
                    "ez'",
                    "eky'",
                    "eby'",
                    "ely'",
                    "ag'",
                    "ak'",
                    "obw'",
                    "olw'",
                    "okw'",
                    "otw'",
                    "w'",
                    "b'",
                    "gw'",
                    "gy'",
                    "y'",
                    "z'",
                    "ky'",
                    "by'",
                    "ly'",
                    "g'",
                    "k'",
                    "bw'",
                    "lw'",
                    "kw'",
                    "tw'",
                    "n'"
            ));

    private static final Set<Character> VOWELS =
            new HashSet<>(Arrays.asList(
                    'a', 'e', 'i', 'o', 'u',
                    'A', 'E', 'I', 'O', 'U'
            ));

    public LugandaPossessiveRules(
            ResourceBundle messages,
            Language language,
            UserConfig userConfig) {

        super(messages);
    }

    @Override
    public String getId() {
        return RULE_ID;
    }

    @Override
    public String getDescription() {
        return "Checks spacing of Luganda possessive prefixes.";
    }

    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) {

        AnalyzedTokenReadings[] tokens = sentence.getTokens();

        for (int i = 1; i < tokens.length - 1; i++) {

            String token = tokens[i].getToken();

            if (!isPossessivePrefix(token)) {
                continue;
            }

            String nextToken = tokens[i + 1].getToken();

            /*
             * The next token must begin with a vowel.
             */
            if (!startsWithVowel(nextToken)) {
                continue;
            }

            /*
             * We deliberately don't check the dictionary here.
             *
             * The spelling rule will check the following word normally.
             * This rule's sole purpose is to detect the illegal whitespace
             * between the possessive prefix and the following vowel-starting
             * word.
             */

            int startPos = tokens[i].getStartPos();
            int endPos = tokens[i + 1].getEndPos();

            RuleMatch match = new RuleMatch(
                    this,
                    sentence,
                    startPos,
                    endPos,
                    "The Luganda possessive prefix should be combined with the following word."
            );

            match.setSuggestedReplacement(token + nextToken);

            return new RuleMatch[]{match};
        }

        return new RuleMatch[0];
    }

    private boolean isPossessivePrefix(String word) {

        if (word == null || word.isEmpty()) {
            return false;
        }

        return POSSESSIVE_PREFIXES.contains(
                word.toLowerCase()
        );
    }

    private boolean startsWithVowel(String word) {

        if (word == null || word.isEmpty()) {
            return false;
        }

        return VOWELS.contains(word.charAt(0));
    }
}