package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum SampleSpreadSheets implements Spreadsheet<Sample> {
  TRACKING_LIST("Tracking List", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
          SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Tissue Origin", true,
          detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), "")), //
      Column.forString("Type", true, dnaOrRna()), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Box", sam -> sam.getBox() == null ? null : sam.getBox().getAlias()), //
      Column.forString("Position", sam -> sam.getBoxPosition()), //
      Column.forString("Class", true, sampleClass()), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Group ID", true,
          sam -> LimsUtils.isDetailedSample(sam) ? ((DetailedSample) sam).getGroupId() : null), //
      Column.forString("Timepoint", true, detailedSample(SampleTissue.class, SampleTissue::getTimepoint, null)), //
      Column.forString("Subproject", true,
          (sam -> (LimsUtils.isDetailedSample(sam) && ((DetailedSample) sam).getSubproject() != null
              ? ((DetailedSample) sam).getSubproject().getAlias()
              : ""))), //
      Column.forString("Location", BoxUtils::makeLocationLabel), //
      Column.forBigDecimal("Concentration", Sample::getConcentration), //
      Column.forString("Concentration Units",
          sam -> sam.getConcentrationUnits() == null ? "" : sam.getConcentrationUnits().getRawLabel())),

  BIOBANK_TRANSFER_LIST("BioBank Transfer List", //
      Arrays.asList(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Tissue Origin", true,
          detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), ""))),

  TRANSFER_LIST("Transfer List", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Type", true, dnaOrRna()), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Class", true, sampleClass()), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forBigDecimal("VOL (uL)", Sample::getVolume), //
      Column.forBigDecimal("[] (ng/uL)", Sample::getConcentration), //
      Column.forBigDecimal("Total (ng)", getYield()) //
  ), //

  TRANSFER_LIST_V2("Transfer List V2", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Origin", detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), "")), //
      Column.forString("Type", detailedSample(SampleTissue.class, st -> st.getTissueType().getAlias(), "")), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Box", sam -> sam.getBox() == null ? null : sam.getBox().getAlias()), //
      Column.forString("Position", sam -> sam.getBoxPosition()), //
      Column.forString("Class", true, sampleClass()), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Timepoint", true, detailedSample(SampleTissue.class, SampleTissue::getTimepoint, null)), //
      Column.forString("Subproject", true,
          (sam -> (LimsUtils.isDetailedSample(sam) && ((DetailedSample) sam).getSubproject() != null
              ? ((DetailedSample) sam).getSubproject().getAlias()
              : ""))), //
      Column.forBigDecimal("VOL (uL)", Sample::getVolume), //
      Column.forBigDecimal("[] (ng/uL)", Sample::getConcentration), //
      Column.forBigDecimal("Total (ng)", getYield()) //
  ), //
  INITIAL_EXTRACTION_YIELDS("Initial Extraction Yields List", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), Column.forString("Type", dnaOrRna()), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forBigDecimal("VOL (uL)", Sample::getInitialVolume), //
      Column.forBigDecimal("[] (ng/uL)", Sample::getConcentration), //
      Column.forBigDecimal("Total (ng)", getYield()), //
      Column.forString("Subproject", true,
          (sam -> (LimsUtils.isDetailedSample(sam) && ((DetailedSample) sam).getSubproject() != null
              ? ((DetailedSample) sam).getSubproject().getAlias()
              : ""))), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Group Description", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupDescription)), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Created By", s -> s.getCreator().getLoginName()), //
      Column.forDate("Created Date", Sample::getCreationTime)), //
  PROCESSING_AND_EXTRACTION("Processing & Extraction", //
      Arrays.asList(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME,
          SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Project", sample -> sample.getProject().getCode()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Description", Sample::getDescription), //
      Column.forInteger("Slides", true, detailedSample(SampleSlide.class, SampleSlide::getSlides, null)), //
      Column.forString("QC Status", qcStatusFunction()), //
      Column.forString("QC Status Note",
          detailedSample(DetailedSample.class, DetailedSample::getDetailedQcStatusNote, "")), //
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? ""
              : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; "))), //
      Column.forString("QCs", sam -> sam.getQCs() == null ? ""
          : sam.getQCs().stream()
              .map(qc -> qc.getType().getAlias() + ": " + LimsUtils.toNiceString(qc.getResults()))
              .collect(Collectors.joining("; ")))), //
  RNA_LIBRARY_PREPARATION("RNA Library Preparation", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Project", sample -> sample.getProject().getCode()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Description", Sample::getDescription), //
      Column.forBigDecimal("Concentration", Sample::getConcentration), //
      Column.forString("Concentration Units",
          sam -> sam.getConcentrationUnits() == null ? "" : sam.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", Sample::getVolume), //
      Column.forString("Volume Units", sam -> sam.getVolumeUnits() == null ? "" : sam.getVolumeUnits().getRawLabel()), //
      Column.forBigDecimal("DV200", sam -> qcValue(sam, "DV200", false)), //
      Column.forBigDecimal("RIN", sam -> qcValue(sam, "RIN", true)), //
      Column.forString("QC Status", qcStatusFunction()), //
      Column.forString("QC Status Note", true,
          detailedSample(DetailedSample.class, DetailedSample::getDetailedQcStatusNote, "")), //
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? ""
              : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; ")))), //
  DNA_LIBRARY_PREPARATION("DNA Library Preparation", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Project", sample -> sample.getProject().getCode()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Box Alias", sample -> sample.getBox() == null ? null : sample.getBox().getAlias()), //
      Column.forString("Position", sample -> sample.getBoxPosition()), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Description", Sample::getDescription), //
      Column.forBigDecimal("Concentration", Sample::getConcentration), //
      Column.forString("Concentration Units",
          sam -> sam.getConcentrationUnits() == null ? "" : sam.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", Sample::getVolume), //
      Column.forString("Volume Units", sam -> sam.getVolumeUnits() == null ? "" : sam.getVolumeUnits().getRawLabel()), //
      Column.forString("QC Status", qcStatusFunction()), //
      Column.forString("QC Status Note", true,
          detailedSample(DetailedSample.class, DetailedSample::getDetailedQcStatusNote, "")), //
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? ""
              : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; ")))), //
  RUNNING_SHEET("Running Sheet", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)),
      Column.forString("", blankColumn()), // intentional blank column
      Column.forString("Class", true, sampleClass()),
      Column.forString("Material", true, detailedSample(SampleTissue.class,
          tissue -> tissue.getTissueMaterial() == null ? null : tissue.getTissueMaterial().getAlias(), null)),
      Column.forBigDecimal("Yield (ng)", getYield()),
      Column.forString("Type", true, detailedSample(SampleTissue.class, st -> st.getTissueType().getAlias(), null)), //
      Column.forBigDecimal("RNA Initial Vol.", true, ifRna(Sample::getInitialVolume)),
      Column.forBigDecimal("RNA Conc.", true, ifRna(Sample::getConcentration)),
      Column.forBigDecimal("RNA Used", true, ifRna(sam -> sam.getInitialVolume() != null && sam.getVolume() != null
          ? sam.getInitialVolume().subtract(sam.getVolume())
          : null)),
      Column.forBigDecimal("RNA Total Remaining", true, ifRna(getYield())),
      Column.forBigDecimal("RIN", ifRna(sam -> qcValue(sam, "RIN", true))),
      Column.forBigDecimal("DV200", ifRna(sam -> qcValue(sam, "DV200", false))),
      Column.forBigDecimal("DNA Current Vol.", true, ifDna(Sample::getVolume)),
      Column.forBigDecimal("DNA Conc.", true, ifDna(Sample::getConcentration)),
      Column.forBigDecimal("DNA Total Remaining", true, ifDna(getYield())),
      Column.forString("% Genomic", true, blankColumn()), // intentionally blank - column no longer used
      Column.forString("RNA Identity", true, ifRna(detailedSample(SampleIdentity.class, Sample::getName, null))),
      Column.forString("RNA Tissue", true, ifRna(detailedSample(SampleTissue.class, Sample::getName, null))),
      Column.forString("RNA Slide", true, ifRna(detailedSample(SampleSlide.class, Sample::getName, null))),
      Column.forString("RNA Curl", true, ifRna(detailedSample(SampleTissuePiece.class, Sample::getName, null))),
      Column.forString("RNA Stock", true, ifRna(detailedSample(SampleStock.class, Sample::getName, null))),
      Column.forString("RNA Aliquot", true, ifRna(detailedSample(SampleAliquot.class, Sample::getName, null))),
      Column.forString("RNA Library ID", true, blankColumn()), // To be filled in manually
      Column.forString("RNA Library Alias", true, blankColumn()), // To be filled in manually
      Column.forString("RNA LDI ID", true, blankColumn()), // To be filled in manually
      Column.forString("DNA Identity", true, ifDna(detailedSample(SampleIdentity.class, Sample::getName, null))),
      Column.forString("DNA Tissue", true, ifDna(detailedSample(SampleTissue.class, Sample::getName, null))),
      Column.forString("DNA Slide", true, ifDna(detailedSample(SampleSlide.class, Sample::getName, null))),
      Column.forString("DNA Curl", true, ifDna(detailedSample(SampleTissuePiece.class, Sample::getName, null))),
      Column.forString("DNA Stock", true, ifDna(detailedSample(SampleStock.class, Sample::getName, null))),
      Column.forString("DNA Aliquot", true, ifDna(detailedSample(SampleAliquot.class, Sample::getName, null))),
      Column.forString("DNA Library ID", true, blankColumn()), // To be filled in manually
      Column.forString("DNA Library Alias", true, blankColumn()), // To be filled in manually
      Column.forString("DNA LDI ID", true, blankColumn()), // To be filled in manually
      Column.forString("DNA LDI Alias", true, blankColumn()) // To be filled in manually
  ), //
  MOH_EXTRACTION("MOH Extraction Tracker", //
      Arrays.asList(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME,
          SampleAliquot.CATEGORY_NAME), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Sample Type", true, mohSampleType()), //
      Column.forString("Match Code", true, mohMatchCode()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Type", true, dnaOrRna()), //
      Column.forBigDecimal("Total (ng)", getInitialYield()), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)),
      Column.forString("Requisition", false, effectiveRequisition()));


  private static Function<Sample, String> effectiveRequisition() {
    return sam -> {
      if (sam.getRequisition() != null) {
        return sam.getRequisition().getAlias();
      }
      if (LimsUtils.isDetailedSample(sam)) {
        for (DetailedSample detailed = (DetailedSample) sam; detailed != null; detailed = detailed.getParent()) {
          if (detailed.getRequisition() != null) {
            return detailed.getRequisition().getAlias();
          }
        }
      }
      return null;
    };
  }

  private static Function<Sample, String> mohSampleType() {
    return sam -> {
      if (!LimsUtils.isDetailedSample(sam)) {
        return null;
      }
      DetailedSample detailed = (DetailedSample) sam;
      TissueType tissueType = detailed.getParentAttributes().getTissueAttributes().getTissueType();
      if ("R".equals(tissueType.getAlias())) {
        return "BC DNA";
      } else if (detailed.getSampleClass().getAlias().contains("RNA")) {
        return "RNA";
      } else if (detailed.getSampleClass().getAlias().contains("DNA")) {
        return "T DNA";
      } else {
        return "?";
      }
    };
  }

  private static Function<Sample, String> mohMatchCode() {
    return sam -> {
      if (!LimsUtils.isDetailedSample(sam)) {
        return null;
      }
      DetailedSample detailed = (DetailedSample) sam;
      String sampleType = mohSampleType().apply(detailed);
      String externalName = detailed.getParentAttributes().getIdentityAttributes().getExternalName();
      return externalName + " " + sampleType;
    };
  }

  private static Function<Sample, String> blankColumn() {
    return sample -> null;
  }

  private static <T> Function<Sample, T> ifRna(Function<Sample, T> function) {
    return ifAcidType("RNA", function);
  }

  private static <T> Function<Sample, T> ifDna(Function<Sample, T> function) {
    return ifAcidType("DNA", function);
  }

  private static <T> Function<Sample, T> ifAcidType(String type, Function<Sample, T> function) {
    return detailedSample(SampleStock.class, sam -> {
      if (sam.getSampleClass().getAlias().contains(type)) {
        return function.apply(sam);
      } else {
        return null;
      }
    }, null);
  }

  private static Function<Sample, String> sampleClass() {
    return sam -> LimsUtils.isDetailedSample(sam)
        ? ((DetailedSample) sam).getSampleClass().getAlias()
        : null;
  }

  private static Function<Sample, BigDecimal> getYield() {
    return sam -> (sam.getVolume() != null && sam.getConcentration() != null)
        ? sam.getVolume().multiply(sam.getConcentration())
        : null;
  }

  private static Function<Sample, BigDecimal> getInitialYield() {
    return sam -> (sam.getInitialVolume() != null && sam.getConcentration() != null)
        ? sam.getInitialVolume().multiply(sam.getConcentration())
        : null;
  }

  private static <S extends DetailedSample, T> Function<Sample, T> detailedSample(Class<S> clazz,
      Function<S, T> function, T defaultValue) {
    return s -> {
      if (clazz.isInstance(s)) {
        return function.apply(clazz.cast(s));
      }
      if (LimsUtils.isDetailedSample(s)) {
        S parent = LimsUtils.getParent(clazz, (DetailedSample) s);
        if (parent != null) {
          return function.apply(parent);
        }
      }
      return defaultValue;
    };
  }

  private static Function<Sample, String> dnaOrRna() {
    return detailedSample(SampleStock.class, sam -> {
      if (sam.getSampleClass().getAlias().contains("DNA")) {
        return "DNA";
      } else if (sam.getSampleClass().getAlias().contains("RNA")) {
        return "RNA";
      } else {
        return "Other";
      }
    }, "Other");
  }

  private static Function<Sample, String> effectiveGroupIdProperty(Function<GroupIdentifiable, String> getter) {
    return s -> {
      if (LimsUtils.isDetailedSample(s)) {
        GroupIdentifiable parent = ((DetailedSample) s).getEffectiveGroupIdEntity();
        if (parent != null) {
          return getter.apply(parent);
        }
      }
      return "";
    };
  }

  private static Function<Sample, String> qcStatusFunction() {
    return s -> s.getDetailedQcStatus() == null ? "Not Ready" : s.getDetailedQcStatus().getDescription();
  }

  private static BigDecimal qcValue(Sample sample, String qcTypeName, boolean matchStartOnly) {
    return sample.getQCs().stream()
        .filter(qc -> matchStartOnly ? qc.getType().getName().startsWith(qcTypeName)
            : qc.getType().getName().contains(qcTypeName))
        .max(Comparator.comparing(QC::getDate))
        .map(QC::getResults).orElse(null);
  }

  private final List<Column<Sample>> columns;
  private final List<String> allowedClasses;
  private final String description;

  @SafeVarargs
  private SampleSpreadSheets(String description, List<String> allowedClasses, Column<Sample>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
    this.allowedClasses = allowedClasses;
  }

  @Override
  public List<Column<Sample>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }

  public List<String> allowedClasses() {
    return allowedClasses;
  }
}
