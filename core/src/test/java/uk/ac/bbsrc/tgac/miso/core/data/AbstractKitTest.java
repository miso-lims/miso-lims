package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class AbstractKitTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public final void testKit() {
    /*
     * A basic unit test to exercise the class. Mainly for cobertura coverage.
     */
    final KitImpl kit = new KitImpl() {
    };
    assertNotNull(kit);
  }

}
