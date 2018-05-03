package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;

public interface TemplateStore {

  List<LibraryTemplate> listLibraryTemplatesForProject(long projectId);

}
