package org.languagetool.rules;

/**
 * Thin abstraction over "is this word in my dictionary". Lets
 * LugandaPossessiveSpaceRule stay decoupled from Morfologik so you can back it
 * with whatever speller instance you already have loaded (or a plain HashSet,
 * a Hunspell wrapper, etc.) without changing the rule itself.
 */
public interface DictionaryLookup {
    boolean isKnown(String word);
}
