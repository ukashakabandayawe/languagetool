package org.languagetool.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Utility for recognising Luganda possessive-pronoun contractions such as
 * "eby'", "ow'", "z'" etc., whether they appear glued to a following vowel-initial
 * stem ("eby'okulya") or as a bare token on their own ("eby'" with a space after it).
 *
 * These are not stored in the .dict because expanding the full cross-product of
 * (prefix x every vowel-initial word) would blow up the compiled dictionary size.
 * Instead, both rules that use this class check the *stem* against the real
 * speller/dictionary at runtime.
 */
public final class PossessivePrefixes {

    // Longest-to-shortest match order matters: "eby'" must be tried before "by'",
    // "by'" before "y'", etc., or a longer prefix would be mis-split.
    private static final List<String> RAW_PREFIXES = List.of(
            "ow'", "ab'", "ogw'", "egy'", "ey'", "ez'", "eky'", "eby'", "ely'",
            "ag'", "ak'", "obw'", "olw'", "okw'", "otw'", "w'", "b'", "gw'",
            "gy'", "y'", "z'", "ky'", "by'", "ly'", "g'", "k'", "bw'", "lw'",
            "kw'", "tw'", "n'", "ng'"
    );

    /** De-duplicated, length-descending view of RAW_PREFIXES. */
    public static final List<String> PREFIXES;

    static {
        LinkedHashSet<String> dedup = new LinkedHashSet<>(RAW_PREFIXES);
        List<String> sorted = new ArrayList<>(dedup);
        sorted.sort((a, b) -> b.length() - a.length()); // longest first
        PREFIXES = Collections.unmodifiableList(sorted);
    }

    private static final Set<Character> VOWELS =
            Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');

    private PossessivePrefixes() {
    }

    /** Curly apostrophe (U+2019) -> straight apostrophe, so matching is consistent
     *  regardless of which one the user's keyboard/autocorrect produced. */
    public static String normalizeApostrophe(String s) {
        return s.replace('\u2019', '\'');
    }

    public static boolean startsWithVowel(String word) {
        return !word.isEmpty() && VOWELS.contains(word.charAt(0));
    }

    /** True if {@code token} IS one of the possessive prefixes and nothing else,
     *  e.g. "eby'" typed on its own (this is the shape you get when a space follows it,
     *  since your tokenizer only glues the apostrophe to a following word if there's
     *  no space in between). */
    public static boolean isBarePrefix(String token) {
        String norm = normalizeApostrophe(token).toLowerCase(Locale.ROOT);
        for (String p : PREFIXES) {
            if (norm.equals(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If {@code token} looks like prefix+stem (e.g. "eby'okulya"), split it using the
     * longest matching prefix and return {rawPrefixAsTyped, stem}.
     * Returns null if no prefix matches, or the remainder is empty, or the remainder
     * doesn't start with a vowel (these contractions are only ever valid before a vowel,
     * so anything else was never a candidate in the first place).
     */
    public static String[] splitPossessive(String token) {
        String norm = normalizeApostrophe(token);
        String lower = norm.toLowerCase(Locale.ROOT);
        for (String p : PREFIXES) {
            if (lower.startsWith(p) && norm.length() > p.length()) {
                String stem = norm.substring(p.length());
                if (startsWithVowel(stem)) {
                    return new String[]{norm.substring(0, p.length()), stem};
                }
            }
        }
        return null;
    }
}
