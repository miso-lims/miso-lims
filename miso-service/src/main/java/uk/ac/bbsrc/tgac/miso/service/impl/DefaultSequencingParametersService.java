package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingParametersService implements SequencingParametersService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSequencingParametersService.class);

  @Autowired
  private SequencingParametersDao sequencingParametersDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private InstrumentModelService instrumentModelService;

  @Override
  public long create(SequencingParameters sequencingParameters) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sequencingParameters.setCreatedBy(user);
    sequencingParameters.setUpdatedBy(user);
    return sequencingParametersDao.create(sequencingParameters);
  }

  @Override
  public SequencingParameters get(long sequencingParametersId) throws IOException {
    return sequencingParametersDao.get(sequencingParametersId);
  }

  @Override
  public List<SequencingParameters> list() throws IOException {
    return sequencingParametersDao.list();
  }

  @Override
  public long update(SequencingParameters sequencingParameters) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    sequencingParameters.setUpdatedBy(user);
    return sequencingParametersDao.update(sequencingParameters);
  }

  @Override
  public List<SequencingParameters> listByInstrumentModelId(long instrumentModelId) throws IOException {
    InstrumentModel model = instrumentModelService.get(instrumentModelId);
    return sequencingParametersDao.listByInstrumentModel(model);
  }

  public void setSequencingParametersDao(SequencingParametersDao sequencingParametersDao) {
    this.sequencingParametersDao = sequencingParametersDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

}
