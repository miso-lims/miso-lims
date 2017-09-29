package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

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
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.SampleHandsOnTable;

public abstract class AbstractBulkSampleIT extends AbstractIT {

  protected void saveSingleAndAssertSuccess(SampleHandsOnTable table) {
    HandsOnTableSaveResult result = table.save();

    assertTrue("Sample save", result.getItemsSaved() == 1);
    assertTrue("Server errors", result.getServerErrors().isEmpty());
    assertTrue("Save errors", result.getSaveErrors().isEmpty());

    assertTrue("Sample name generation", table.getText(Columns.NAME, 0).contains("SAM"));
    assertTrue("Sample alias generation", !isStringEmptyOrNull(table.getText(Columns.ALIAS, 0)));
  }

  protected void assertPlainSampleAttributes(Map<String, String> hotAttributes, Sample fromDb, boolean newlyCreated) {
    if (newlyCreated) {
      assertEquals("confirm project", hotAttributes.get(Columns.PROJECT), fromDb.getProject().getShortName());
    }
    assertEquals("confirm alias", hotAttributes.get(Columns.ALIAS), fromDb.getAlias());
    assertEquals("confirm description", hotAttributes.get(Columns.DESCRIPTION),
        (fromDb.getDescription() == null ? "" : fromDb.getDescription()));
    assertEquals("confirm matrix barcode", hotAttributes.get(Columns.ID_BARCODE),
        (fromDb.getIdentificationBarcode() == null ? "" : fromDb.getIdentificationBarcode()));
    assertEquals("confirm sample type", hotAttributes.get(Columns.SAMPLE_TYPE), fromDb.getSampleType());
    assertEquals("confirm scientific name", hotAttributes.get(Columns.SCIENTIFIC_NAME), fromDb.getScientificName());
    if (!LimsUtils.isIdentitySample(fromDb)) {
      assertEquals("confirm received date", hotAttributes.get(Columns.RECEIVE_DATE),
          (fromDb.getReceivedDate() == null ? "" : LimsUtils.formatDate(fromDb.getReceivedDate())));
    }
  }

  protected void assertDetailedSampleAttributes(Map<String, String> hotAttributes, DetailedSample fromDb) {
    assertEquals("confirm QC status", hotAttributes.get(Columns.QC_STATUS),
        (fromDb.getDetailedQcStatus() == null ? "Not Ready" : fromDb.getDetailedQcStatus().getDescription()));
    if (!fromDb.getSampleClass().getSampleCategory().equals(SampleIdentity.CATEGORY_NAME)) {
      assertNotNull("parent is not null", fromDb.getParent());
    }
    assertEquals("confirm group ID", hotAttributes.get(Columns.GROUP_ID), (fromDb.getGroupId() == null ? "" : fromDb.getGroupId()));
    assertEquals("confirm group description", hotAttributes.get(Columns.GROUP_DESCRIPTION),
        (fromDb.getGroupDescription() == null ? "" : fromDb.getGroupDescription()));
  }

  protected void assertSampleClass(String sampleClass, DetailedSample fromDb) {
    assertEquals("confirm sample class", sampleClass, fromDb.getSampleClass().getAlias());
  }

  protected void assertIdentityAttributes(Map<String, String> hotAttributes, SampleIdentity fromDb) {
    assertEquals("confirm external name", hotAttributes.get(Columns.EXTERNAL_NAME), fromDb.getExternalName());
    assertEquals("confirm donor sex", (hotAttributes.get(Columns.DONOR_SEX) == null ? "Unknown" : hotAttributes.get(Columns.DONOR_SEX)),
        fromDb.getDonorSex().getLabel());
  }

  protected void assertTissueAttributes(Map<String, String> hotAttributes, SampleTissue fromDb) {
    assertEquals("confirm tissue material", hotAttributes.get(Columns.TISSUE_MATERIAL),
        (fromDb.getTissueMaterial() == null ? "(None)" : fromDb.getTissueMaterial().getAlias()));
    assertEquals("confirm region", hotAttributes.get(Columns.REGION), (fromDb.getRegion() == null ? "" : fromDb.getRegion()));
    assertEquals("confirm secondary id", hotAttributes.get(Columns.SECONDARY_ID),
        (fromDb.getSecondaryIdentifier() == null ? "" : fromDb.getSecondaryIdentifier()));
    assertEquals("confirm lab", hotAttributes.get(Columns.LAB), (fromDb.getLab() == null ? "(None)" : fromDb.getLab().getItemLabel()));
    assertEquals("confirm tissue origin", hotAttributes.get(Columns.TISSUE_ORIGIN), fromDb.getTissueOrigin().getItemLabel());
    assertEquals("confirm tissue type", hotAttributes.get(Columns.TISSUE_TYPE), fromDb.getTissueType().getItemLabel());
    assertEquals("confirm passage number", hotAttributes.get(Columns.PASSAGE_NUMBER),
        (fromDb.getPassageNumber() == null ? "" : fromDb.getPassageNumber().toString()));
    assertEquals("confirm times received", hotAttributes.get(Columns.TIMES_RECEIVED),
        fromDb.getTimesReceived().toString());
    assertEquals("confirm tube number", hotAttributes.get(Columns.TUBE_NUMBER), fromDb.getTubeNumber().toString());
  }

  protected void assertSlideAttributes(Map<String, String> hotAttributes, SampleSlide fromDb) {
    assertEquals("confirm slides", hotAttributes.get(Columns.SLIDES), fromDb.getSlides().toString());
    assertEquals("confirm discards", hotAttributes.get(Columns.DISCARDS),
        (fromDb.getDiscards() == null ? "" : fromDb.getDiscards().toString()));
    assertEquals("confirm thickness", hotAttributes.get(Columns.THICKNESS),
        (fromDb.getThickness() == null ? "" : fromDb.getThickness().toString()));
    assertEquals("confirm stain", hotAttributes.get(Columns.STAIN), (fromDb.getStain() == null ? "(None)" : fromDb.getStain().getName()));
  }

  protected void assertLcmTubeAttributes(Map<String, String> hotAttributes, SampleLCMTube fromDb) {
    assertEquals("confirm slides consumed", hotAttributes.get(Columns.SLIDES_CONSUMED), fromDb.getSlidesConsumed().toString());
  }

  protected void assertAnalyteAttributes(Map<String, String> hotAttributes, DetailedSample fromDb) {
    assertEquals("confirm volume", hotAttributes.get(Columns.VOLUME).toString(),
        (fromDb.getVolume() == null ? "" : fromDb.getVolume().toString()));
    assertEquals("confirm concentration", hotAttributes.get(Columns.CONCENTRATION).toString(),
        (fromDb.getConcentration() == null ? "" : fromDb.getConcentration().toString()));
  }

  protected void assertStockAttributes(Map<String, String> hotAttributes, SampleStock fromDb) {
    assertEquals("confirm STR Status", hotAttributes.get(Columns.STR_STATUS), fromDb.getStrStatus().getLabel());
  }

  protected void assertRnaStockSampleAttributes(Map<String, String> hotAttributes, SampleStock fromDb) {
    assertEquals("confirm DNAse Treated", Boolean.valueOf(hotAttributes.get(Columns.DNASE_TREATED)),
        Boolean.valueOf(fromDb.getDNAseTreated()));
  }

  protected void assertAliquotAttributes(Map<String, String> hotAttributes, SampleAliquot fromDb) {
    assertEquals("confirm purpose", hotAttributes.get(Columns.PURPOSE), fromDb.getSamplePurpose().getAlias());
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
    String newId = table.getText(Columns.NAME, row).substring(3, table.getText(Columns.NAME, 0).length());
    return Long.valueOf(newId);
  }
}
