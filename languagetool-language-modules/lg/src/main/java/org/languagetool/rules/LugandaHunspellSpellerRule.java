package org.languagetool.rules;

import java.util.ResourceBundle;

import org.languagetool.Language;
import org.languagetool.UserConfig;
import org.languagetool.language.Luganda;
import org.languagetool.rules.spelling.hunspell.HunspellRule;

public class LugandaHunspellSpellerRule extends HunspellRule {

    public static final String RULE_ID = "HUNSPELL_RULE_LU";
    private static final String RESOURCE_FILENAME = "/lg/hunspell/lg_UG.dic";
    
    public LugandaHunspellSpellerRule(ResourceBundle messages, UserConfig userConfig) {
        super(messages, new Luganda(), userConfig);
    }
    
    public LugandaHunspellSpellerRule(ResourceBundle messages) {
        this(messages, null);
    }
    
    @Override
    public String getId() {
        return RULE_ID;
    }
    
    @Override
    protected String getDictFilenameInResources(String langCountry) {
        return RESOURCE_FILENAME;
    }
    
    @Override
    protected boolean isLatinScript() {
        return true;
    }
}
