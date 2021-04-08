package uk.ac.bbsrc.tgac.miso.persistence.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DbUtilsTest {

  @Test
  public void testReplaceWildcardsNoChange() {
    assertEquals("test", DbUtils.replaceWildcards("test"));
  }

  @Test
  public void testReplaceWildcardsReplace() {
    assertEquals("%test", DbUtils.replaceWildcards("*test"));
    assertEquals("te%st", DbUtils.replaceWildcards("te*st"));
    assertEquals("test%", DbUtils.replaceWildcards("test*"));
  }

  @Test
  public void testReplaceWildcardsUnescape() {
    assertEquals("*test", DbUtils.replaceWildcards("\\*test"));
    assertEquals("te*st", DbUtils.replaceWildcards("te\\*st"));
    assertEquals("test*", DbUtils.replaceWildcards("test\\*"));
  }

  @Test
  public void testRemoveQuotesNoChange() {
    assertEquals("test", DbUtils.removeQuotes("test"));
    assertEquals("\"test", DbUtils.removeQuotes("\"test"));
    assertEquals("test\"", DbUtils.removeQuotes("test\""));
    assertEquals("te\"st", DbUtils.removeQuotes("te\"st"));
  }

  @Test
  public void testRemoveQuotesUnescapeOnly() {
    assertEquals("\"test", DbUtils.removeQuotes("\\\"test"));
    assertEquals("test\"", DbUtils.removeQuotes("test\\\""));
    assertEquals("te\"st", DbUtils.removeQuotes("te\\\"st"));
  }

  @Test
  public void testRemoveQuotesRemoveOnly() {
    assertEquals("test", DbUtils.removeQuotes("\"test\""));
  }

  @Test
  public void testRemoveQuotesRemoveAndUnescape() {
    assertEquals("\"test", DbUtils.removeQuotes("\"\\\"test\""));
    assertEquals("te\"st", DbUtils.removeQuotes("\"te\\\"st\""));
    assertEquals("test\"", DbUtils.removeQuotes("\"test\\\"\""));
  }

  @Test
  public void testIsQuoted() {
    assertTrue(DbUtils.isQuoted("\"test\""));
    assertTrue(DbUtils.isQuoted("\"te\"st\""));
    assertTrue(DbUtils.isQuoted("\"te\\\"st\""));
  }

  @Test
  public void testIsNotQuoted() {
    assertFalse(DbUtils.isQuoted("test"));
    assertFalse(DbUtils.isQuoted("\"test"));
    assertFalse(DbUtils.isQuoted("test\""));
    assertFalse(DbUtils.isQuoted("te\"st"));
    assertFalse(DbUtils.isQuoted("\"test\\\""));
    assertFalse(DbUtils.isQuoted("\\\"test\""));
    assertFalse(DbUtils.isQuoted("\\\"test\\\""));
    assertFalse(DbUtils.isQuoted("\"te\"st"));
    assertFalse(DbUtils.isQuoted("te\"st\""));
  }

  @Test
  public void testContainsWildcards() {
    assertTrue(DbUtils.containsWildcards("te*st"));
    assertTrue(DbUtils.containsWildcards("*test"));
    assertTrue(DbUtils.containsWildcards("test*"));
  }

  @Test
  public void testContainsNoWildcards() {
    assertFalse(DbUtils.containsWildcards("test"));
    assertFalse(DbUtils.containsWildcards("te\\*st"));
    assertFalse(DbUtils.containsWildcards("\\*test"));
    assertFalse(DbUtils.containsWildcards("test\\*"));
  }

}
