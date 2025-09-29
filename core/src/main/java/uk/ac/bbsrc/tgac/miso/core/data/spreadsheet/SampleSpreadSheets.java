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
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
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
      Column.forString("Box", BoxUtils::makeBoxLabel), //
      Column.forString("Position", sam -> sam.getBoxPosition()), //
      Column.forString("Custody", custody()), //
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
      Column.forBigDecimal("Total (ng)", getYield())//
  ), //

  TRANSFER_LIST_V2("Transfer List V2", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Origin", detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), "")), //
      Column.forString("Type", detailedSample(SampleTissue.class, st -> st.getTissueType().getAlias(), "")), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Box", BoxUtils::makeBoxLabel), //
      Column.forString("Position", sam -> sam.getBoxPosition()), //
      Column.forString("Custody", custody()), //
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
      Column.forBigDecimal("Total (ng)", getYield())//
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
      Column.forString("Name", Sample::getName), // A
      Column.forString("Project", sample -> sample.getProject().getCode()), // B
      Column.forString("Alias", Sample::getAlias), // C
      Column.forString("Box Alias", BoxUtils::makeBoxLabel), // D
      Column.forString("Position", sample -> sample.getBoxPosition()), // E
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), // F
      Column.forString("Description", Sample::getDescription), // G
      Column.forString("Material", true, detailedSample(SampleTissue.class,
          tissue -> tissue.getTissueMaterial() == null ? null : tissue.getTissueMaterial().getAlias(), null)), // H
      Column.forString("Tissue Origin", true,
          detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), "")), // I
      Column.forString("Tissue Type", true,
          detailedSample(SampleTissue.class, st -> st.getTissueType().getAlias(), "")), // J
      Column.forBigDecimal("Concentration", Sample::getConcentration), // K
      Column.forBigDecimal("Volume", Sample::getVolume), // L
      Column.forDouble("DV200", sam -> {
        // May lose precision, but was requested because it works better in formulas
        BigDecimal value = qcValue(sam, "DV200", false);
        return value == null ? null : value.doubleValue();
      }), // M
      Column.forFormula("Material Needed", true, "IF(@H = \"FFPE\", 200, 50)"), // N
      Column.forFormula("SAM Vol. Needed", true, "@N / @K"), // O
      Column.forFormula("NFW Vol. Needed", true, "IF(@O > 10, 0, 10 - @O)"), // P
      Column.forFormula("RNAClean Beads", true, "IF(@M < 65, 193, 99)"), // Q
      Column.forFormula("Frag. Time", true, "IF(@M < 65, IF(@M < 55, 0, 4), 8)"), // R
      Column.forString("Index Well", true, sam -> null), // S
      Column.forString("LIB Conc.", true, sam -> null), // T
      Column.forString("LIB. Avg. Size", true, sam -> null), // U
      Column.forString("Adapter Contamination", true, sam -> null), // V
      Column.forString("LIB Vol. Needed for LDI", true, sam -> null), // W
      Column.forString("RSB Vol. Needed for LDI", true, sam -> null), // X
      Column.forString("LIB ID", true, sam -> null), // Y
      Column.forString("LIB Alias", true, sam -> null), // Z
      Column.forString("LDI ID", true, sam -> null), // AA
      Column.forString("LDI Alias", true, sam -> null), // AB
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? ""
              : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; ")))), // AC
  DNA_LIBRARY_PREPARATION("DNA Library Preparation", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), // A
      Column.forString("Project", sample -> sample.getProject().getCode()), // B
      Column.forString("Alias", Sample::getAlias), // C
      Column.forString("Box Alias", BoxUtils::makeBoxLabel), // D
      Column.forString("Position", sample -> sample.getBoxPosition()), // E
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), // F
      Column.forString("Description", Sample::getDescription), // G
      Column.forString("Material", true, detailedSample(SampleTissue.class,
          tissue -> tissue.getTissueMaterial() == null ? null : tissue.getTissueMaterial().getAlias(), null)), // H
      Column.forString("Tissue Origin", true,
          detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), "")), // I
      Column.forString("Tissue Type", true,
          detailedSample(SampleTissue.class, st -> st.getTissueType().getAlias(), "")), // J
      Column.forBigDecimal("Concentration", Sample::getConcentration), // K
      Column.forBigDecimal("Volume", Sample::getVolume), // L
      Column.forFormula("Material Needed", true, "IF(@H = \"Fresh Frozen\", 25, 100)"), // M
      Column.forFormula("SAM Vol. Needed", true, "@M/@K"), // N
      Column.forFormula("TE Vol. Needed", true, "IF(@N > 50, 0, 50 - @N)"), // O
      Column.forString("Index Well", true, sam -> null), // P
      Column.forString("1:10 Conc.", true, sam -> null), // Q
      Column.forFormula("Actual LIB Conc.", true, "@Q * 10"), // R
      Column.forString("LIB Avg. Size", true, sam -> null), // S
      Column.forString("Adapter Contamination", true, sam -> null), // T
      Column.forString("LIB Vol. Needed for LDI", true, sam -> null), // U
      Column.forString("TE Vol. Needed for LDI", true, sam -> null), // V
      Column.forString("LIB ID", true, sam -> null), // W
      Column.forString("LIB Alias", true, sam -> null), // X
      Column.forString("LDI ID", true, sam -> null), // Y
      Column.forString("LDI Alias", true, sam -> null), // Z
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? ""
              : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; ")))), // AA
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
      Column.forString("DNA LDI Alias", true, blankColumn())// To be filled in manually
  ), //
  MOH_EXTRACTION("MOH Extraction Tracker", //
      Arrays.asList(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME,
          SampleAliquot.CATEGORY_NAME), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Sample Type", true, mohSampleType()), //
      Column.forString("Material", true, detailedSample(SampleTissue.class,
          tissue -> tissue.getTissueMaterial() == null ? null : tissue.getTissueMaterial().getAlias(), null)), //
      Column.forString("Match Code", true, mohMatchCode()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Type", true, dnaOrRna()), //
      Column.forBigDecimal("Total (ng)", getInitialYield()), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)),
      Column.forString("Requisition", false, effectiveRequisition()));


  private static Function<Sample, String> custody() {
    return sam -> {
      if (sam.getTransferViews() == null || sam.getTransferViews().isEmpty()) {
        return null;
      }
      ListTransferView transfer = sam.getTransferViews().stream()
          .max(Comparator.comparing(ListTransferView::getTransferTime))
          .orElseThrow();
      if (transfer.getRecipientGroup() != null) {
        return transfer.getRecipientGroup().getName();
      } else {
        return transfer.getRecipient();
      }
    };
  }

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
