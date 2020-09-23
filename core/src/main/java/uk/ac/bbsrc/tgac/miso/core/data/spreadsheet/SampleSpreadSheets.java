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
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum SampleSpreadSheets implements Spreadsheet<Sample> {
  TRACKING_LIST("Tracking List", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
          SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("External Identifier", true, detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", true, detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel)),
  
  BIOBANK_TRANSFER_LIST("BioBank Transfer List", //
      Arrays.asList(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Tissue Origin", detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), ""))),

  TRANSFER_LIST("Transfer List", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Type", dnaOrRna()), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forBigDecimal("VOL (uL)", Sample::getVolume), //
      Column.forBigDecimal("[] (ng/uL)", Sample::getConcentration), //
      Column.forBigDecimal("Total (ng)",
          (sam -> (sam.getVolume() != null && sam.getConcentration() != null) ? sam.getVolume().multiply(sam.getConcentration()) : null)), //
      Column.forString("Subproject", true,
          (sam-> (LimsUtils.isDetailedSample(sam) && ((DetailedSample) sam).getSubproject() != null ? 
              ((DetailedSample) sam).getSubproject().getAlias() : ""))), //
      Column.forString("Group ID", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Group Description", true, effectiveGroupIdProperty(GroupIdentifiable::getGroupDescription)), //
      Column.forString("Barcode", Sample::getIdentificationBarcode)
  ), //

  TRANSFER_LIST_V2("Transfer List V2", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Class", dnaOrRna()), //
      Column.forString("Origin", detailedSample(SampleTissue.class, st -> st.getTissueOrigin().getAlias(), "")), //
      Column.forString("Type", detailedSample(SampleTissue.class, st -> st.getTissueType().getAlias(), "")), //
      Column.forString("Material",
          detailedSample(SampleTissue.class, st -> st.getTissueMaterial() != null ? st.getTissueMaterial().getAlias() : "", "")), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forBigDecimal("VOL (uL)", Sample::getInitialVolume), //
      Column.forBigDecimal("[] (ng/uL)", Sample::getConcentration), //
      Column.forBigDecimal("Total (ng)",
          (sam -> (sam.getVolume() != null && sam.getConcentration() != null) ? sam.getVolume().multiply(sam.getConcentration()) : null)), //
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Group Description", effectiveGroupIdProperty(GroupIdentifiable::getGroupDescription)), //
      Column.forString("Barcode", Sample::getIdentificationBarcode)
  ), //
  INITIAL_EXTRACTION_YIELDS("Initial Extraction Yields List", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias), Column.forString("Type", dnaOrRna()), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forBigDecimal("VOL (uL)", Sample::getInitialVolume), //
      Column.forBigDecimal("[] (ng/uL)", Sample::getConcentration), //
      Column.forBigDecimal("Total (ng)",
          (sam -> (sam.getVolume() != null && sam.getConcentration() != null) ? sam.getVolume().multiply(sam.getConcentration()) : null)), //
      Column.forString("Subproject",
          (sam -> (LimsUtils.isDetailedSample(sam) && ((DetailedSample) sam).getSubproject() != null
              ? ((DetailedSample) sam).getSubproject().getAlias()
              : ""))), //
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Group Description", effectiveGroupIdProperty(GroupIdentifiable::getGroupDescription)), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("Created By", s -> s.getCreator().getLoginName()), //
      Column.forDate("Created Date", Sample::getCreationTime)),
  PROCESSING_AND_EXTRACTION("Processing & Extraction", //
      Arrays.asList(SampleTissue.CATEGORY_NAME, SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME,
          SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Project", sample -> sample.getProject().getShortName()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Description", Sample::getDescription), //
      Column.forInteger("Slides", detailedSample(SampleSlide.class, SampleSlide::getSlides, null)), //
      Column.forInteger("Discards", detailedSample(SampleSlide.class, SampleSlide::getDiscards, null)), //
      Column.forString("QC Status", qcStatusFunction()), //
      Column.forString("QC Status Note", detailedSample(DetailedSample.class, DetailedSample::getDetailedQcStatusNote, "")), //
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? "" : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; "))), //
      Column.forString("QCs", sam -> sam.getQCs() == null ? ""
          : sam.getQCs().stream().map(qc -> qc.getType().getAlias() + ": " + LimsUtils.toNiceString(qc.getResults()))
              .collect(Collectors.joining("; ")))), //
  RNA_LIBRARY_PREPARATION("RNA Library Preparation", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Project", sample -> sample.getProject().getShortName()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Description", Sample::getDescription), //
      Column.forBigDecimal("Concentration", Sample::getConcentration), //
      Column.forString("Concentration Units", sam -> sam.getConcentrationUnits() == null ? "" : sam.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", Sample::getVolume), //
      Column.forString("Volume Units", sam -> sam.getVolumeUnits() == null ? "" : sam.getVolumeUnits().getRawLabel()), //
      Column.forBigDecimal("DV200", sam -> qcValue(sam, "DV200")), //
      Column.forBigDecimal("RIN", sam -> qcValue(sam, "RIN")), //
      Column.forString("QC Status", qcStatusFunction()), //
      Column.forString("QC Status Note", true, detailedSample(DetailedSample.class, DetailedSample::getDetailedQcStatusNote, "")), //
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? "" : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; ")))), //
  DNA_LIBRARY_PREPARATION("DNA Library Preparation", //
      Arrays.asList(Sample.PLAIN_CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Project", sample -> sample.getProject().getShortName()), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Group ID", effectiveGroupIdProperty(GroupIdentifiable::getGroupId)), //
      Column.forString("Description", Sample::getDescription), //
      Column.forBigDecimal("Concentration", Sample::getConcentration), //
      Column.forString("Concentration Units", sam -> sam.getConcentrationUnits() == null ? "" : sam.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", Sample::getVolume), //
      Column.forString("Volume Units", sam -> sam.getVolumeUnits() == null ? "" : sam.getVolumeUnits().getRawLabel()), //
      Column.forString("QC Status", qcStatusFunction()), //
      Column.forString("QC Status Note", true, detailedSample(DetailedSample.class, DetailedSample::getDetailedQcStatusNote, "")), //
      Column.forString("Notes",
          sam -> sam.getNotes() == null ? "" : sam.getNotes().stream().map(Note::getText).collect(Collectors.joining("; ")))
      );

  private static <S extends DetailedSample, T> Function<Sample, T> detailedSample(Class<S> clazz, Function<S, T> function, T defaultValue) {
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

  private static BigDecimal qcValue(Sample sample, String qcTypeName) {
    return sample.getQCs().stream()
        .filter(qc -> qcTypeName.equals(qc.getType().getName()))
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
