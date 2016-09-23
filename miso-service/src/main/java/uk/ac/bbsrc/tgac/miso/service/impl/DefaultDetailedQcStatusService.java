package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultDetailedQcStatusService implements DetailedQcStatusService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultDetailedQcStatusService.class);

  @Autowired
  private DetailedQcStatusDao detailedQcStatusDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public DetailedQcStatus get(Long detailedQcStatus) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return detailedQcStatusDao.getDetailedQcStatus(detailedQcStatus);
  }

  @Override
  public Long create(DetailedQcStatus detailedQcStatus) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    detailedQcStatus.setCreatedBy(user);
    detailedQcStatus.setUpdatedBy(user);
    return detailedQcStatusDao.addDetailedQcStatus(detailedQcStatus);
  }

  @Override
  public void update(DetailedQcStatus detailedQcStatus) throws IOException {
    authorizationManager.throwIfNonAdmin();
    DetailedQcStatus updatedDetailedQcStatus = get(detailedQcStatus.getId());
    updatedDetailedQcStatus.setStatus(detailedQcStatus.getStatus());
    updatedDetailedQcStatus.setDescription(detailedQcStatus.getDescription());
    updatedDetailedQcStatus.setNoteRequired(detailedQcStatus.getNoteRequired());
    User user = authorizationManager.getCurrentUser();
    updatedDetailedQcStatus.setUpdatedBy(user);
    detailedQcStatusDao.update(updatedDetailedQcStatus);
  }

  @Override
  public Set<DetailedQcStatus> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(detailedQcStatusDao.getDetailedQcStatus());
  }

  @Override
  public void delete(Long detailedQcStatusId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    DetailedQcStatus detailedQcStatus = get(detailedQcStatusId);
    detailedQcStatusDao.deleteDetailedQcStatus(detailedQcStatus);
  }

}
