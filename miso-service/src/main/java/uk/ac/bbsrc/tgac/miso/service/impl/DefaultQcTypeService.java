package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.QualityControlTypeStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultQcTypeService implements QcTypeService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultQcTypeService.class);

  @Autowired
  private QualityControlTypeStore qcTypeStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public QcType get(long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return qcTypeStore.get(id);
  }

  @Override
  public void update(QcType qcType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    QcType updatedQcType = get(qcType.getId());
    updatedQcType.setName(qcType.getName());
    updatedQcType.setDescription(LimsUtils.isStringBlankOrNull(qcType.getDescription()) ? "" : qcType.getDescription());
    updatedQcType.setQcTarget(qcType.getQcTarget());
    updatedQcType.setUnits(LimsUtils.isStringBlankOrNull(qcType.getUnits()) ? "" : qcType.getUnits());
    updatedQcType.setPrecisionAfterDecimal(qcType.getPrecisionAfterDecimal());
    qcTypeStore.update(updatedQcType);
  }

  @Override
  public Long create(QcType qcType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    if (qcType.getDescription() == null) {
      qcType.setDescription("");
    }
    if (qcType.getUnits() == null) {
      qcType.setUnits("");
    }
    return qcTypeStore.create(qcType);
  }

  @Override
  public Collection<QcType> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return qcTypeStore.list();
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

}
