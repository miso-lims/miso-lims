package uk.ac.bbsrc.tgac.miso.service;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;

public interface TemplateService {

  List<LibraryTemplate> listLibraryTemplatesForProject(long projectId);

}
