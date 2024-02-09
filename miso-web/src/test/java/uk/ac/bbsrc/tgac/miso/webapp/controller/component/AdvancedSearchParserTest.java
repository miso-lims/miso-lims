package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AdvancedSearchParserTest {

  private static final String SIMPLE_TERMLESS_CRITERION = "phrase";

  private AdvancedSearchParser sut;

  @Before
  public void setup() {
    sut = new AdvancedSearchParser();
  }

  @Test
  public void testSplitCriteriaSingle() {
    testSplitCriteria(SIMPLE_TERMLESS_CRITERION);
    testSplitCriteria("phrase with spaces");
    testSplitCriteria("term:phrase");
    testSplitCriteria("term:phrase with spaces");
  }

  @Test
  public void testSplitCriteriaMultiple() {
    testSplitCriteria(SIMPLE_TERMLESS_CRITERION, "term:phrase");
    testSplitCriteria("term1:phrase1", "term2:phrase2");
    testSplitCriteria("term1:phrase with spaces", "term2:more spaces");
    testSplitCriteria(SIMPLE_TERMLESS_CRITERION, "term:phrase with spaces");
  }

  @Test
  public void testSplitCriteriaColon() {
    // shouldn't affect criteria splitting
    testGetCriteriaDoubled("term:phrase with\\:colon");
  }

  @Test
  public void testSplitCriteriaQuotes() {
    // shouldn't affect criteria splitting
    testGetCriteriaDoubled("term:\"exact phrase\"");
    testGetCriteriaDoubled("term:\"exact phrase with unescaped \"quotes\"\"");
    testGetCriteriaDoubled("term:\"exact phrase with escaped \\\"quotes\\\"\"");
    testGetCriteriaDoubled("term:phrase with unescaped \"quotes\"");
    testGetCriteriaDoubled("term:phrase with escaped \\\"quotes\\\"");
    testGetCriteriaDoubled("term:\\\"phrase beginning with escaped quote");
  }

  @Test
  public void testSplitCriteriaWildcards() {
    // shouldn't affect criteria splitting
    testGetCriteriaDoubled("term:*test");
    testGetCriteriaDoubled("term:te*st");
    testGetCriteriaDoubled("term:test*");
    testGetCriteriaDoubled("term:\\*test");
    testGetCriteriaDoubled("term:te\\*st");
    testGetCriteriaDoubled("term:test\\*");
  }

  private void testGetCriteriaDoubled(String criterion) {
    // test as first and second criterion since first can be handled differently
    testSplitCriteria(criterion, criterion);
  }

  private void testSplitCriteria(String... criteria) {
    String search = String.join(" ", criteria);
    List<String> results = sut.splitCriteria(search);
    assertEquals(criteria.length, results.size());
    for (int i = 0; i < criteria.length; i++) {
      String expected = criteria[i];
      assertEquals(expected, results.get(i));
    }
  }

}
