package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;

public class IndexTest {

  @Test
  public void testCheckEditDistanceDuplicate() {
    assertEquals(0, LibraryIndex.checkMismatches("ABCDEFGH", "ABCDEFGH"));
  }

  @Test
  public void testCheckEditDistanceDuplicateDifferentLength() {
    assertEquals(0, LibraryIndex.checkMismatches("ABCDEFGH", "ABCDEFGHIJKL"));
  }

  @Test
  public void testCheckEditDistance1Diff() {
    assertEquals(1, LibraryIndex.checkMismatches("ABCDEFGH", "ABCDEFGG"));
  }

  @Test
  public void testCheckEditDistance1DiffDifferentLength() {
    assertEquals(1, LibraryIndex.checkMismatches("ABCDEFGH", "ABCDEFGGIJKL"));
  }

  @Test
  public void testCheckEditDistance2Diff() {
    assertEquals(2, LibraryIndex.checkMismatches("ABCDEFGH", "ABCDEFFF"));
  }

}
