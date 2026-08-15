package org.languagetool.rules.lg;

import org.junit.Test;
import org.languagetool.rules.patterns.PatternRuleTest;

import java.io.IOException;

public class LugandaPatternRuleTest extends PatternRuleTest {

  @Test
  public void testRules() throws IOException {
    runGrammarRulesFromXmlTest();
  }

}
