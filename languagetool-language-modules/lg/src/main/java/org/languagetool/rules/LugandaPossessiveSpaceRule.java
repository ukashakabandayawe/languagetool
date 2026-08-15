package org.languagetool.rules;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.rules.ITSIssueType;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

// DictionaryLookup is in the same package (org.languagetool.rules) — no import needed

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Flags a possessive-pronoun contraction (e.g. "eby'", "ow'", "z'") that is
 * incorrectly separated from the word it attaches to by whitespace, e.g.
 *
 *   "eby' okulya"   ->  should be "eby'okulya"
 *
 * and offers the merged form as a correction. Only fires when the following
 * word starts with a vowel and is itself a known dictionary word — the same
 * conditions under which the prefix+word combination would be valid Luganda,
 * mirroring the exception made in LugandaSpellerRule for the glued-together form.
 *
 * This is intentionally a plain Rule (not a spelling rule) because the error
 * spans two tokens plus the whitespace between them, which the speller-oriented
 * rule hooks don't see.
 */
public class LugandaPossessiveSpaceRule extends Rule {

    private final DictionaryLookup dictionary;

    public LugandaPossessiveSpaceRule(DictionaryLookup dictionary) {
        this.dictionary = dictionary;
        setLocQualityIssueType(ITSIssueType.Grammar);
    }

    @Override
    public String getId() {
        return "LG_POSSESSIVE_SPACE";
    }

    @Override
    public String getDescription() {
        return "Possessive pronoun contraction should not be separated from the following word by a space";
    }

    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) {
        List<RuleMatch> matches = new ArrayList<>();
        AnalyzedTokenReadings[] tokens = sentence.getTokens();

        for (int i = 0; i < tokens.length; i++) {
            String tokenText = tokens[i].getToken();

            if (!PossessivePrefixes.isBarePrefix(tokenText)) {
                continue;
            }

            // Walk forward across any run of whitespace-only tokens to find the
            // next real token, and remember whether we actually crossed whitespace.
            int j = i + 1;
            boolean crossedWhitespace = false;
            while (j < tokens.length && tokens[j].isWhitespace()) {
                crossedWhitespace = true;
                j++;
            }
            if (!crossedWhitespace || j >= tokens.length) {
                continue; // nothing follows, or it's already glued on (tokenizer would
                          // have merged it, per the tokenizer behavior this rule assumes)
            }

            AnalyzedTokenReadings nextTok = tokens[j];
            String nextWord = nextTok.getToken();

            if (nextWord.isEmpty() || !PossessivePrefixes.startsWithVowel(nextWord)) {
                continue;
            }
            if (dictionary != null && !dictionary.isKnown(nextWord)) {
                continue;
            }

            int fromPos = tokens[i].getStartPos();
            int toPos = nextTok.getEndPos();
            String merged = tokenText + nextWord;

            RuleMatch match = new RuleMatch(this, sentence, fromPos, toPos,
                    "Did you mean to write this as one word?");
            match.setSuggestedReplacements(Collections.singletonList(merged));
            matches.add(match);
        }

        return matches.toArray(new RuleMatch[0]);
    }
}

