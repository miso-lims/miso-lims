package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultChangeLogService implements ChangeLogService {

  @Autowired
  private ChangeLogStore changeLogDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Collection<ChangeLog> listAll(String type) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return changeLogDao.listAll(type);
  }

  @Override
  public Collection<ChangeLog> listAllById(String type, long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return changeLogDao.listAllById(type, id);
  }

  @Override
  public void deleteAllById(String type, long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    changeLogDao.deleteAllById(type, id);
  }

  @Override
  public Long create(ChangeLog changeLog) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return changeLogDao.create(changeLog);
  }

  public void setChangeLogDao(ChangeLogStore changeLogDao) {
    this.changeLogDao = changeLogDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

}
