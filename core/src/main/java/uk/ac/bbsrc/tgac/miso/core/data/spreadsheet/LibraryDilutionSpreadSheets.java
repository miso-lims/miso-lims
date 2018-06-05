package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum LibraryDilutionSpreadSheets implements Spreadsheet<LibraryDilution> {
  TRACKING_LIST("Tracking List", //
      Column.forString("Name", LibraryDilution::getName), //
      Column.forString("Barcode", LibraryDilution::getIdentificationBarcode), //
      Column.forString("Library Name", dilution -> dilution.getLibrary().getName()), //
      Column.forString("Library Alias", dilution -> dilution.getLibrary().getAlias()), //
      Column.forString("Library Barcode", dilution -> dilution.getLibrary().getIdentificationBarcode()), //
      Column.forString("Library Type", dilution -> dilution.getLibrary().getLibraryType().getDescription()), //
      Column.forString("Index(es)", LibraryDilutionSpreadSheets::listIndices), //
      Column.forString("Targeted Sequencing",
          dilution -> dilution.getTargetedSequencing() != null ? dilution.getTargetedSequencing().getAlias() : ""), //
      Column.forString("Sample Name", dilution -> dilution.getLibrary().getSample().getName()), //
      Column.forString("Sample Alias", dilution -> dilution.getLibrary().getSample().getAlias()), //
      Column.forString("Sample Barcode", dilution -> dilution.getLibrary().getSample().getIdentificationBarcode()), //
      Column.forString("Identity Name", detailedSample(SampleIdentity.class, SampleIdentity::getName, "")), //
      Column.forString("Identity Alias", detailedSample(SampleIdentity.class, SampleIdentity::getAlias, "")), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel));

  private static <S extends DetailedSample, T> Function<LibraryDilution, T> detailedSample(Class<S> clazz, Function<S, T> function,
      T defaultValue) {
    return d -> {
      if (clazz.isInstance(d.getLibrary().getSample())) {
        return function.apply(clazz.cast(d.getLibrary().getSample()));
      }
      if (LimsUtils.isDetailedSample(d.getLibrary().getSample())) {
        S parent = LimsUtils.getParent(clazz, (DetailedSample) d.getLibrary().getSample());
        if (parent != null) {
          return function.apply(parent);
        }
      }
      return defaultValue;
    };
  }

  private static String listIndices(LibraryDilution dilution) {
    return dilution.getLibrary().getIndices().stream().map(Index::getSequence).collect(Collectors.joining(", "));
  }

  private final List<Column<LibraryDilution>> columns;
  private final String description;

  @SafeVarargs
  private LibraryDilutionSpreadSheets(String description, Column<LibraryDilution>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<LibraryDilution>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }
}
