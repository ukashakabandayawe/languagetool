package org.languagetool.tagging.disambiguation.rules.lg;

import java.io.IOException;

import org.languagetool.AnalyzedSentence;
import org.languagetool.language.Luganda;
import org.languagetool.tagging.disambiguation.Disambiguator;
import org.languagetool.tagging.disambiguation.rules.XmlRuleDisambiguator;

public class LugandaRuleDisambiguator implements Disambiguator {
    private final Disambiguator disambiguator = new XmlRuleDisambiguator(new Luganda());
    
      @Override
      public final AnalyzedSentence disambiguate(AnalyzedSentence input)
          throws IOException {
        return disambiguator.disambiguate(input);
      }

      @Override
      public AnalyzedSentence preDisambiguate(AnalyzedSentence input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'preDisambiguate'");
      }
}
