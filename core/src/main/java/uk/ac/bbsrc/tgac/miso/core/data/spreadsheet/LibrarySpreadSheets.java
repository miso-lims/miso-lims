package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
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
      Column.forString("i7 Index Name", listIndexName(1)), //
      Column.forString("i7 Index", listIndex(1)), //
      Column.forString("i5 Index Name", listIndexName(2)), //
      Column.forString("i5 Index", listIndex(2)), //
      Column.forString("Sample Name", library -> library.getSample().getName()), //
      Column.forString("Sample Alias", library -> library.getSample().getAlias()), //
      Column.forString("Sample Barcode", library -> library.getSample().getIdentificationBarcode()), //
      Column.forString("Identity Name", detailedSample(SampleIdentity.class, SampleIdentity::getName, "")), //
      Column.forString("Identity Alias", detailedSample(SampleIdentity.class, SampleIdentity::getAlias, "")), //
      Column.forString("External Identifier", true, detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", true, detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel)), //
  POOL_PREPARATION("Pool Preparation", //
      Column.forString("Name", Library::getName), //
      Column.forString("Alias", Library::getAlias), //
      Column.forString("Group ID", groupIdFunction()), //
      Column.forString("Description", Library::getDescription), //
      Column.forBigDecimal("Concentration", Library::getConcentration), //
      Column.forString("Concentration Units", lib -> lib.getConcentrationUnits() == null ? "" : lib.getConcentrationUnits().getRawLabel()), //
      Column.forInteger("Size (bp)", Library::getDnaSize), //
      Column.forString("Index 1", listIndex(1)), //
      Column.forString("Index 2", listIndex(2)), //
      Column.forString("Index Family",
          lib -> lib.getIndices() == null || lib.getIndices().isEmpty() ? "" : lib.getIndices().iterator().next().getFamily().getName())), //
  DILUTION_PREPARATION("Dilution Preparation", //
      Column.forString("Name", Library::getName), //
      Column.forString("Alias", Library::getAlias), //
      Column.forString("Group ID", groupIdFunction()), //
      Column.forString("Description", Library::getDescription), //
      Column.forBigDecimal("Concentration", Library::getConcentration), //
      Column.forString("Concentration Units", lib -> lib.getConcentrationUnits() == null ? "" : lib.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", Library::getVolume), //
      Column.forString("Volume Units", lib -> lib.getVolumeUnits() == null ? "" : lib.getVolumeUnits().getRawLabel()), //
      Column.forInteger("Size (bp)", Library::getDnaSize), //
      Column.forString("Index 1", listIndex(1)), //
      Column.forString("Index 2", listIndex(2)), //
      Column.forString("Index Family",
          lib -> lib.getIndices() == null || lib.getIndices().isEmpty() ? "" : lib.getIndices().iterator().next().getFamily().getName()));

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

  private static Function<Library, String> listIndex(int position) {
    return library -> library.getIndices().stream().filter(i -> i.getPosition() == position)
        .map(Index::getSequence)
        .findFirst().orElse("");
  }

  private static Function<Library, String> listIndexName(int position) {
    return library -> library.getIndices().stream().filter(i -> i.getPosition() == position)
        .map(Index::getName)
        .findFirst().orElse("");
  }

  private static Function<Library, String> groupIdFunction() {
    return lib -> {
      if (!LimsUtils.isDetailedLibrary(lib)) {
        return "";
      }
      GroupIdentifiable entity = ((DetailedSample) lib.getSample()).getEffectiveGroupIdEntity();
      return entity == null ? "" : entity.getGroupId();
    };
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
