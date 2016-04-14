package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;

public interface LibraryAdditionalInfoService {
  
  /**
   * Retrieves a single LibraryAdditionalInfo by ID
   * 
   * @param id ID of the LibraryAdditionalInfo record to retrieve
   * @return the LibraryAdditionalInfo with the specified ID
   * @throws IOException
   */
  LibraryAdditionalInfo get(Long id) throws IOException;
  
  /**
   * Saves a new LibraryAdditionalInfo
   * 
   * @param libraryAdditionalInfo the new LibraryAdditionalInfo to save
   * @param libraryId ID of the library that this libraryAdditionalInfo belongs to
   * @return the ID of the newly-created LibraryAdditionalInfo
   * @throws IOException
   */
  Long create(LibraryAdditionalInfo libraryAdditionalInfo, Long libraryId) throws IOException;
  
  /**
   * Saves a modified LibraryAdditionalInfo
   * 
   * @param libraryAdditionalInfo the modified LibraryAdditionalInfo to save
   * @throws IOException
   */
  void update(LibraryAdditionalInfo libraryAdditionalInfo) throws IOException;
  
  /**
   * @return a Set containing all LibraryAdditionalInfo records
   * @throws IOException
   */
  Set<LibraryAdditionalInfo> getAll() throws IOException;
  
  /**
   * Remove a LibraryAdditionalInfo
   * 
   * @param libraryAdditionalInfoId ID of the LibraryAdditionalInfo to delete
   * @throws IOException
   */
  void delete(Long libraryAdditionalInfoId) throws IOException;
  
}
