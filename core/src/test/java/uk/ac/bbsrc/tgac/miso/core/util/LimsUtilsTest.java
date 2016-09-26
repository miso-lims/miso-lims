package uk.ac.bbsrc.tgac.miso.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleCVSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

public class LimsUtilsTest {

  @Test
  public void testValidateRelationshipForSimpleSample() throws Exception {
    Sample child = new SampleImpl(); // Simple sample has no DetailedSample attributes.
    Sample parent = null; // Simple sample has no parent.
    assertTrue(
        "Simple sample with a null parent and null DetailedSample is a valid relationship",
        LimsUtils.isValidRelationship(null, parent, child));
  }

  @Test
  public void testInstanceOfSampleTissueProcessing() throws Exception {
    SampleCVSlide cvSlide = new SampleCVSlideImpl();
    assertTrue("CV Slide is a type of Tissue Processing", LimsUtils.isTissueProcessingSample(cvSlide));
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
