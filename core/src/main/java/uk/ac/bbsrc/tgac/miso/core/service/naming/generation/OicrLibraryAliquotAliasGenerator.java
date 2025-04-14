package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.io.IOException;
import java.time.LocalDate;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrLibraryAliquotAliasGenerator
    extends OicrBaseLibraryAliasGenerator<LibraryAliquot, DetailedLibraryAliquot> {

  @Override
  public String generate(LibraryAliquot item) throws MisoNamingException, IOException {
    if (!isDetailed(item)) {
      throw new IllegalArgumentException("Can only generate an alias for detailed samples");
    }
    DetailedLibraryAliquot detailedItem = (DetailedLibraryAliquot) item;
    if (getPlatformType(detailedItem) != PlatformType.ULTIMA) {
      super.generate(item);
    }
    // Ultima library aliquot e.g. PROJ_0001_Pa_P_WG_1-1
    String partial = detailedItem.getLibrary().getAlias() + "-";
    int siblingNumber = getSiblingNumberGenerator().getFirstAvailableSiblingNumber(LibraryAliquot.class, partial);
    return partial + siblingNumber;
  }

  @Override
  protected boolean isDetailed(LibraryAliquot item) {
    return LimsUtils.isDetailedLibraryAliquot(item);
  }

  @Override
  protected PlatformType getPlatformType(DetailedLibraryAliquot item) {
    return item.getLibrary().getPlatformType();
  }

  @Override
  protected Sample getSample(DetailedLibraryAliquot item) {
    return item.getLibrary().getSample();
  }

  @Override
  protected LibraryType getLibraryType(DetailedLibraryAliquot item) {
    return item.getLibrary().getLibraryType();
  }

  @Override
  protected Integer getDnaSize(DetailedLibraryAliquot item) {
    return item.getDnaSize() != null ? item.getDnaSize() : item.getLibrary().getDnaSize();
  }

  @Override
  protected LibraryDesignCode getLibraryDesignCode(DetailedLibraryAliquot item) {
    return item.getLibraryDesignCode() != null ? item.getLibraryDesignCode()
        : ((DetailedLibrary) item.getLibrary()).getLibraryDesignCode();
  }

  @Override
  protected LocalDate getCreationDate(DetailedLibraryAliquot item) {
    return item.getCreationDate();
  }

}
