package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Index;

public class IndexTest {

  @Test
  public void testCheckEditDistanceDuplicate() {
    assertEquals(0, Index.checkEditDistance("ABCDEFGH", "ABCDEFGH"));
  }

  @Test
  public void testCheckEditDistanceDuplicateDifferentLength() {
    assertEquals(0, Index.checkEditDistance("ABCDEFGH", "ABCDEFGHIJKL"));
  }

  @Test
  public void testCheckEditDistance1Diff() {
    assertEquals(1, Index.checkEditDistance("ABCDEFGH", "ABCDEFGG"));
  }

  @Test
  public void testCheckEditDistance1DiffDifferentLength() {
    assertEquals(1, Index.checkEditDistance("ABCDEFGH", "ABCDEFGGIJKL"));
  }

  @Test
  public void testCheckEditDistance2Diff() {
    assertEquals(2, Index.checkEditDistance("ABCDEFGH", "ABCDEFFF"));
  }

}
