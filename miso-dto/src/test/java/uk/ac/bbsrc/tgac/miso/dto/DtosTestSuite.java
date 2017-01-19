package uk.ac.bbsrc.tgac.miso.dto;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

public class DtosTestSuite {

  @Test
  public void testToStockWithNewParents() {
    SampleStockDto dto = new SampleStockDto();
    dto.setRootSampleClassId(1L);
    dto.setParentTissueSampleClassId(2L);
    dto.setSampleClassId(3L);
    dto.setExternalName("externalName");
    dto.setTissueOriginId(4L);
    dto.setTissueTypeId(5L);

    Sample sample = Dtos.to(dto);
    assertNotNull(sample);
    assertTrue(LimsUtils.isStockSample(sample));
    SampleStock analyte = (SampleStock) sample;
    assertNotNull(analyte.getSampleClass());
    assertEquals(Long.valueOf(3L), analyte.getSampleClass().getId());

    assertNotNull(analyte.getParent());
    assertTrue(LimsUtils.isTissueSample(analyte.getParent()));
    SampleTissue tissue = (SampleTissue) analyte.getParent();
    assertNotNull(tissue.getSampleClass());
    assertEquals(Long.valueOf(2L), tissue.getSampleClass().getId());
    assertNotNull(tissue.getTissueOrigin());
    assertEquals(Long.valueOf(4L), tissue.getTissueOrigin().getId());
    assertNotNull(tissue.getTissueType());
    assertEquals(Long.valueOf(5L), tissue.getTissueType().getId());

    assertNotNull(tissue.getParent());
    assertTrue(LimsUtils.isIdentitySample(tissue.getParent()));
    Identity identity = (Identity) tissue.getParent();
    assertEquals("externalName", identity.getExternalName());
  }

  @Test
  public void testToAliquotWithNewParents() {
    SampleAliquotDto dto = new SampleAliquotDto();
    dto.setRootSampleClassId(1L);
    dto.setParentTissueSampleClassId(2L);
    dto.setSampleClassId(4L);
    dto.setExternalName("externalName");
    dto.setTissueOriginId(4L);
    dto.setTissueTypeId(5L);
    dto.setStockClassId(3L);

    Sample sample = Dtos.to(dto);
    assertNotNull(sample);
    assertTrue(LimsUtils.isAliquotSample(sample));
    SampleAliquot aliquot = (SampleAliquot) sample;
    assertNotNull(aliquot.getSampleClass());
    assertEquals(Long.valueOf(4L), aliquot.getSampleClass().getId());

    assertNotNull(aliquot.getParent());
    assertTrue(LimsUtils.isStockSample(aliquot.getParent()));
    SampleStock stock = (SampleStock) aliquot.getParent();
    assertEquals(Long.valueOf(3L), stock.getSampleClass().getId());

    assertNotNull(stock.getParent());
    assertTrue(LimsUtils.isTissueSample(stock.getParent()));
    SampleTissue tissue = (SampleTissue) stock.getParent();
    assertNotNull(tissue.getSampleClass());
    assertEquals(Long.valueOf(2L), tissue.getSampleClass().getId());
    assertNotNull(tissue.getTissueOrigin());
    assertEquals(Long.valueOf(4L), tissue.getTissueOrigin().getId());
    assertNotNull(tissue.getTissueType());
    assertEquals(Long.valueOf(5L), tissue.getTissueType().getId());

    assertNotNull(tissue.getParent());
    assertTrue(LimsUtils.isIdentitySample(tissue.getParent()));
    Identity identity = (Identity) tissue.getParent();
    assertEquals("externalName", identity.getExternalName());
  }

  @Test
  public void testToAliquotWithExistingParent() {
    SampleAliquotDto dto = new SampleAliquotDto();
    dto.setRootSampleClassId(1L);
    dto.setParentTissueSampleClassId(2L);
    dto.setSampleClassId(3L);
    dto.setExternalName("externalName");
    dto.setTissueOriginId(4L);
    dto.setTissueTypeId(5L);
    dto.setParentId(6L); // known ID should cause other parent details to be ignored

    Sample sample = Dtos.to(dto);
    assertNotNull(sample);
    assertTrue(LimsUtils.isAliquotSample(sample));
    SampleAliquot analyte = (SampleAliquot) sample;
    assertNotNull(analyte.getSampleClass());
    assertEquals(Long.valueOf(3L), analyte.getSampleClass().getId());

    assertNotNull(analyte.getParent());
    assertTrue(LimsUtils.isDetailedSample(analyte.getParent()));
    DetailedSample parent = analyte.getParent();
    assertEquals(6L, parent.getId());

    assertNull(parent.getParent());
  }

  @Test
  public void testToTissueWithParent() {
    SampleTissueDto dto = new SampleTissueDto();
    dto.setRootSampleClassId(1L);
    dto.setExternalName("externalName");
    dto.setSampleClassId(2L);
    dto.setTissueOriginId(3L);
    dto.setTissueTypeId(4L);

    Sample sample = Dtos.to(dto);
    assertNotNull(sample);
    assertTrue(LimsUtils.isTissueSample(sample));
    SampleTissue tissue = (SampleTissue) sample;
    assertNotNull(tissue.getParent());
    assertTrue(LimsUtils.isIdentitySample(tissue.getParent()));
    Identity identity = (Identity) tissue.getParent();
    assertEquals("externalName", identity.getExternalName());
  }

}
