package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface IndexedLibrary {

  LibraryIndex getIndex1();

  void setIndex1(LibraryIndex index1);

  LibraryIndex getIndex2();

  void setIndex2(LibraryIndex index2);

  PlatformType getPlatformType();

  void setPlatformType(PlatformType platformType);

}
