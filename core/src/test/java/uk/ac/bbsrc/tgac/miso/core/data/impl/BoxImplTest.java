package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;

public class BoxImplTest {

  @Test
  public void testSetBoxable() {
    Box box = makeBox();
    BoxableView v = makeBoxable();

    box.setBoxable("A01", v);
    assertNotNull(box.getBoxable("A01"));
    assertNull(box.getBoxable("A02"));

    box.setBoxable("A02", v);
    assertNull(box.getBoxable("A01"));
    assertNotNull(box.getBoxable("A02"));
  }

  private static Box makeBox() {
    Box box = new BoxImpl();
    BoxSize s = new BoxSize();
    s.setRows(1);
    s.setColumns(2);
    box.setSize(s);
    return box;
  }

  private static BoxableView makeBoxable() {
    BoxableView b = new BoxableView();
    b.setId(new BoxableId(EntityType.SAMPLE, 1L));
    return b;
  }

}
