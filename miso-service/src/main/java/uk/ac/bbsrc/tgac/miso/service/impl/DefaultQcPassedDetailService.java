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

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.service.QcPassedDetailService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultQcPassedDetailService implements QcPassedDetailService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultQcPassedDetailService.class);

  @Autowired
  private QcPassedDetailDao qcPassedDetailDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public QcPassedDetail get(Long qcPassedDetailId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return qcPassedDetailDao.getQcPassedDetails(qcPassedDetailId);
  }

  @Override
  public Long create(QcPassedDetail qcPassedDetail) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    qcPassedDetail.setCreatedBy(user);
    qcPassedDetail.setUpdatedBy(user);
    return qcPassedDetailDao.addQcPassedDetails(qcPassedDetail);
  }

  @Override
  public void update(QcPassedDetail qcPassedDetail) throws IOException {
    authorizationManager.throwIfNonAdmin();
    QcPassedDetail updatedQcPassedDetails = get(qcPassedDetail.getId());
    updatedQcPassedDetails.setStatus(qcPassedDetail.getStatus());
    updatedQcPassedDetails.setDescription(qcPassedDetail.getDescription());
    updatedQcPassedDetails.setNoteRequired(qcPassedDetail.getNoteRequired());
    User user = authorizationManager.getCurrentUser();
    updatedQcPassedDetails.setUpdatedBy(user);
    qcPassedDetailDao.update(updatedQcPassedDetails);
  }

  @Override
  public Set<QcPassedDetail> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(qcPassedDetailDao.getQcPassedDetails());
  }

  @Override
  public void delete(Long qcPassedDetailId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    QcPassedDetail qcPassedDetail = get(qcPassedDetailId);
    qcPassedDetailDao.deleteQcPassedDetails(qcPassedDetail);
  }

}
