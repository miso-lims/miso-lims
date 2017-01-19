package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;

public interface LibraryAdditionalInfoDao {

  /**
   * @return a List of all LibraryAdditionalInfo records
   * @throws IOException
   */
  List<LibraryAdditionalInfo> getLibraryAdditionalInfo() throws IOException;

  /**
   * Retrieves a single LibraryAdditionalInfo by ID
   *
   * @param id ID of the LibraryAdditionalInfo to retrieve
   * @throws IOException
   */
  LibraryAdditionalInfo getLibraryAdditionalInfo(Long id) throws IOException;

  /**
   * Retrieves a single LibraryAdditionalInfo by Library
   *
   * @param id ID of Library to find LibraryAdditionalInfo for
   * @throws IOException
   */
  LibraryAdditionalInfo getLibraryAdditionalInfoByLibraryId(Long id) throws IOException;

  /**
   * Saves a new LibraryAdditionalInfo
   *
   * @param libraryAdditionalInfo the new LibraryAdditionalInfo to save
   * @return the ID of the newly-created LibraryAdditionalInfo
   */
  Long addLibraryAdditionalInfo(LibraryAdditionalInfo libraryAdditionalInfo);

  /**
   * Deletes an existing LibraryAdditionalInfo
   *
   * @param LibraryAdditionalInfo the LibraryAdditionalInfo to delete
   */
  void deleteLibraryAdditionalInfo(LibraryAdditionalInfo LibraryAdditionalInfo);

  /**
   * Saves a modified LibraryAdditionalInfo
   *
   * @param libraryAdditionalInfo the modified LibraryAdditionalInfo to save
   */
  void update(LibraryAdditionalInfo libraryAdditionalInfo);

  void setKitDescriptorStore(KitDescriptorStore kitDescriptorStore);

  KitDescriptorStore getKitDescriptorStore();
}
