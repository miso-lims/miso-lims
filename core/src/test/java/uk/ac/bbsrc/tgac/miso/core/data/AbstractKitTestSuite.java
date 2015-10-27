package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class AbstractKitTestSuite {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public final void testKit() {
    /*
     * A basic unit test to exercise the class. Mainly for cobertura coverage.
     */
    final AbstractKit kit = new AbstractKit() {
    };
    assertNotNull(kit);
  }

}
