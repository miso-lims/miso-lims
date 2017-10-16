package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleLCMTubeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.SampleHandsOnTable;

public abstract class AbstractBulkSampleIT extends AbstractIT {

  protected void saveSingleAndAssertSuccess(SampleHandsOnTable table) {
    HandsOnTableSaveResult result = table.save();

    assertTrue("Sample save", result.getItemsSaved() == 1);
    assertTrue("Server errors", result.getServerErrors().isEmpty());
    assertTrue("Save errors", result.getSaveErrors().isEmpty());

    assertTrue("Sample name generation", table.getText(SamColumns.NAME, 0).contains("SAM"));
    assertTrue("Sample alias generation", !isStringEmptyOrNull(table.getText(SamColumns.ALIAS, 0)));
  }

  protected void assertPlainSampleAttributes(Map<String, String> attributes, Sample sample, boolean newlyCreated) {
    if (newlyCreated) {
      assertEntityAttribute(SamColumns.PROJECT, attributes, sample, s -> s.getProject().getShortName());
    }
    assertEntityAttribute(SamColumns.ALIAS, attributes, sample, Sample::getAlias);
    assertEntityAttribute(SamColumns.DESCRIPTION, attributes, sample, s -> s.getDescription() == null ? "" : s.getDescription());
    assertEntityAttribute(SamColumns.ID_BARCODE, attributes, sample,
        s -> s.getIdentificationBarcode() == null ? null : s.getIdentificationBarcode());
    assertEntityAttribute(SamColumns.SAMPLE_TYPE, attributes, sample, Sample::getSampleType);
    assertEntityAttribute(SamColumns.SCIENTIFIC_NAME, attributes, sample, Sample::getScientificName);
    if (!LimsUtils.isIdentitySample(sample)) {
      assertEntityAttribute(SamColumns.RECEIVE_DATE, attributes, sample,
          s -> s.getReceivedDate() == null ? "" : LimsUtils.formatDate(s.getReceivedDate()));
    }
  }

  protected void assertDetailedSampleAttributes(Map<String, String> attributes, DetailedSample sample) {
    assertEntityAttribute(SamColumns.QC_STATUS, attributes, sample,
        s -> (s.getDetailedQcStatus() == null ? "Not Ready" : s.getDetailedQcStatus().getDescription()));
    if (!sample.getSampleClass().getSampleCategory().equals(SampleIdentity.CATEGORY_NAME)) {
      assertNotNull("parent is not null", sample.getParent());
    }
    assertEntityAttribute(SamColumns.GROUP_ID, attributes, sample, DetailedSample::getGroupId);
    assertEntityAttribute(SamColumns.GROUP_DESCRIPTION, attributes, sample, DetailedSample::getGroupDescription);
  }

  protected void assertSampleClass(String sampleClass, DetailedSample sample) {
    assertEquals("confirm sample class", sampleClass, sample.getSampleClass().getAlias());
  }

  protected void assertIdentityAttributes(Map<String, String> attributes, SampleIdentity sample) {
    assertEntityAttribute(SamColumns.EXTERNAL_NAME, attributes, sample, SampleIdentity::getExternalName);
    assertEntityAttribute(SamColumns.DONOR_SEX, attributes, sample, s -> s.getDonorSex() == null ? "Unknown" : s.getDonorSex().getLabel());
  }

  protected void assertTissueAttributes(Map<String, String> attributes, SampleTissue sample) {
    assertEntityAttribute(SamColumns.TISSUE_MATERIAL, attributes, sample,
        s -> s.getTissueMaterial() == null ? "(None)" : s.getTissueMaterial().getAlias());
    assertEntityAttribute(SamColumns.REGION, attributes, sample, SampleTissue::getRegion);
    assertEntityAttribute(SamColumns.SECONDARY_ID, attributes, sample, SampleTissue::getSecondaryIdentifier);
    assertEntityAttribute(SamColumns.LAB, attributes, sample, s -> s.getLab() == null ? "(None)" : s.getLab().getItemLabel());
    assertEntityAttribute(SamColumns.TISSUE_ORIGIN, attributes, sample, s -> s.getTissueOrigin().getItemLabel());
    assertEntityAttribute(SamColumns.TISSUE_TYPE, attributes, sample, s -> s.getTissueType().getItemLabel());
    assertEntityAttribute(SamColumns.PASSAGE_NUMBER, attributes, sample,
        s -> s.getPassageNumber() == null ? "" : s.getPassageNumber().toString());
    assertEntityAttribute(SamColumns.TIMES_RECEIVED, attributes, sample, s -> s.getTimesReceived().toString());
    assertEntityAttribute(SamColumns.TUBE_NUMBER, attributes, sample, s -> s.getTubeNumber().toString());
  }

  protected void assertSlideAttributes(Map<String, String> attributes, SampleSlide sample) {
    assertEntityAttribute(SamColumns.SLIDES, attributes, sample, s -> s.getSlides().toString());
    assertEntityAttribute(SamColumns.DISCARDS, attributes, sample, s -> s.getDiscards() == null ? null : s.getDiscards().toString());
    assertEntityAttribute(SamColumns.THICKNESS, attributes, sample,
        s -> s.getThickness() == null ? null : sample.getThickness().toString());
    assertEntityAttribute(SamColumns.STAIN, attributes, sample, s -> s.getStain() == null ? null : s.getStain().getName());
  }

  protected void assertLcmTubeAttributes(Map<String, String> attributes, SampleLCMTube sample) {
    assertEntityAttribute(SamColumns.SLIDES_CONSUMED, attributes, sample, s -> s.getSlidesConsumed().toString());
  }

  protected void assertAnalyteAttributes(Map<String, String> attributes, DetailedSample sample) {
    assertEntityAttribute(SamColumns.VOLUME, attributes, sample, s -> s.getVolume() == null ? null : s.getVolume().toString());
    assertEntityAttribute(SamColumns.CONCENTRATION, attributes, sample,
        s -> s.getConcentration() == null ? null : s.getConcentration().toString());
  }

  protected void assertStockAttributes(Map<String, String> attributes, SampleStock sample) {
    assertEntityAttribute(SamColumns.STR_STATUS, attributes, sample, s -> s.getStrStatus().getLabel());
  }

  protected void assertRnaStockSampleAttributes(Map<String, String> attributes, SampleStock sample) {
    assertEntityAttribute(SamColumns.DNASE_TREATED, attributes, sample, s -> getQcPassedString(s.getDNAseTreated()));
  }

  protected void assertAliquotAttributes(Map<String, String> attributes, SampleAliquot sample) {
    assertEntityAttribute(SamColumns.PURPOSE, attributes, sample, s -> s.getSamplePurpose().getAlias());
  }

  protected void assertAllForIdentity(Map<String, String> identity, Long sampleId, boolean newlyCreated) {
    SampleIdentity target = (SampleIdentity) getSession().get(SampleIdentityImpl.class, sampleId);

    assertPlainSampleAttributes(identity, target, newlyCreated);
    assertDetailedSampleAttributes(identity, target);
    assertIdentityAttributes(identity, target);
  }

  protected void assertAllForTissue(Map<String, String> slide, Long sampleId, boolean newlyCreated) {
    SampleTissue target = (SampleTissue) getSession().get(SampleTissueImpl.class, sampleId);

    assertPlainSampleAttributes(slide, target, newlyCreated);
    assertDetailedSampleAttributes(slide, target);
    assertTissueAttributes(slide, target);
  }

  protected void assertAllForSlide(Map<String, String> slide, Long sampleId, boolean newlyCreated) {
    SampleSlide target = (SampleSlide) getSession().get(SampleSlideImpl.class, sampleId);

    assertPlainSampleAttributes(slide, target, newlyCreated);
    assertDetailedSampleAttributes(slide, target);
    assertSlideAttributes(slide, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(slide, tissueParent);
    }
  }

  protected void assertAllForLcmTube(Map<String, String> lcmTube, Long sampleId, boolean newlyCreated) {
    SampleLCMTube target = (SampleLCMTube) getSession().get(SampleLCMTubeImpl.class, sampleId);

    assertPlainSampleAttributes(lcmTube, target, newlyCreated);
    assertDetailedSampleAttributes(lcmTube, target);
    assertLcmTubeAttributes(lcmTube, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(lcmTube, tissueParent);
    }
  }

  protected void assertAllForTissueProcessing(Map<String, String> tproc, Long sampleId, boolean newlyCreated) {
    SampleTissueProcessing target = (SampleTissueProcessing) getSession().get(SampleTissueProcessingImpl.class, sampleId);

    assertPlainSampleAttributes(tproc, target, newlyCreated);
    assertDetailedSampleAttributes(tproc, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(tproc, tissueParent);
    }
  }

  protected void assertAllForStock(Map<String, String> stock, Long sampleId, boolean newlyCreated, boolean isRNA) {
    SampleStock target = (SampleStock) getSession().get(SampleStockImpl.class, sampleId);

    assertPlainSampleAttributes(stock, target, newlyCreated);
    assertDetailedSampleAttributes(stock, target);
    assertStockAttributes(stock, target);
    assertAnalyteAttributes(stock, target);

    if (isRNA) {
      assertRnaStockSampleAttributes(stock, target);
    }

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(stock, tissueParent);

      SampleIdentity identityAncestor = LimsUtils.getParent(SampleIdentity.class, target);
      assertIdentityAttributes(stock, identityAncestor);
    }
  }

  protected void assertAllForAliquot(Map<String, String> aliquot, Long sampleId, boolean newlyCreated, boolean isRNA) {
    SampleAliquot target = (SampleAliquot) getSession().get(SampleAliquotImpl.class, sampleId);

    assertPlainSampleAttributes(aliquot, target, newlyCreated);
    assertDetailedSampleAttributes(aliquot, target);
    assertAnalyteAttributes(aliquot, target);
    assertAliquotAttributes(aliquot, target);

    if (newlyCreated) {
      SampleStock stockParent = LimsUtils.getParent(SampleStock.class, target);
      assertStockAttributes(aliquot, stockParent);
      if (isRNA) {
        assertRnaStockSampleAttributes(aliquot, stockParent);
      }

      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(aliquot, tissueParent);
    }
  }

  protected Long getIdForRow(HandsOnTable table, Integer row) {
    String newId = table.getText(SamColumns.NAME, row).substring(3, table.getText(SamColumns.NAME, 0).length());
    return Long.valueOf(newId);
  }
}
