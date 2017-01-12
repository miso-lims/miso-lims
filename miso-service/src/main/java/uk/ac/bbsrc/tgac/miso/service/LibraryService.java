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

public interface LibraryService {

  Library get(long libraryId) throws IOException;

  Library save(Library library) throws IOException;

  Long create(Library library) throws IOException;

  void update(Library library) throws IOException;

  boolean delete(Library library) throws IOException;

  int count() throws IOException;

  long countBySearch(String querystr) throws IOException;

  List<Library> getAll() throws IOException;

  List<Library> getAllByPageAndSize(int offset, int size, String sortDir, String sortCol) throws IOException;

  List<Library> getAllByPageSizeAndSearch(int offset, int size, String querystr, String sortDir, String sortCol) throws IOException;

  Library getAdjacentLibrary(long libraryId, boolean before) throws IOException;

  Library getByBarcode(String barcode) throws IOException;

  List<Library> getAllByBarcodeList(List<String> barcodeList) throws IOException;

  List<Library> getAllByIdList(List<Long> idList) throws IOException;

  Library getByPositionId(long positionId) throws IOException;

  List<Library> getAllBySearch(String querystr) throws IOException;

  List<Library> getAllByAlias(String alias) throws IOException;

  List<Library> getAllWithLimit(long limit) throws IOException;

  List<Library> searchByCreationDate(Date from, Date to) throws IOException;

  List<Library> getAllBySampleId(long sampleId) throws IOException;

  List<Library> getAllByProjectId(long projectId) throws IOException;

  Map<String, Integer> getLibraryColumnSizes() throws IOException;

  LibraryType getLibraryTypeById(long libraryTypeId) throws IOException;

  LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException;

  Collection<LibraryType> getAllLibraryTypes() throws IOException;

  Collection<LibraryType> getAllLibraryTypesByPlatform(PlatformType platformType) throws IOException;

  LibrarySelectionType getLibrarySelectionTypeById(Long librarySelectionTypeId) throws IOException;

  LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException;

  Collection<LibrarySelectionType> getAllLibrarySelectionTypes() throws IOException;

  LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException;

  LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException;

  Collection<LibraryStrategyType> getAllLibraryStrategyTypes() throws IOException;

  void addNote(Library library, Note note) throws IOException;

  void deleteNote(Library library, Long noteId) throws IOException;

  Collection<QcType> getAllLibraryQcTypes() throws IOException;

  void addQc(Library library, LibraryQC qc) throws IOException;

  void deleteQc(Library library, Long qcId) throws IOException;
}
