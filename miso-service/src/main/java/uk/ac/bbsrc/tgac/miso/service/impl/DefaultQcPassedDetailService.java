package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.service.QcPassedDetailService;

@Transactional
@Service
public class DefaultQcPassedDetailService implements QcPassedDetailService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultQcPassedDetailService.class);

  @Autowired
  private QcPassedDetailDao qcPassedDetailDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public QcPassedDetail get(Long qcPassedDetailId) {
    return qcPassedDetailDao.getQcPassedDetails(qcPassedDetailId);
  }

  @Override
  public Long create(QcPassedDetail qcPassedDetail) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    qcPassedDetail.setCreatedBy(user);
    qcPassedDetail.setUpdatedBy(user);
    return qcPassedDetailDao.addQcPassedDetails(qcPassedDetail);
  }

  @Override
  public void update(QcPassedDetail qcPassedDetail) throws IOException {
    QcPassedDetail updatedQcPassedDetails = get(qcPassedDetail.getQcPassedDetailId());
    updatedQcPassedDetails.setStatus(qcPassedDetail.getStatus());
    updatedQcPassedDetails.setDescription(qcPassedDetail.getDescription());
    updatedQcPassedDetails.setNoteRequired(qcPassedDetail.getNoteRequired());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedQcPassedDetails.setUpdatedBy(user);
    qcPassedDetailDao.update(updatedQcPassedDetails);
  }

  @Override
  public Set<QcPassedDetail> getAll() {
    return Sets.newHashSet(qcPassedDetailDao.getQcPassedDetails());
  }

  @Override
  public void delete(Long qcPassedDetailId) {
    QcPassedDetail qcPassedDetail = get(qcPassedDetailId);
    qcPassedDetailDao.deleteQcPassedDetails(qcPassedDetail);
  }

}
