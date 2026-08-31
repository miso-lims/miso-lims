package uk.ac.bbsrc.tgac.miso.core.data;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractKitTest {

  @BeforeEach
  public void setUp() throws Exception {
  }

  @Test
  public final void testKit() {
    /*
     * A basic unit test to exercise the class. Mainly for cobertura coverage.
     */
    final KitImpl kit = new KitImpl() {
      private static final long serialVersionUID = 1L;
    };
    assertNotNull(kit);
  }

}
