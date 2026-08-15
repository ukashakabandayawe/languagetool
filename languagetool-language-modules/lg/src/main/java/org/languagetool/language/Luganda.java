package org.languagetool.language;

import org.languagetool.Language;
import org.languagetool.LanguageMaintainedState;
import org.languagetool.UserConfig;
import org.languagetool.rules.*;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.languagetool.rules.spelling.multitoken.MultitokenSpeller;
import org.languagetool.tagging.Tagger; // Tagger is required for getTagger()
import org.languagetool.tagging.lg.LugandaTagger;
import org.languagetool.tokenizers.SRXSentenceTokenizer;
import org.languagetool.tokenizers.SentenceTokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Support for Luganda.
 * 
 * @since 6.8
 */
public class Luganda extends Language {

  @Override
  public String getName() {
    return "Luganda";
  }

  @Override
  public String getShortCode() {
    return "lg"; // ISO 639 code
  }

  @Override
  public SentenceTokenizer createDefaultSentenceTokenizer() {
    return new SRXSentenceTokenizer(this);
  }

  @Override
  public org.languagetool.tokenizers.Tokenizer createDefaultWordTokenizer() {
    return new org.languagetool.tokenizers.LugandaWordTokenizer();
  }

  @Override
  public Tagger createDefaultTagger() {
    return new LugandaTagger();
  }

  // Removed broken override for getRelevantRules()

  @Override
  public String[] getCountries() {
    // Luganda is primarily spoken in Uganda
    return new String[] { "UG" };
  }

  @Override
  public Contributor[] getMaintainers() {
    return new Contributor[] {
        new Contributor("Kabanda Ukasha Yawe")
    };
  }

  @Override
  public LanguageMaintainedState getMaintainedState() {
    return LanguageMaintainedState.ActivelyMaintained;
  }

  @Override
  public List<Rule> getRelevantRules(ResourceBundle messages, UserConfig userConfig, Language language,
      List<Language> altLanguages)
      throws IOException {
    MorfologikLugandaSpellerRule spellerRule = new MorfologikLugandaSpellerRule(messages, this, userConfig);

    DictionaryLookup possessiveDictionaryLookup = word -> {
      try {
        return spellerRule.isKnownWord(word);
      } catch (IOException e) {
        return false; // fail closed: if the speller can't be checked, don't force a merge
      }
    };
    return Arrays.asList(
        new EmptyLineRule(messages, this),
        new MultipleWhitespaceRule(messages, this),
        new SentenceWhitespaceRule(messages),
        new CommaWhitespaceRule(messages, true),
        new LongSentenceRule(messages, userConfig, 50),
        new ParagraphRepeatBeginningRule(messages, this),
        new PunctuationMarkAtParagraphEnd(messages, this),
        new PunctuationMarkAtParagraphEnd2(messages, this),
        new LongParagraphRule(messages, this, userConfig),
        new LugandaProperNounAssumptionRule(messages, this, userConfig),
        new LugandaPossessiveSpaceRule(possessiveDictionaryLookup),
        new DemoRule(),
        new UppercaseSentenceStartRule(messages, this,
            Example.wrong("Eno ennyuumba nkadde. <marker>baagiziimba</marker> mu 1950."),
            Example.fixed("Eno ennyuumba nkadde. <marker>Baagiziimba</marker> mu 1950.")),
        new CommaWhitespaceRule(messages,
            Example.wrong("Twaanywedde kaawa<marker> ,</marker> ammazzi ne caayi n'amata."),
            Example.fixed("Twaanywedde kaawa<marker>,</marker> ammazzi ne caayi n'amata.")),
        new GenericUnpairedBracketsRule(messages,
            Arrays.asList("[", "(", "{", "«", "﴾", "\""),
            Arrays.asList("]", ")", "}", "»", "﴿", "\"")));
  }

  @Override
  public SpellingCheckRule createDefaultSpellingRule(ResourceBundle messages) throws IOException {
    return new MorfologikLugandaSpellerRule(messages, this, null);
  }

  // TODO: Implement other required methods as needed
  @Override
  public MultitokenSpeller getMultitokenSpeller() {
    // Provide a no-op multitoken speller to avoid NPEs in generic filters
    return new MultitokenSpeller(this, Collections.emptyList());
  }
}
