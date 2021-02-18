package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLogStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultChangeLogService implements ChangeLogService {

  @Autowired
  private ChangeLogStore changeLogDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Long create(ChangeLog changeLog) throws IOException {
    if (LimsUtils.isStringEmptyOrNull(changeLog.getSummary())) {
      throw new IllegalArgumentException("Changelog summary cannot be empty");
    }
    if (changeLog.getTime() == null) {
      changeLog.setTime(new Date());
    }
    if (changeLog.getUser() == null) {
      changeLog.setUser(authorizationManager.getCurrentUser());
    }
    return changeLogDao.create(changeLog);
  }

  public void setChangeLogDao(ChangeLogStore changeLogDao) {
    this.changeLogDao = changeLogDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

}
