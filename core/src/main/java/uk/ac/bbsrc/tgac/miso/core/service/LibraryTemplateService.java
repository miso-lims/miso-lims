package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryTemplateService extends PaginatedDataSource<LibraryTemplate>, DeleterService<LibraryTemplate> {

  Long create(LibraryTemplate libraryTemplate) throws IOException;

  void update(LibraryTemplate libraryTemplate) throws IOException;

  List<LibraryTemplate> list() throws IOException;

  List<LibraryTemplate> listLibraryTemplatesForProject(long projectId) throws IOException;

  List<LibraryTemplate> listByIdList(List<Long> ids) throws IOException;

}
