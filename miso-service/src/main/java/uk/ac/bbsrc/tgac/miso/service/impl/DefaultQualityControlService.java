package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLoggableStore;
import uk.ac.bbsrc.tgac.miso.persistence.ContainerQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.persistence.QcTargetStore;
import uk.ac.bbsrc.tgac.miso.persistence.QualityControlTypeStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleQcStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultQualityControlService implements QualityControlService {
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private LibraryQcStore libraryQcStore;
  @Autowired
  private PoolQcStore poolQcStore;
  @Autowired
  private ContainerQcStore containerQcStore;
  @Autowired
  private QcTypeService qcTypeService;
  @Autowired
  private QualityControlTypeStore qcTypeStore;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private ChangeLoggableStore changeLoggableStore;
  @Autowired
  private InstrumentService instrumentService;

  @Override
  public QC createQC(QC qc) throws IOException {
    QcTargetStore handler = getHandler(qc.getType().getQcTarget());

    QualityControlEntity entity = handler.getEntity(qc.getEntity().getId());
    loadChildEntities(qc);

    User user = authorizationManager.getCurrentUser();
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(qc.getCreationTime());

    if (!qc.getType().getQcTarget().equals(entity.getQcTarget())) {
      throw new IllegalArgumentException("QC type and entity are mismatched.");
    }

    if (qc.getType().isAutoUpdateField()) {
      handler.updateEntity(qc.getEntity().getId(), qc.getType().getCorrespondingField(), qc.getResults(), qc.getType().getUnits());
    }

    entity.setChangeDetails(user);
    changeLoggableStore.update(entity);

    long id = handler.save(qc);
    saveControlRuns(id, qc, handler);
    return handler.get(id);
  }

  @Override
  public QC updateQc(QC qc) throws IOException {
    QcTargetStore handler = getHandler(qc.getType().getQcTarget());

    QualityControlEntity entity = handler.getEntity(qc.getEntity().getId());
    User user = authorizationManager.getCurrentUser();

    QC managed = handler.get(qc.getId());
    if (managed.getType().getId() != qc.getType().getId()) {
      throw new IllegalArgumentException("QC type has changed");
    }
    loadChildEntities(qc);
    validateChange(qc, managed);
    applyChanges(managed, qc);

    entity.setChangeDetails(user);
    changeLoggableStore.update(entity);

    handler.save(managed);
    saveControlRuns(managed.getId(), qc, handler);
    return managed;
  }

  private void saveControlRuns(long savedId, QC from, QcTargetStore handler) throws IOException {
    QC to = get(from.getEntity().getQcTarget(), from.getId());
    List<QcControlRun> toDelete = new ArrayList<>();
    for (QcControlRun toControl : to.getControls()) {
      if (!toControl.isSaved()) {
        // newly created QC
        continue;
      }
      QcControlRun fromControl = from.getControls().stream()
          .filter(fc -> fc.getId() == toControl.getId())
          .findFirst().orElse(null);
      if (fromControl == null) {
        toDelete.add(toControl);
      } else {
        toControl.setControl(fromControl.getControl());
        toControl.setLot(fromControl.getLot());
        toControl.setQcPassed(fromControl.isQcPassed());
        handler.updateControlRun(toControl);
      }
    }

    for (QcControlRun control : toDelete) {
      handler.deleteControlRun(control);
    }

    for (QcControlRun fromControl : from.getControls()) {
      if (!fromControl.isSaved()) {
        switch (to.getType().getQcTarget()) {
        case Container:
          ((ContainerQcControlRun) fromControl).setQc((ContainerQC) to);
          break;
        case Library:
          ((LibraryQcControlRun) fromControl).setQc((LibraryQC) to);
          break;
        case Pool:
          ((PoolQcControlRun) fromControl).setQc((PoolQC) to);
          break;
        case Sample:
          ((SampleQcControlRun) fromControl).setQc((SampleQC) to);
          break;
        default:
          throw new IllegalArgumentException("Unhandled QC target: " + to.getType().getQcTarget());
        }
        handler.createControlRun(fromControl);
      }
    }
  }

  private void loadChildEntities(QC qc) throws IOException {
    ValidationUtils.loadChildEntity(qc::setType, qc.getType(), qcTypeService, "typeName");
    ValidationUtils.loadChildEntity(qc::setInstrument, qc.getInstrument(), instrumentService, "instrument");
  }

  private void applyChanges(QC to, QC from) {
    to.setResults(from.getResults());
    to.setLastModified(new Date());
    to.setInstrument(from.getInstrument());
    to.setKitLot(from.getKitLot());
  }

  private void validateChange(QC qc, QC beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (beforeChange != null && ValidationUtils.isSetAndChanged(QC::getType, qc, beforeChange)) {
      errors.add(new ValidationError("typeName", "Cannot be changed"));
    }

    if (qc.getType().getInstrumentModel() != null) {
      if (qc.getInstrument() == null) {
        errors.add(new ValidationError("instrument", "Must be specified"));
      } else if (qc.getInstrument().getInstrumentModel().getId() != qc.getType().getInstrumentModel().getId()) {
        errors.add(new ValidationError("instrument", "Invalid instrument model"));
      }
    }
    if (qc.getType().getKitDescriptor() != null && qc.getKitLot() == null) {
      errors.add(new ValidationError("kitLot", "Must be specified"));
    }

    for (QcControlRun controlRun : qc.getControls()) {
      if (qc.getType().getControls().stream().noneMatch(control -> control.getId() == controlRun.getControl().getId())) {
        errors.add(new ValidationError("controlId", "Invalid control for this QC type"));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  @Override
  public QC get(QcTarget target, Long id) throws IOException {
    return getHandler(target).get(id);
  }

  @Override
  public QualityControlEntity getEntity(QcTarget target, long ownerId) throws IOException {
    return getHandler(target).getEntity(ownerId);
  }

  private QcTargetStore getHandler(QcTarget target) {
    switch (target) {
    case Library:
      return libraryQcStore;
    case Pool:
      return poolQcStore;
    case Sample:
      return sampleQcStore;
    case Container:
      return containerQcStore;
    default:
      throw new IllegalArgumentException("Unknown QC target: " + target);
    }
  }

  @Override
  public Collection<? extends QC> listQCsFor(QcTarget target, long ownerId) throws IOException {
    Collection<? extends QC> unfiltered = getHandler(target).listForEntity(ownerId);
    return unfiltered;
  }

  @Override
  public Collection<QcType> listQcTypes() throws IOException {
    return qcTypeStore.list();
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setLibraryQcStore(LibraryQcStore libraryQcStore) {
    this.libraryQcStore = libraryQcStore;
  }

  public void setPoolQcStore(PoolQcStore poolQcStore) {
    this.poolQcStore = poolQcStore;
  }

  public void setContainerQcStore(ContainerQcStore containerQcStore) {
    this.containerQcStore = containerQcStore;
  }

  public void setQcTypeStore(QualityControlTypeStore qcTypeStore) {
    this.qcTypeStore = qcTypeStore;
  }

  public void setSampleQcStore(SampleQcStore sampleQcStore) {
    this.sampleQcStore = sampleQcStore;
  }

  @Override
  public QC save(QC qc) throws IOException {
    return createQC(qc);
  }

}
