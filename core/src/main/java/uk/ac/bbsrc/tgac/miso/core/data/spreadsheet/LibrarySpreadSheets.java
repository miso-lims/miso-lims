package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum LibrarySpreadSheets implements Spreadsheet<Library> {
  TRACKING_LIST("Tracking List", //
      Column.forString("Name", Library::getName), //
      Column.forString("Alias", Library::getAlias), //
      Column.forString("Barcode", Library::getIdentificationBarcode), //
      Column.forString("Library Type", library -> library.getLibraryType().getDescription()), //
      Column.forString("Index(es)", LibrarySpreadSheets::listIndices), //
      Column.forString("Sample Name", library -> library.getSample().getName()), //
      Column.forString("Sample Alias", library -> library.getSample().getAlias()), //
      Column.forString("Sample Barcode", library -> library.getSample().getIdentificationBarcode()), //
      Column.forString("Identity Name", detailedSample(SampleIdentity.class, SampleIdentity::getName, "")), //
      Column.forString("Identity Alias", detailedSample(SampleIdentity.class, SampleIdentity::getAlias, "")), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel));

  private static <S extends DetailedSample, T> Function<Library, T> detailedSample(Class<S> clazz, Function<S, T> function,
      T defaultValue) {
    return l -> {
      if (clazz.isInstance(l.getSample())) {
        return function.apply(clazz.cast(l.getSample()));
      }
      if (LimsUtils.isDetailedSample(l.getSample())) {
        S parent = LimsUtils.getParent(clazz, (DetailedSample) l.getSample());
        if (parent != null) {
          return function.apply(parent);
        }
      }
      return defaultValue;
    };
  }

  private static String listIndices(Library library) {
    return library.getIndices().stream().map(Index::getSequence).collect(Collectors.joining(", "));
  }

  private final List<Column<Library>> columns;
  private final String description;

  @SafeVarargs
  private LibrarySpreadSheets(String description, Column<Library>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<Library>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }
}
