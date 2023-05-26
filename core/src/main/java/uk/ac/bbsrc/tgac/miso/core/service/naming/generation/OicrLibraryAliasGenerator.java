package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.time.LocalDate;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class OicrLibraryAliasGenerator extends OicrBaseLibraryAliasGenerator<Library, DetailedLibrary> {

  @Override
  protected boolean isDetailed(Library item) {
    return LimsUtils.isDetailedLibrary(item);
  }

  @Override
  protected PlatformType getPlatformType(DetailedLibrary item) {
    return item.getPlatformType();
  }

  @Override
  protected Sample getSample(DetailedLibrary item) {
    return item.getSample();
  }

  @Override
  protected LibraryType getLibraryType(DetailedLibrary item) {
    return item.getLibraryType();
  }

  @Override
  protected Integer getDnaSize(DetailedLibrary item) {
    return item.getDnaSize();
  }

  @Override
  protected LibraryDesignCode getLibraryDesignCode(DetailedLibrary item) {
    return item.getLibraryDesignCode();
  }

  @Override
  protected LocalDate getCreationDate(DetailedLibrary item) {
    return item.getCreationDate();
  }

}
