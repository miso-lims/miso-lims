package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface LibraryTypeService extends BulkSaveService<LibraryType>, DeleterService<LibraryType>,
    ListService<LibraryType> {

  public List<LibraryType> listByPlatform(PlatformType platform) throws IOException;

}
