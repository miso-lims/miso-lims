package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum LibrarySpreadSheets implements Spreadsheet<Library> {
  TRACKING_LIST("Tracking List", //
      Column.forString("Name", Library::getName), //
      Column.forString("Alias", Library::getAlias), //
      Column.forString("Requisition", LibrarySpreadSheets::getEffectiveRequisition), //
      Column.forString("Assay", LibrarySpreadSheets::getEffectiveAssay), //
      Column.forString("Tissue Origin", true, tissueOrigin()), //
      Column.forString("Tissue Type", true, tissueType()), //
      Column.forString("Barcode", Library::getIdentificationBarcode), //
      Column.forString("Library Type", library -> library.getLibraryType().getDescription()),
      Column.forString("Library Design", true, library -> ((DetailedLibrary) library).getLibraryDesignCode().getCode()), //
      Column.forString("i7 Index Name", library -> getName(library.getIndex1())), //
      Column.forString("i7 Index", library -> getSequence(library.getIndex1())), //
      Column.forString("i5 Index Name", library -> getName(library.getIndex2())), //
      Column.forString("i5 Index", library -> getSequence(library.getIndex2())), //
      Column.forString("Sample Name", library -> library.getSample().getName()), //
      Column.forString("Sample Alias", library -> library.getSample().getAlias()), //
      Column.forString("Sample Barcode", library -> library.getSample().getIdentificationBarcode()), //
      Column.forString("Identity Name", true, detailedSample(SampleIdentity.class, SampleIdentity::getName, "")), //
      Column.forString("Identity Alias", true, detailedSample(SampleIdentity.class, SampleIdentity::getAlias, "")), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", true,
          detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Location", BoxUtils::makeLocationLabel)), //
  POOL_PREPARATION("Pool Preparation", //
      Column.forString("Name", Library::getName), //
      Column.forString("Alias", Library::getAlias), //
      Column.forString("Tissue Origin", true, tissueOrigin()), //
      Column.forString("Tissue Type", true, tissueType()), //
      Column.forString("Group ID", true, groupIdFunction()), //
      Column.forString("Description", Library::getDescription), //
      Column.forBigDecimal("Concentration", Library::getConcentration), //
      Column.forString("Concentration Units",
          lib -> lib.getConcentrationUnits() == null ? "" : lib.getConcentrationUnits().getRawLabel()), //
      Column.forInteger("Size (bp)", Library::getDnaSize), //
      Column.forString("Index 1", library -> getSequence(library.getIndex1())), //
      Column.forString("Index 2", library -> getSequence(library.getIndex2())), //
      Column.forString("Index Family",
          lib -> lib.getIndex1() == null ? null : lib.getIndex1().getFamily().getName())), //
  DILUTION_PREPARATION("Dilution Preparation", //
      Column.forString("Name", Library::getName), //
      Column.forString("Alias", Library::getAlias), //
      Column.forString("Tissue Origin", true, tissueOrigin()), //
      Column.forString("Tissue Type", true, tissueType()), //
      Column.forString("Group ID", true, groupIdFunction()), //
      Column.forString("Description", Library::getDescription), //
      Column.forBigDecimal("Concentration", Library::getConcentration), //
      Column.forString("Concentration Units",
          lib -> lib.getConcentrationUnits() == null ? "" : lib.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", Library::getVolume), //
      Column.forString("Volume Units", lib -> lib.getVolumeUnits() == null ? "" : lib.getVolumeUnits().getRawLabel()), //
      Column.forInteger("Size (bp)", Library::getDnaSize), //
      Column.forString("Index 1", library -> getSequence(library.getIndex1())), //
      Column.forString("Index 2", library -> getSequence(library.getIndex2())), //
      Column.forString("Index Family", lib -> lib.getIndex1() == null ? null : lib.getIndex1().getFamily().getName()));

  private static Function<Library, String> tissueOrigin() {
    return l -> {
      if (!LimsUtils.isDetailedLibrary(l)) {
        return null;
      }
      return ((DetailedSample) l.getSample()).getTissueAttributes().getTissueOrigin().getAlias();
    };
  }

  private static Function<Library, String> tissueType() {
    return l -> {
      if (!LimsUtils.isDetailedLibrary(l)) {
        return null;
      }
      return ((DetailedSample) l.getSample()).getTissueAttributes().getTissueType().getAlias();
    };
  }

  private static <S extends DetailedSample, T> Function<Library, T> detailedSample(Class<S> clazz,
      Function<S, T> function,
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

  private static String getSequence(LibraryIndex index) {
    return index == null ? null : index.getSequence();
  }

  private static String getName(LibraryIndex index) {
    return index == null ? null : index.getName();
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

  private static String getEffectiveRequisition(Library library) {
    Requisition requisition = LimsUtils.getEffectiveRequisition(library);
    if (requisition == null) {
      return null;
    }
    return requisition.getAlias();
  }

  private static String getEffectiveAssay(Library library) {
    Requisition requisition = LimsUtils.getEffectiveRequisition(library);
    if (requisition == null || requisition.getAssays() == null || requisition.getAssays().isEmpty()) {
      return null;
    }
    return requisition.getAssays().stream()
        .map(assay -> assay.getAlias() + " v" + assay.getVersion())
        .collect(Collectors.joining("; "));
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
