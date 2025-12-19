package org.languagetool.rules;

import java.io.IOException;
import java.util.ResourceBundle;
import org.languagetool.rules.spelling.morfologik.MorfologikSpellerRule;

import org.languagetool.Language;
import org.languagetool.UserConfig;

public class MorfologikLugandaSpellerRule extends MorfologikSpellerRule {

    public MorfologikLugandaSpellerRule(ResourceBundle messages, Language language, UserConfig userConfig) throws IOException {
        super(messages, language, userConfig);
    }

    @Override
    public String getFileName() {
        return "/lg/hunspell/Luganda.dict";
    }

    @Override
    public String getId() {
        return "MORFOLOGIK_LUGANDA_SPELLER_RULE";
    }
    
}
