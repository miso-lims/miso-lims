package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum SampleSpreadSheets implements Spreadsheet<Sample> {
  TRACKING_LIST("Tracking List", //
      Column.forString("Name", Sample::getName), //
      Column.forString("Alias", Sample::getAlias), //
      Column.forString("Barcode", Sample::getIdentificationBarcode), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel));

  private static <S extends DetailedSample, T> Function<Sample, T> detailedSample(Class<S> clazz, Function<S, T> function, T defaultValue) {
    return s -> {
      if (LimsUtils.isDetailedSample(s)) {
        S parent = LimsUtils.getParent(clazz, (DetailedSample) s);
        if (parent != null) {
          return function.apply(parent);
        }
      }
      return defaultValue;
    };
  }

  private final List<Column<Sample>> columns;
  private final String description;

  @SafeVarargs
  private SampleSpreadSheets(String description, Column<Sample>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<Sample>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }
}
