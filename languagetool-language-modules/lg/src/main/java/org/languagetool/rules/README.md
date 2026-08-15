# Luganda possessive-contraction rules for LanguageTool

Two rules working together:

1. **`LugandaSpellerRule`** — a drop-in replacement for your normal Morfologik
   speller rule. It overrides `ignoreWord(String)` so that a token like
   `eby'okulya` is *not* flagged, as long as:
   - it starts with a recognised possessive prefix (`eby'`, `ow'`, `z'`, ...), and
   - the remaining stem (`okulya`) starts with a vowel, and
   - the stem, checked on its own, is a real word already in your `.dict`.

2. **`LugandaPossessiveSpaceRule`** — a normal grammar rule that catches the
   opposite mistake: `eby' okulya` (space after the apostrophe). It fires under
   the same three conditions and suggests merging into `eby'okulya`.

Both share `PossessivePrefixes`, which holds your prefix list (de-duplicated,
sorted longest-first so e.g. `eby'` is tried before `by'`) and does the
apostrophe-normalization (`'` vs `’`) and vowel check.

## Wiring it up

```java
// Registering the speller replacement — same pattern as swapping in any
// language-specific Morfologik*SpellerRule.
LugandaSpellerRule spellerRule =
        new LugandaSpellerRule(messages, lugandaLanguage, userConfig);

// The space rule needs something that can answer "is this word in the dict".
// Easiest: back it with the same speller instance, e.g. via a small adapter:
DictionaryLookup lookup = word -> !spellerRule.speller1IsMisspelled(word);
// (add a tiny public wrapper method on LugandaSpellerRule if speller1 isn't
// visible from where you're wiring rules up, since it's `protected`)

LugandaPossessiveSpaceRule spaceRule = new LugandaPossessiveSpaceRule(lookup);

lugandaLanguage.getRelevantRules(...).add(spellerRule);
lugandaLanguage.getRelevantRules(...).add(spaceRule);
```

## Version-compatibility note

`speller1` is a `protected` field on `MorfologikSpellerRule` in current
LanguageTool releases (a `MorfologikMultiSpeller` with `isMisspelled(String)`).
That's confirmed by the current 6.4-snapshot API docs and by real subclasses
like `MorfologikItalianSpellerRule`. If your LT version differs slightly, the
only line that should need touching is `isStemKnown()` in
`LugandaSpellerRule` — everything else (prefix matching, the space rule) is
version-independent.

If you'd rather not touch a protected field at all, `MorfologikSpellerRule`
also exposes `ignorePotentiallyMisspelledWord(String)` in some versions, which
is called *only after* the standard check already flagged the word — cheaper,
since you skip the prefix-matching work on every correctly-spelled token. Swap
the `@Override` target if that method exists in your version.

## Test cases to check against

| Input | Expected |
|---|---|
| `eby'okulya` | not flagged (assuming `okulya` is in the dict) |
| `ow'omusomesa` | not flagged (assuming `omusomesa` is in the dict) |
| `eby' okulya` | flagged by `LG_POSSESSIVE_SPACE`, suggestion `eby'okulya` |
| `eby'  okulya` (two spaces) | same as above — the rule walks over *any* run of whitespace tokens |
| `eby'xyzzy` (stem not in dict) | still flagged as a spelling error — correctly, since it's not a real word either way |
| `n'ebintu` where `n'` + vowel-stem | same treatment as any other prefix in the list |
| `by'` on its own at end of sentence | ignored by the space rule (nothing follows to merge with) |
