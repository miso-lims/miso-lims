package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleSheetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleSheetDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleSheetService extends AbstractSaveService<SampleSheet> implements SampleSheetService {

  private static final String TYPE_LABEL = "sample sheet";

  @Autowired
  private SampleSheetDao sampleSheetDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<SampleSheet> list() throws IOException {
    return sampleSheetDao.list();
  }

  @Override
  public SaveDao<SampleSheet> getDao() {
    return sampleSheetDao;
  }

  @Override
  protected void collectValidationErrors(SampleSheet object, SampleSheet beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(SampleSheet::getName, object, beforeChange)
        && sampleSheetDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate(TYPE_LABEL, "name"));
    }
    if (object.getSections() == null || object.getSections().isEmpty()) {
      errors.add(new ValidationError("sections", "A sample sheet must contain at least one section"));
    }
    // TODO: more validation (when an editor is added)
  }

  @Override
  protected void applyChanges(SampleSheet to, SampleSheet from) throws IOException {
    // There's no changelog or bulk entry, and sample sheets will rarely be modified, so no need to
    // further optimize this
    to.setName(from.getName());
    to.setPlatformType(from.getPlatformType());
    to.setParameters(from.getParameters());
    to.setSections(from.getSections());
  }

  @Override
  public List<SampleSheet> listByPlatform(PlatformType platform) throws IOException {
    return sampleSheetDao.listByPlatform(platform);
  }

}
