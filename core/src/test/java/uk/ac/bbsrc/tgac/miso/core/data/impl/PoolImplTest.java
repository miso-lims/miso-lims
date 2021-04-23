package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;

public class PoolImplTest {

  @Test
  public void testLongestReadSingle() {
    PoolImpl pool = new PoolImpl();
    addElement(pool, "AAAAAA");
    addElement(pool, "CCCCCC");
    addElement(pool, "GGGGGGGG");
    addElement(pool, "TTTTTT");
    assertEquals("8", pool.getLongestIndex());
  }
  
  @Test
  public void testLongestReadDual() {
    PoolImpl pool = new PoolImpl();
    addElement(pool, "AAAAAA", "AAAAAA");
    addElement(pool, "CCCCCC", "CCCCCCCC");
    addElement(pool, "GGGGGGGG", "GGGGGG");
    addElement(pool, "TTTTTT", "TTTTTT");
    assertEquals("8,8", pool.getLongestIndex());
  }
  
  @Test
  public void testLongestReadNone() {
    PoolImpl pool = new PoolImpl();
    assertEquals("0", pool.getLongestIndex());
  }

  private void addElement(PoolImpl pool, String index1) {
    addElement(pool, makeIndex(index1, 1));
  }
  
  private void addElement(PoolImpl pool, String index1, String index2) {
    addElement(pool, makeIndex(index1, 1), makeIndex(index2, 2));
  }

  private void addElement(PoolImpl pool, Index... indices) {
    ListLibraryAliquotView ldi = new ListLibraryAliquotView();
    ldi.setParentLibrary(new ParentLibrary());
    ldi.getParentLibrary().setIndices(Lists.newArrayList(indices));
    PoolElement element = new PoolElement(pool, ldi);
    pool.getPoolContents().add(element);
  }

  private Index makeIndex(String sequence, int position) {
    Index index = new Index();
    index.setSequence(sequence);
    index.setPosition(position);
    return index;
  }

}
