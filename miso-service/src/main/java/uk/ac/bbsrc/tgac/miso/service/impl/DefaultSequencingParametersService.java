package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingParametersService implements SequencingParametersService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSequencingParametersService.class);

  @Autowired
  private SequencingParametersDao sequencingParametersDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Long create(SequencingParameters sequencingParameters) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sequencingParameters.setCreatedBy(user);
    sequencingParameters.setUpdatedBy(user);
    return sequencingParametersDao.addSequencingParameters(sequencingParameters);
  }

  @Override
  public SequencingParameters get(Long sequencingParametersId) throws IOException {
    return sequencingParametersDao.getSequencingParameters(sequencingParametersId);
  }

  @Override
  public Collection<SequencingParameters> getAll() throws IOException {
    return sequencingParametersDao.getSequencingParameters();
  }

  @Override
  public void update(SequencingParameters sequencingParameters) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sequencingParameters.setUpdatedBy(user);
    sequencingParametersDao.update(sequencingParameters);
  }

  @Override
  public Collection<SequencingParameters> getForInstrumentModel(Long instrumentModelId) throws IOException {
    if (instrumentModelId == null) {
      return Collections.emptyList();
    }
    Collection<SequencingParameters> results = new ArrayList<>();
    for (SequencingParameters sp : sequencingParametersDao.getSequencingParameters()) {
      if (sp.getInstrumentModel().getId() == instrumentModelId) {
        results.add(sp);
      }
    }
    return results;
  }

  public void setSequencingParametersDao(SequencingParametersDao sequencingParametersDao) {
    this.sequencingParametersDao = sequencingParametersDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

}
