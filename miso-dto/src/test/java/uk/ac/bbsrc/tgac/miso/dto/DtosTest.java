package uk.ac.bbsrc.tgac.miso.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import ca.on.oicr.gsi.runscanner.rs.dto.IlluminaNotificationDto;
import ca.on.oicr.gsi.runscanner.rs.dto.NotificationDto;
import ca.on.oicr.gsi.runscanner.rs.dto.PacBioNotificationDto;
import ca.on.oicr.gsi.runscanner.rs.dto.type.HealthType;

public class DtosTest {

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
    assertEquals(4L, tissue.getTissueOrigin().getId());
    assertNotNull(tissue.getTissueType());
    assertEquals(5L, tissue.getTissueType().getId());

    assertNotNull(tissue.getParent());
    assertTrue(LimsUtils.isIdentitySample(tissue.getParent()));
    SampleIdentity identity = (SampleIdentity) tissue.getParent();
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
    assertEquals(4L, tissue.getTissueOrigin().getId());
    assertNotNull(tissue.getTissueType());
    assertEquals(5L, tissue.getTissueType().getId());

    assertNotNull(tissue.getParent());
    assertTrue(LimsUtils.isIdentitySample(tissue.getParent()));
    SampleIdentity identity = (SampleIdentity) tissue.getParent();
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
    SampleIdentity identity = (SampleIdentity) tissue.getParent();
    assertEquals("externalName", identity.getExternalName());
  }

  // TODO: Can the code repetition be reduced here?
  @Test
  public void testConvertToUtilDate_Illumina() throws ParseException {
    NotificationDto dto = fullyPopulatedIlluminaNotificationDto("RUN_B");
    Run run = Dtos.to(dto, null);
    assertThat(dto.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE), is(LimsUtils.formatDate(run.getStartDate())));
  }

  @Test
  public void testConvertToUtilDate_PacBio() throws ParseException {
    NotificationDto dto = fullyPopulatedPacBioNotificationDto("RUN_B");
    Run run = Dtos.to(dto, null);
    assertThat(dto.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE), is(LimsUtils.formatDate(run.getStartDate())));
  }

  static IlluminaNotificationDto fullyPopulatedIlluminaNotificationDto(String sequencerName) {
    IlluminaNotificationDto notificationDto = new IlluminaNotificationDto();
    notificationDto.setRunAlias("TEST_RUN_NAME");
    notificationDto.setSequencerFolderPath("/sequencers/TEST_RUN_FOLDER");
    notificationDto.setContainerSerialNumber("CONTAINER_ID");
    notificationDto.setSequencerName(sequencerName);
    notificationDto.setLaneCount(8);
    notificationDto.setHealthType(HealthType.RUNNING);
    notificationDto.setStartDate(LocalDateTime.of(2017, 2, 23, 0, 0));
    notificationDto.setCompletionDate(LocalDateTime.of(2017, 2, 27, 0, 0));
    notificationDto.setPairedEndRun(true);
    notificationDto.setSoftware("Fido Opus SEAdog Standard Interface Layer");
    notificationDto.setRunBasesMask("y151,I8,y151");
    notificationDto.setNumCycles(20);
    notificationDto.setImgCycle(19);
    notificationDto.setScoreCycle(18);
    notificationDto.setCallCycle(17);
    return notificationDto;
  }

  static PacBioNotificationDto fullyPopulatedPacBioNotificationDto(String sequencerName) {
    PacBioNotificationDto notificationDto = new PacBioNotificationDto();
    notificationDto.setRunAlias("TEST_RUN_NAME");
    notificationDto.setSequencerFolderPath("/sequencers/TEST_RUN_FOLDER");
    notificationDto.setContainerSerialNumber("CONTAINER_ID");
    notificationDto.setSequencerName(sequencerName);
    notificationDto.setLaneCount(8);
    notificationDto.setHealthType(HealthType.RUNNING);
    notificationDto.setStartDate(LocalDateTime.of(2017, 2, 23, 0, 0));
    notificationDto.setCompletionDate(LocalDateTime.of(2017, 2, 27, 0, 0));
    notificationDto.setPairedEndRun(true);
    notificationDto.setSoftware("Fido Opus SEAdog Standard Interface Layer");
    return notificationDto;
  }

}
