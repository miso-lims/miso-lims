package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface LibraryDilutionService {

  LibraryDilution get(long dilutionId) throws IOException;

  Long create(LibraryDilution dilution) throws IOException;

  void update(LibraryDilution dilution) throws IOException;

  int count() throws IOException;

  int countByPlatform(PlatformType platform) throws IOException;

  int countBySearchAndPlatform(String querystr, PlatformType platform) throws IOException;

  List<LibraryDilution> getAllBySearchAndPlatform(String querystr, PlatformType platform) throws IOException;

  List<LibraryDilution> getAllBySearch(String querystr) throws IOException;

  List<LibraryDilution> getAllByLibraryId(Long libraryId) throws IOException;

  List<LibraryDilution> getAll() throws IOException;

  List<LibraryDilution> getAllWithLimit(long limit) throws IOException;

  List<LibraryDilution> getAllByPlatform(PlatformType platform) throws IOException;

  List<LibraryDilution> getAllByProjectId(Long projectId) throws IOException;

  List<LibraryDilution> getAllByProjectIdAndPlatform(Long projectId, PlatformType platform) throws IOException;

  LibraryDilution getByBarcode(String barcode) throws IOException;

  List<LibraryDilution> getAllByPageSizeSearchAndPlatform(int offset, int size, String querystr, PlatformType platform, String sortDir,
      String sortCol) throws IOException;

  boolean delete(LibraryDilution dilution) throws IOException;

}
