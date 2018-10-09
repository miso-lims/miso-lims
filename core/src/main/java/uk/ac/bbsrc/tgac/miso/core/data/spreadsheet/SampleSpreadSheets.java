package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum SampleSpreadSheets implements Spreadsheet<Sample> {
  TRACKING_LIST("Tracking List", //
      Arrays.asList(SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
          SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Name", Sample::getName), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel)),
  
  TRANSFER_LIST("Transfer List", //
      Arrays.asList(SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME), //
      Column.forString("Alias", Sample::getAlias),
      Column.forString("Type", detailedSample(SampleStock.class, (sam -> {
        if (sam.getSampleClass().getAlias().contains("DNA")) {
          return "DNA";
        } else if (sam.getSampleClass().getAlias().contains("RNA")) {
          return "RNA";
        } else {
          return "Other";
        }
      }), "Other")), //
      Column.forString("External Identifier",  detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forDouble("VOL (uL)", Sample::getVolume), //
      Column.forDouble("[] (ng/uL)", Sample::getConcentration), //
      Column.forDouble("Total (ng)",
          (sam -> (sam.getVolume() != null && sam.getConcentration() != null) ? sam.getVolume() * sam.getConcentration() : null)), //
      Column.forString("Subproject",
          (sam-> (LimsUtils.isDetailedSample(sam) && ((DetailedSample) sam).getSubproject() != null ? 
              ((DetailedSample) sam).getSubproject().getAlias() : ""))), //
      Column.forString("Group ID", effectiveGroupIdProperty(DetailedSample::getGroupId)), //
      Column.forString("Group Description", effectiveGroupIdProperty(DetailedSample::getGroupDescription))
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

  private static Function<Sample, String> effectiveGroupIdProperty(Function<DetailedSample, String> getter) {
    return s -> {
      if (LimsUtils.isDetailedSample(s)) {
        DetailedSample parent = ((DetailedSample) s).getEffectiveGroupIdSample().orElse(null);
        if (parent != null) {
          return getter.apply(parent);
        }
      }
      return "";
    };
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
