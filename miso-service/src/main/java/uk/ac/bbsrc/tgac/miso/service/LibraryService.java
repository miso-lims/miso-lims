package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryService extends PaginatedDataSource<Library> {

  Library get(long libraryId) throws IOException;

  Long create(Library library) throws IOException;

  void update(Library library) throws IOException;

  boolean delete(Library library) throws IOException;

  int count() throws IOException;

  long countBySearch(String querystr) throws IOException;

  List<Library> list() throws IOException;

  Library getAdjacentLibrary(long libraryId, boolean before) throws IOException;

  Library getByBarcode(String barcode) throws IOException;

  List<Library> listByBarcodeList(List<String> barcodeList) throws IOException;

  List<Library> listByIdList(List<Long> idList) throws IOException;

  Library getByPositionId(long positionId) throws IOException;

  List<Library> listBySearch(String querystr) throws IOException;

  List<Library> listByAlias(String alias) throws IOException;

  List<Library> listWithLimit(long limit) throws IOException;

  List<Library> searchByCreationDate(Date from, Date to) throws IOException;

  List<Library> listBySampleId(long sampleId) throws IOException;

  List<Library> listByProjectId(long projectId) throws IOException;

  Map<String, Integer> getLibraryColumnSizes() throws IOException;

  LibraryType getLibraryTypeById(long libraryTypeId) throws IOException;

  LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException;

  Collection<LibraryType> listLibraryTypes() throws IOException;

  Collection<LibraryType> listLibraryTypesByPlatform(PlatformType platformType) throws IOException;

  LibrarySelectionType getLibrarySelectionTypeById(Long librarySelectionTypeId) throws IOException;

  LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException;

  Collection<LibrarySelectionType> listLibrarySelectionTypes() throws IOException;

  LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException;

  LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException;

  Collection<LibraryStrategyType> listLibraryStrategyTypes() throws IOException;

  void addNote(Library library, Note note) throws IOException;

  void deleteNote(Library library, Long noteId) throws IOException;

  Collection<QcType> listLibraryQcTypes() throws IOException;

  void addQc(Library library, LibraryQC qc) throws IOException;

  void deleteQc(Library library, Long qcId) throws IOException;

}
