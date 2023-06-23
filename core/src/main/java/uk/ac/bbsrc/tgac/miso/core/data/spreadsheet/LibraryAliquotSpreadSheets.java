package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public enum LibraryAliquotSpreadSheets implements Spreadsheet<LibraryAliquot> {
  TRACKING_LIST("Tracking List", //
      Column.forString("Name", LibraryAliquot::getName), //
      Column.forString("Alias", LibraryAliquot::getAlias), //
      Column.forString("Tissue Origin", true, tissueOrigin()), //
      Column.forString("Tissue Type", true, tissueType()), //
      Column.forString("Barcode", LibraryAliquot::getIdentificationBarcode), //
      Column.forString("Library Name", libraryAliquot -> libraryAliquot.getLibrary().getName()), //
      Column.forString("Library Alias", libraryAliquot -> libraryAliquot.getLibrary().getAlias()), //
      Column.forString("Library Barcode", libraryAliquot -> libraryAliquot.getLibrary().getIdentificationBarcode()), //
      Column.forString("Library Type", libraryAliquot -> libraryAliquot.getLibrary().getLibraryType().getDescription()), //
      Column.forString("Library Design", true,
          libraryAliquot -> ((DetailedLibraryAliquot) libraryAliquot).getLibraryDesignCode().getCode()), //
      Column.forString("Index(es)", LibraryAliquotSpreadSheets::listIndices), //
      Column.forString("i7 Index", libraryAliquot -> getSequence(libraryAliquot.getLibrary().getIndex1())), //
      Column.forString("i5 Index", libraryAliquot -> getSequence(libraryAliquot.getLibrary().getIndex2())), //
      Column.forString("Targeted Sequencing",
          libraryAliquot -> libraryAliquot.getTargetedSequencing() != null
              ? libraryAliquot.getTargetedSequencing().getAlias()
              : ""), //
      Column.forString("Sample Name", libraryAliquot -> libraryAliquot.getLibrary().getSample().getName()), //
      Column.forString("Sample Alias", libraryAliquot -> libraryAliquot.getLibrary().getSample().getAlias()), //
      Column.forString("Sample Barcode",
          libraryAliquot -> libraryAliquot.getLibrary().getSample().getIdentificationBarcode()), //
      Column.forString("Identity Name", true, detailedSample(SampleIdentity.class, SampleIdentity::getName, "")), //
      Column.forString("Identity Alias", true, detailedSample(SampleIdentity.class, SampleIdentity::getAlias, "")), //
      Column.forString("External Identifier", true,
          detailedSample(SampleIdentity.class, SampleIdentity::getExternalName, "")), //
      Column.forString("Secondary Identifier", true,
          detailedSample(SampleTissue.class, SampleTissue::getSecondaryIdentifier, "")), //
      Column.forString("Group ID", true, groupIdFunction()), //
      Column.forString("Location", BoxUtils::makeLocationLabel)), //
  POOL_PREPARATION("Pool Preparation", //
      Column.forString("Name", LibraryAliquot::getName), //
      Column.forString("Alias", LibraryAliquot::getAlias), //
      Column.forString("Tissue Origin", true, tissueOrigin()), //
      Column.forString("Tissue Type", true, tissueType()), //
      Column.forString("Group ID", true, groupIdFunction()), //
      Column.forString("Library Description", aliquot -> aliquot.getLibrary().getDescription()), //
      Column.forBigDecimal("Concentration", LibraryAliquot::getConcentration), //
      Column.forString("Concentration Units",
          aliquot -> aliquot.getConcentrationUnits() == null ? "" : aliquot.getConcentrationUnits().getRawLabel()), //
      Column.forInteger("Size (bp)", LibraryAliquot::getDnaSize), //
      Column.forString("Index 1", libraryAliquot -> getSequence(libraryAliquot.getLibrary().getIndex1())), //
      Column.forString("Index 2", libraryAliquot -> getSequence(libraryAliquot.getLibrary().getIndex2())), //
      Column.forString("Index Family",
          aliquot -> aliquot.getLibrary().getIndex1() == null ? null
              : aliquot.getLibrary().getIndex1().getFamily().getName())), //
  DILUTION_PREPARATION("Dilution Preparation", //
      Column.forString("Name", LibraryAliquot::getName), //
      Column.forString("Alias", LibraryAliquot::getAlias), //
      Column.forString("Tissue Origin", true, tissueOrigin()), //
      Column.forString("Tissue Type", true, tissueType()), //
      Column.forString("Group ID", true, groupIdFunction()), //
      Column.forString("Library Description", aliquot -> aliquot.getLibrary().getDescription()), //
      Column.forBigDecimal("Concentration", LibraryAliquot::getConcentration), //
      Column.forString("Concentration Units",
          aliquot -> aliquot.getConcentrationUnits() == null ? "" : aliquot.getConcentrationUnits().getRawLabel()), //
      Column.forBigDecimal("Volume", LibraryAliquot::getVolume), //
      Column.forString("Volume Units",
          aliquot -> aliquot.getVolumeUnits() == null ? "" : aliquot.getVolumeUnits().getRawLabel()), //
      Column.forInteger("Size (bp)", LibraryAliquot::getDnaSize), //
      Column.forString("Index 1", libraryAliquot -> getSequence(libraryAliquot.getLibrary().getIndex1())), //
      Column.forString("Index 2", libraryAliquot -> getSequence(libraryAliquot.getLibrary().getIndex2())), //
      Column.forString("Index Family",
          aliquot -> aliquot.getLibrary().getIndex1() == null ? null
              : aliquot.getLibrary().getIndex1().getFamily().getName()));

  private static <S extends DetailedSample, T> Function<LibraryAliquot, T> detailedSample(Class<S> clazz,
      Function<S, T> function,
      T defaultValue) {
    return aliquot -> {
      Sample sample = aliquot.getLibrary().getSample();
      if (clazz.isInstance(sample)) {
        return function.apply(clazz.cast(sample));
      }
      if (LimsUtils.isDetailedSample(sample)) {
        S parent = LimsUtils.getParent(clazz, (DetailedSample) sample);
        if (parent != null) {
          return function.apply(parent);
        }
      }
      return defaultValue;
    };
  }

  private static Function<LibraryAliquot, String> tissueOrigin() {
    return l -> {
      if (!LimsUtils.isDetailedLibraryAliquot(l)) {
        return null;
      }
      return ((DetailedSample) l.getLibrary().getSample()).getTissueAttributes().getTissueOrigin().getAlias();
    };
  }

  private static Function<LibraryAliquot, String> tissueType() {
    return l -> {
      if (!LimsUtils.isDetailedLibraryAliquot(l)) {
        return null;
      }
      return ((DetailedSample) l.getLibrary().getSample()).getTissueAttributes().getTissueType().getAlias();
    };
  }

  private static String listIndices(LibraryAliquot libraryAliquot) {
    Index index1 = libraryAliquot.getLibrary().getIndex1();
    Index index2 = libraryAliquot.getLibrary().getIndex2();
    if (index1 == null) {
      return null;
    } else if (index2 == null) {
      return index1.getSequence();
    } else {
      return index1.getSequence() + ", " + index2.getSequence();
    }
  }

  private static String getSequence(Index index) {
    return index == null ? null : index.getSequence();
  }

  private static Function<LibraryAliquot, String> groupIdFunction() {
    return aliquot -> {
      if (!LimsUtils.isDetailedLibraryAliquot(aliquot)) {
        return "";
      }
      GroupIdentifiable entity = ((DetailedSample) aliquot.getLibrary().getSample()).getEffectiveGroupIdEntity();
      return entity == null ? "" : entity.getGroupId();
    };
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
