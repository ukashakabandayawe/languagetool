package org.languagetool.tokenizers;

import java.util.List;

public class LugandaWordTokenizer extends WordTokenizer {
     @Override
    public List<String> tokenize(String text) {
        // Use the default LanguageTool word tokenizer logic
        return super.tokenize(text);
    }
}
