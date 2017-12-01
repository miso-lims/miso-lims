package uk.ac.bbsrc.tgac.miso.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;

public class LimsUtilsTest {

  @Test
  public void testInstanceOfSampleTissueProcessing() throws Exception {
    SampleSlide slide = new SampleSlideImpl();
    assertTrue("Slide is a type of Tissue Processing", LimsUtils.isTissueProcessingSample(slide));
  }

  @Test
  public void testNullifyStringIfBlankIsBlank() throws Exception {
    String nullString = null;
    assertEquals(nullString, LimsUtils.nullifyStringIfBlank("     "));
  }

  @Test
  public void testNullifyStringIfBlankNotBlank() throws Exception {
    String notNullString = "not null";
    assertEquals(notNullString, LimsUtils.nullifyStringIfBlank("not null"));
  }
}
