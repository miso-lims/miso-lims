package uk.ac.bbsrc.tgac.miso.core.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
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

  @Test
  public void testGetDecimalPlacesToDisplay() throws Exception {
    assertEquals(0, LimsUtils.getDecimalPlacesToDisplay(null));
    assertEquals(0, LimsUtils.getDecimalPlacesToDisplay(BigDecimal.TEN));
    assertEquals(0, LimsUtils.getDecimalPlacesToDisplay(new BigDecimal("23.45")));
    assertEquals(0, LimsUtils.getDecimalPlacesToDisplay(new BigDecimal("-12.34")));
    assertEquals(1, LimsUtils.getDecimalPlacesToDisplay(BigDecimal.ZERO));
    assertEquals(1, LimsUtils.getDecimalPlacesToDisplay(BigDecimal.ONE));
    assertEquals(1, LimsUtils.getDecimalPlacesToDisplay(new BigDecimal("1.23")));
    assertEquals(1, LimsUtils.getDecimalPlacesToDisplay(new BigDecimal("-2.34")));
  }

  @Test
  public void testMakeFormatString() throws Exception {
    testMakeFormatString("1.2", new BigDecimal("1.23"), VolumeUnit.MICROLITRES.getUnits());
    testMakeFormatString("23", new BigDecimal("23.45"), VolumeUnit.MICROLITRES.getUnits());
  }

  private void testMakeFormatString(String expected, BigDecimal input, String units) {
    assertEquals(expected + units, String.format(LimsUtils.makeFormatString(input), input, units));
  }

}
