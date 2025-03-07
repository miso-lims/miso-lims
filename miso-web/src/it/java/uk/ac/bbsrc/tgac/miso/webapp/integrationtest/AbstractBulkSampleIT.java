package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.Before;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotRna;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockRna;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotRnaImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockSingleCellImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissuePieceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.impl.QueryBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public abstract class AbstractBulkSampleIT extends AbstractIT {

  protected static final long projectId = 1L;
  protected static final long identityClassId = 1L;
  protected static final long tissueClassId = 23L;
  protected static final long slideClassId = 24L;
  protected static final long tissuePieceClassId = 10L;
  protected static final long lcmTubeClassId = 10L;
  protected static final long gStockClassId = 11L;
  protected static final long rStockClassId = 13L;
  protected static final long cStockClassId = 14L;
  protected static final long gAliquotClassId = 15L;
  protected static final long rAliquotClassId = 17L;
  protected static final long cAliquotClassId = 21L;
  protected static final long mRnaClassId = 19L;
  protected static final long singleCellClassId = 25;
  protected static final long singleCellStockClassId = 26;
  protected static final long singleCellAliquotClassId = 27;

  @Before
  public void setup() {
    login();
  }

  protected void assertPlainSampleAttributes(Map<String, String> attributes, Sample sample, boolean newlyCreated) {
    if (newlyCreated) {
      assertEntityAttribute(SamColumns.PROJECT, attributes, sample, s -> s.getProject().getCode());
    }
    assertEntityAttribute(SamColumns.ALIAS, attributes, sample, Sample::getAlias);
    assertEntityAttribute(SamColumns.DESCRIPTION, attributes, sample,
        s -> s.getDescription() == null ? "" : s.getDescription());
    assertEntityAttribute(SamColumns.ID_BARCODE, attributes, sample,
        s -> s.getIdentificationBarcode() == null ? "" : s.getIdentificationBarcode());
    assertEntityAttribute(SamColumns.SAMPLE_TYPE, attributes, sample, Sample::getSampleType);
    assertEntityAttribute(SamColumns.SCIENTIFIC_NAME, attributes, sample,
        s -> s.getScientificName() == null ? "" : s.getScientificName().getAlias());
    assertEntityAttribute(SamColumns.BOX_ALIAS, attributes, sample,
        s -> s.getBox() == null ? "" : s.getBox().getAlias());
    assertEntityAttribute(SamColumns.BOX_POSITION, attributes, sample,
        s -> s.getBoxPosition() == null ? "" : s.getBoxPosition());
    if (attributes.containsKey(SamColumns.RECEIVE_DATE)) {
      assertReceiptAttributes(attributes, sample);
    }
    assertEntityAttribute(SamColumns.QC_STATUS, attributes, sample,
        s -> (s.getDetailedQcStatus() == null ? "Not Ready" : s.getDetailedQcStatus().getDescription()));
  }

  protected void assertReceiptAttributes(Map<String, String> attributes, Sample sample) {
    QueryBuilder<TransferSample, TransferSample> builder =
        new QueryBuilder<>(getSession(), TransferSample.class, TransferSample.class);
    Join<TransferSample, Transfer> join = builder.getJoin(builder.getRoot(), TransferSample_.transfer);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(TransferSample_.item), sample));
    builder.addPredicate(builder.getCriteriaBuilder().isNotNull(join.get(Transfer_.senderLab)));
    TransferSample receipt = builder.getSingleResultOrNull();

    assertNotNull("A receipt transfer should be created", receipt);

    assertEntityAttribute(SamColumns.RECEIVE_DATE, attributes, receipt,
        s -> s == null ? "" : LimsUtils.formatDate(s.getTransfer().getTransferTime()));
    assertEntityAttribute(SamColumns.RECEIVE_TIME, attributes, receipt, AbstractBulkSampleIT::getReceiptTime);
    assertEntityAttribute(SamColumns.RECEIVED_FROM, attributes, receipt,
        s -> s == null ? "" : s.getTransfer().getSenderLab().getAlias());
    assertEntityAttribute(SamColumns.RECEIVED_BY, attributes, receipt,
        s -> s == null ? "" : s.getTransfer().getRecipientGroup().getName());
    assertEntityAttribute(SamColumns.RECEIPT_CONFIRMED, attributes, receipt,
        s -> s == null ? "" : booleanString(s.isReceived(), "Unknown"));
    assertEntityAttribute(SamColumns.RECEIPT_QC_PASSED, attributes, receipt,
        s -> s == null ? "" : booleanString(s.isQcPassed(), "Unknown"));
    assertEntityAttribute(SamColumns.RECEIPT_QC_NOTE, attributes, receipt,
        s -> s == null ? "" : emptyIfNull(s.getQcNote()));
  }

  private static String getReceiptTime(TransferSample receipt) {
    if (receipt == null) {
      return "";
    }
    DateFormat formatter = new SimpleDateFormat("h:mm a");
    return formatter.format(receipt.getTransfer().getTransferTime()).toLowerCase();
  }

  protected void assertDetailedSampleAttributes(Map<String, String> attributes, DetailedSample sample) {
    if (!sample.getSampleClass().getSampleCategory().equals(SampleIdentity.CATEGORY_NAME)) {
      assertNotNull("parent is not null", sample.getParent());
    }
    assertEntityAttribute(SamColumns.GROUP_ID, attributes, sample, s -> s.getGroupId() == null ? "" : s.getGroupId());
    assertEntityAttribute(SamColumns.GROUP_DESCRIPTION, attributes, sample,
        s -> s.getGroupDescription() == null ? "" : s.getGroupDescription());
    assertEntityAttribute(SamColumns.CREATION_DATE, attributes, sample,
        s -> s.getCreationDate() == null ? "" : LimsUtils.formatDate(s.getCreationDate()));
  }

  protected void assertSampleClass(String sampleClass, DetailedSample sample) {
    assertEquals("confirm sample class", sampleClass, sample.getSampleClass().getAlias());
  }

  protected void assertIdentityAttributes(Map<String, String> attributes, SampleIdentity sample) {
    assertEntityAttribute(SamColumns.EXTERNAL_NAME, attributes, sample, SampleIdentity::getExternalName);
    assertEntityAttribute(SamColumns.DONOR_SEX, attributes, sample,
        s -> s.getDonorSex() == null ? "Unknown" : s.getDonorSex().getLabel());
    assertEntityAttribute(SamColumns.CONSENT, attributes, sample, s -> s.getConsentLevel().getLabel());
  }

  protected void assertTissueAttributes(Map<String, String> attributes, SampleTissue sample) {
    assertEntityAttribute(SamColumns.TISSUE_MATERIAL, attributes, sample,
        s -> s.getTissueMaterial() == null ? "" : s.getTissueMaterial().getAlias());
    assertEntityAttribute(SamColumns.REGION, attributes, sample, s -> s.getRegion() == null ? "" : s.getRegion());
    assertEntityAttribute(SamColumns.SECONDARY_ID, attributes, sample,
        s -> s.getSecondaryIdentifier() == null ? "" : s.getSecondaryIdentifier());
    assertEntityAttribute(SamColumns.LAB, attributes, sample, s -> s.getLab() == null ? "" : s.getLab().getAlias());
    assertEntityAttribute(SamColumns.TISSUE_ORIGIN, attributes, sample, s -> s.getTissueOrigin().getItemLabel());
    assertEntityAttribute(SamColumns.TISSUE_TYPE, attributes, sample, s -> s.getTissueType().getItemLabel());
    assertEntityAttribute(SamColumns.PASSAGE_NUMBER, attributes, sample,
        s -> s.getPassageNumber() == null ? "" : s.getPassageNumber().toString());
    assertEntityAttribute(SamColumns.TIMES_RECEIVED, attributes, sample, s -> s.getTimesReceived().toString());
    assertEntityAttribute(SamColumns.TUBE_NUMBER, attributes, sample, s -> s.getTubeNumber().toString());
  }

  protected void assertSlideAttributes(Map<String, String> attributes, SampleSlide sample) {
    assertEntityAttribute(SamColumns.SLIDES, attributes, sample, s -> s.getSlides().toString());
    assertEntityAttribute(SamColumns.THICKNESS, attributes, sample,
        s -> s.getThickness() == null ? "" : sample.getThickness().toString());
    assertEntityAttribute(SamColumns.STAIN, attributes, sample,
        s -> s.getStain() == null ? "" : s.getStain().getName());
  }

  protected void assertLcmTubeAttributes(Map<String, String> attributes, SampleTissuePiece sample) {
    assertEntityAttribute(SamColumns.SLIDES_CONSUMED, attributes, sample, s -> s.getSlidesConsumed().toString());
  }

  protected void assertSingleCellAttributes(Map<String, String> attributes, SampleSingleCell sample) {
    assertEntityAttribute(SamColumns.INITIAL_CELL_CONC, attributes, sample,
        s -> emptyIfNull(s.getInitialCellConcentration()));
    assertEntityAttribute(SamColumns.TARGET_CELL_RECOVERY, attributes, sample,
        s -> emptyIfNull(s.getTargetCellRecovery()));
    assertEntityAttribute(SamColumns.LOADING_CELL_CONC, attributes, sample,
        s -> emptyIfNull(s.getLoadingCellConcentration()));
    assertEntityAttribute(SamColumns.DIGESTION, attributes, sample, s -> s.getDigestion());
  }

  protected void assertAnalyteAttributes(Map<String, String> attributes, DetailedSample sample) {
    assertEntityAttribute(SamColumns.VOLUME, attributes, sample,
        s -> s.getVolume() == null ? "" : LimsUtils.toNiceString(s.getVolume()));
    assertEntityAttribute(SamColumns.CONCENTRATION, attributes, sample,
        s -> s.getConcentration() == null ? "" : LimsUtils.toNiceString(s.getConcentration()));
  }

  protected void assertStockAttributes(Map<String, String> attributes, SampleStock sample) {
    assertEntityAttribute(SamColumns.STR_STATUS, attributes, sample, s -> s.getStrStatus().getLabel());
  }

  protected void assertSingleCellStockAttributes(Map<String, String> attributes, SampleStockSingleCell sample) {
    assertEntityAttribute(SamColumns.TARGET_CELL_RECOVERY, attributes, sample,
        s -> emptyIfNull(s.getTargetCellRecovery()));
    assertEntityAttribute(SamColumns.CELL_VIABILITY, attributes, sample, s -> emptyIfNull(s.getCellViability()));
    assertEntityAttribute(SamColumns.LOADING_CELL_CONC, attributes, sample,
        s -> emptyIfNull(s.getLoadingCellConcentration()));
  }

  protected void assertRnaStockSampleAttributes(Map<String, String> attributes, SampleStockRna sample) {
    assertEntityAttribute(SamColumns.DNASE_TREATED, attributes, sample, s -> booleanString(s.getDnaseTreated()));
  }

  protected void assertAliquotAttributes(Map<String, String> attributes, SampleAliquot sample) {
    assertEntityAttribute(SamColumns.PURPOSE, attributes, sample, s -> s.getSamplePurpose().getAlias());
  }

  protected void assertSingleCellAliquotAttributes(Map<String, String> attributes, SampleAliquotSingleCell sample) {
    assertEntityAttribute(SamColumns.INPUT_INTO_LIBRARY, attributes, sample, s -> emptyIfNull(s.getInputIntoLibrary()));
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
    SampleTissuePiece target = (SampleTissuePiece) getSession().get(SampleTissuePieceImpl.class, sampleId);

    assertPlainSampleAttributes(lcmTube, target, newlyCreated);
    assertDetailedSampleAttributes(lcmTube, target);
    assertLcmTubeAttributes(lcmTube, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(lcmTube, tissueParent);
    }
  }

  protected void assertAllForSingleCell(Map<String, String> singleCell, long sampleId, boolean newlyCreated) {
    SampleSingleCell target = (SampleSingleCell) getSession().get(SampleSingleCellImpl.class, sampleId);

    assertPlainSampleAttributes(singleCell, target, newlyCreated);
    assertDetailedSampleAttributes(singleCell, target);
    assertSingleCellAttributes(singleCell, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(singleCell, tissueParent);
    }
  }

  protected void assertAllForTissueProcessing(Map<String, String> tproc, Long sampleId, boolean newlyCreated) {
    SampleTissueProcessing target =
        (SampleTissueProcessing) getSession().get(SampleTissueProcessingImpl.class, sampleId);

    assertPlainSampleAttributes(tproc, target, newlyCreated);
    assertDetailedSampleAttributes(tproc, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(tproc, tissueParent);
    }
  }

  protected void assertAllForStock(Map<String, String> stock, Long sampleId, boolean newlyCreated) {
    SampleStock target = (SampleStock) getSession().get(SampleStockImpl.class, sampleId);

    assertPlainSampleAttributes(stock, target, newlyCreated);
    assertDetailedSampleAttributes(stock, target);
    assertStockAttributes(stock, target);
    assertAnalyteAttributes(stock, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(stock, tissueParent);

      SampleIdentity identityAncestor = LimsUtils.getParent(SampleIdentity.class, target);
      assertIdentityAttributes(stock, identityAncestor);
    }
  }

  protected void assertAllForRnaStock(Map<String, String> stock, Long sampleId, boolean newlyCreated) {
    SampleStockRna target = (SampleStockRna) getSession().get(SampleStockImpl.class, sampleId);

    assertPlainSampleAttributes(stock, target, newlyCreated);
    assertDetailedSampleAttributes(stock, target);
    assertStockAttributes(stock, target);
    assertRnaStockSampleAttributes(stock, target);
    assertAnalyteAttributes(stock, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(stock, tissueParent);

      SampleIdentity identityAncestor = LimsUtils.getParent(SampleIdentity.class, target);
      assertIdentityAttributes(stock, identityAncestor);
    }
  }

  protected void assertAllForSingleCellStock(Map<String, String> stock, Long sampleId, boolean newlyCreated) {
    SampleStockSingleCell target = (SampleStockSingleCell) getSession().get(SampleStockSingleCellImpl.class, sampleId);

    assertPlainSampleAttributes(stock, target, newlyCreated);
    assertDetailedSampleAttributes(stock, target);
    assertStockAttributes(stock, target);
    assertAnalyteAttributes(stock, target);
    assertSingleCellStockAttributes(stock, target);

    if (newlyCreated) {
      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(stock, tissueParent);

      SampleSingleCell singleCellParent = LimsUtils.getParent(SampleSingleCell.class, target);
      assertSingleCellAttributes(stock, singleCellParent);

      SampleIdentity identityAncestor = LimsUtils.getParent(SampleIdentity.class, target);
      assertIdentityAttributes(stock, identityAncestor);
    }
  }

  protected void assertAllForAliquot(Map<String, String> aliquot, Long sampleId, boolean newlyCreated) {
    SampleAliquot target = (SampleAliquot) getSession().get(SampleAliquotImpl.class, sampleId);

    assertPlainSampleAttributes(aliquot, target, newlyCreated);
    assertDetailedSampleAttributes(aliquot, target);
    assertAnalyteAttributes(aliquot, target);
    assertAliquotAttributes(aliquot, target);

    if (newlyCreated) {
      SampleStock stockParent = LimsUtils.getParent(SampleStock.class, target);
      assertStockAttributes(aliquot, stockParent);

      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(aliquot, tissueParent);
    }
  }

  protected void assertAllForRnaAliquot(Map<String, String> aliquot, Long sampleId, boolean newlyCreated) {
    SampleAliquotRna target = (SampleAliquotRna) getSession().get(SampleAliquotRnaImpl.class, sampleId);
    assertNotNull(target);

    assertPlainSampleAttributes(aliquot, target, newlyCreated);
    assertDetailedSampleAttributes(aliquot, target);
    assertAnalyteAttributes(aliquot, target);
    assertAliquotAttributes(aliquot, target);

    if (newlyCreated) {
      SampleStockRna stockParent = LimsUtils.getParent(SampleStockRna.class, target);
      assertStockAttributes(aliquot, stockParent);
      assertRnaStockSampleAttributes(aliquot, stockParent);

      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(aliquot, tissueParent);
    }
  }

  protected void assertAllForSingleCellAliquot(Map<String, String> aliquot, Long sampleId, boolean newlyCreated) {
    SampleAliquotSingleCell target =
        (SampleAliquotSingleCell) getSession().get(SampleAliquotSingleCellImpl.class, sampleId);

    assertPlainSampleAttributes(aliquot, target, newlyCreated);
    assertDetailedSampleAttributes(aliquot, target);
    assertAnalyteAttributes(aliquot, target);
    assertAliquotAttributes(aliquot, target);
    assertSingleCellAliquotAttributes(aliquot, target);

    if (newlyCreated) {
      SampleStockSingleCell stockParent = LimsUtils.getParent(SampleStockSingleCell.class, target);
      assertSingleCellStockAttributes(aliquot, stockParent);

      SampleSingleCell singleCellParent = LimsUtils.getParent(SampleSingleCell.class, target);
      assertSingleCellAttributes(aliquot, singleCellParent);

      SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, target);
      assertTissueAttributes(aliquot, tissueParent);
    }
  }

  protected Long getIdForRow(HandsOnTable table, Integer row) {
    String newId = table.getText(SamColumns.NAME, row).substring(3, table.getText(SamColumns.NAME, 0).length());
    return Long.valueOf(newId);
  }

}
