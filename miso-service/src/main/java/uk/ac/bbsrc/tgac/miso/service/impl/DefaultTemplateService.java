package uk.ac.bbsrc.tgac.miso.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.store.TemplateStore;
import uk.ac.bbsrc.tgac.miso.service.TemplateService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTemplateService implements TemplateService {

  @Autowired
  private TemplateStore templateStore;

  public void setTemplateStore(TemplateStore templateStore) {
    this.templateStore = templateStore;
  }

  @Override
  public List<LibraryTemplate> listLibraryTemplatesForProject(long projectId) {
    return templateStore.listLibraryTemplatesForProject(projectId);
  }

}
