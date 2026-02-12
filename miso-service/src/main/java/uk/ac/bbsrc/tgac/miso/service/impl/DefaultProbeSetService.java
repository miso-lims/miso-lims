package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSetProbe;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProbeSetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.ProbeSetDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultProbeSetService extends AbstractSaveService<ProbeSet> implements ProbeSetService {

  @Autowired
  private ProbeSetDao probeSetDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<ProbeSet> getDao() {
    return probeSetDao;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<ProbeSet> listByIdList(List<Long> ids) throws IOException {
    return probeSetDao.listByIdList(ids);
  }

  @Override
  public List<ProbeSet> list() throws IOException {
    return probeSetDao.list();
  }

  @Override
  public List<ProbeSet> searchByName(String name) throws IOException {
    return probeSetDao.searchByName(name);
  }

  @Override
  protected void collectValidationErrors(ProbeSet object, ProbeSet beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(ProbeSet::getName, object, beforeChange)
        && probeSetDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("probe set", "name"));
    }
    if (object.getProbes() != null && !object.getProbes().isEmpty()) {
      ValidationUtils.validateProbes(object.getProbes(), errors);
    }
  }

  @Override
  protected void applyChanges(ProbeSet to, ProbeSet from) throws IOException {
    to.setName(from.getName());

    if (to.getProbes() == null) {
      to.setProbes(new HashSet<>());
    }
    if (from.getProbes() == null || from.getProbes().isEmpty()) {
      to.getProbes().clear();
      return;
    }
    to.getProbes().removeIf(toProbe -> from.getProbes().stream()
        .noneMatch(fromProbe -> fromProbe.getId() == toProbe.getId()));
    for (ProbeSetProbe fromProbe : from.getProbes()) {
      if (fromProbe.isSaved()) {
        // Update existing probe
        ProbeSetProbe toProbe = to.getProbes().stream()
            .filter(probe -> probe.getId() == fromProbe.getId())
            .findFirst()
            .orElseThrow(() -> new ValidationException(new ValidationError("probes",
                "Probe ID %d belongs to a different probe set".formatted(fromProbe.getId()))));
        applyProbeChanges(toProbe, fromProbe);
      } else {
        // Add new probe
        to.getProbes().add(fromProbe);
      }
    }
  }

  private void applyProbeChanges(ProbeSetProbe to, ProbeSetProbe from) {
    to.setIdentifier(from.getIdentifier());
    to.setName(from.getName());
    to.setPattern(from.getPattern());
    to.setRead(from.getRead());
    to.setSequence(from.getSequence());
    to.setFeatureType(from.getFeatureType());
    to.setTargetGeneId(from.getTargetGeneId());
    to.setTargetGeneName(from.getTargetGeneName());
  }

  @Override
  public void authorizeDeletion(ProbeSet object) throws IOException {
    // no authorization necessary - any user can delete
  }
}
