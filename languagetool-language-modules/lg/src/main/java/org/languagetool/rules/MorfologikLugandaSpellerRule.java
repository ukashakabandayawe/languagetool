package org.languagetool.rules;

import java.io.IOException;
import java.util.ResourceBundle;
import org.languagetool.rules.spelling.morfologik.MorfologikSpellerRule;

import org.languagetool.Language;
import org.languagetool.UserConfig;

public class MorfologikLugandaSpellerRule extends MorfologikSpellerRule {

    public MorfologikLugandaSpellerRule(ResourceBundle messages, Language language, UserConfig userConfig)
            throws IOException {
        super(messages, language, userConfig);
    }

    @Override
    public String getFileName() {
        return "/lg/hunspell/lg_UG.dict";
    }

    @Override
    public String getId() {
        return "MORFOLOGIK_LUGANDA_SPELLER_RULE";
    }

    @Override
    protected boolean ignoreWord(String word) throws IOException {
        if (super.ignoreWord(word)) {
            return true;
        }
        String[] split = PossessivePrefixes.splitPossessive(word);
        if (split == null) {
            return false;
        }
        String stem = split[1];
        return !speller1.isMisspelled(stem);
    }

    

    public boolean isKnownWord(String word) throws IOException {
        return !speller1.isMisspelled(word);
    }

}
