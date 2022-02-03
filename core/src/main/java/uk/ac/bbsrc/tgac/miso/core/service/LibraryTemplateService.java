package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryTemplateService extends BulkSaveService<LibraryTemplate>, PaginatedDataSource<LibraryTemplate>,
    DeleterService<LibraryTemplate>, ListService<LibraryTemplate> {

  List<LibraryTemplate> listByProject(long projectId) throws IOException;

}
