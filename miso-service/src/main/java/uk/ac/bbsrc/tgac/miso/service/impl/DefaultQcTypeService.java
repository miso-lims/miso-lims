package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.QualityControlTypeStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultQcTypeService implements QcTypeService {

  @Autowired
  private QualityControlTypeStore qcTypeStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

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
    updatedQcType.setCorrespondingField(qcType.getCorrespondingField());
    updatedQcType.setAutoUpdateField(qcType.isAutoUpdateField());
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
    return qcTypeStore.list();
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public ValidationResult validateDeletion(QcType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = qcTypeStore.getUsage(object);
    if (usage > 0) {
      switch (object.getQcTarget()) {
      case Container:
        result.addError(ValidationError.forDeletionUsage(object, usage, "sequencing " + Pluralizer.containers(usage)));
        break;
      case Library:
        result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
        break;
      case Pool:
        result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.pools(usage)));
        break;
      case Run:
        result.addError(ValidationError.forDeletionUsage(object, usage, "sequencer " + Pluralizer.runs(usage)));
        break;
      case Sample:
        result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
        break;
      default:
        result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.items(usage)));
        break;
      }
    }
    return result;
  }

}
