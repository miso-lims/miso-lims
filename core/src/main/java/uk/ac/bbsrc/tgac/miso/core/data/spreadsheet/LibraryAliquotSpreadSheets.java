package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum LibraryAliquotSpreadSheets implements Spreadsheet<LibraryAliquot> {
  TRACKING_LIST("Tracking List", //
      Column.forString("Name", LibraryAliquot::getName), //
      Column.forString("Barcode", LibraryAliquot::getIdentificationBarcode), //
      Column.forString("Library Name", libraryAliquot -> libraryAliquot.getLibrary().getName()), //
      Column.forString("Library Alias", libraryAliquot -> libraryAliquot.getLibrary().getAlias()), //
      Column.forString("Library Barcode", libraryAliquot -> libraryAliquot.getLibrary().getIdentificationBarcode()), //
      Column.forString("Library Type", libraryAliquot -> libraryAliquot.getLibrary().getLibraryType().getDescription()), //
      Column.forString("Index(es)", LibraryAliquotSpreadSheets::listIndices), //
      Column.forString("Targeted Sequencing",
          libraryAliquot -> libraryAliquot.getTargetedSequencing() != null ? libraryAliquot.getTargetedSequencing().getAlias() : ""), //
      Column.forString("Sample Name", libraryAliquot -> libraryAliquot.getLibrary().getSample().getName()), //
      Column.forString("Sample Alias", libraryAliquot -> libraryAliquot.getLibrary().getSample().getAlias()), //
      Column.forString("Sample Barcode", libraryAliquot -> libraryAliquot.getLibrary().getSample().getIdentificationBarcode()), //
      Column.forString("Identity Name", detailedSample(SampleIdentity.class, SampleIdentity::getName, "")), //
      Column.forString("Identity Alias", detailedSample(SampleIdentity.class, SampleIdentity::getAlias, "")), //
      Column.forString("External Identifier", detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel));

  private static <S extends DetailedSample, T> Function<LibraryAliquot, T> detailedSample(Class<S> clazz, Function<S, T> function,
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

  private static String listIndices(LibraryAliquot libraryAliquot) {
    return libraryAliquot.getLibrary().getIndices().stream().map(Index::getSequence).collect(Collectors.joining(", "));
  }

  private final List<Column<LibraryAliquot>> columns;
  private final String description;

  @SafeVarargs
  private LibraryAliquotSpreadSheets(String description, Column<LibraryAliquot>... columns) {
    this.description = description;
    this.columns = Arrays.asList(columns);
  }

  @Override
  public List<Column<LibraryAliquot>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return description;
  }
}
