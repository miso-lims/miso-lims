package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
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
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private KitDescriptorService kitDescriptorService;

  @Override
  public QcType get(long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return qcTypeStore.get(id);
  }

  @Override
  public long update(QcType qcType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(qcType);
    QcType managed = get(qcType.getId());
    validateChange(qcType, managed);
    applyChanges(managed, qcType);
    qcTypeStore.update(managed);
    saveControls(managed.getId(), qcType);
    return managed.getId();
  }

  @Override
  public long create(QcType qcType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(qcType);
    validateChange(qcType, null);
    long savedId = qcTypeStore.create(qcType);
    saveControls(savedId, qcType);
    return savedId;
  }

  private void loadChildEntities(QcType qcType) throws IOException {
    if (qcType.getInstrumentModel() != null) {
      qcType.setInstrumentModel(instrumentModelService.get(qcType.getInstrumentModel().getId()));
    }

    Set<KitDescriptor> managedKits = new HashSet<>();
    for (KitDescriptor kit : qcType.getKitDescriptors()) {
      KitDescriptor managedKit = kitDescriptorService.get(kit.getId());
      if (managedKit == null) {
        throw new ValidationException("No kit descriptor found with ID: " + kit.getId());
      }
      managedKits.add(managedKit);
    }
    qcType.getKitDescriptors().clear();
    qcType.getKitDescriptors().addAll(managedKits);

    Set<QcControl> managedControls = new HashSet<>();
    for (QcControl control : qcType.getControls()) {
      if (control.isSaved()) {
        QcControl managedControl = qcTypeStore.getControl(control.getId());
        if (managedControl == null) {
          throw new ValidationException("No control found with ID: " + control.getId());
        }
        managedControls.add(managedControl);
      } else {
        managedControls.add(control);
      }
    }
    qcType.getControls().clear();
    qcType.getControls().addAll(managedControls);
  }

  private void applyChanges(QcType to, QcType from) {
    to.setName(from.getName());
    to.setDescription(LimsUtils.isStringBlankOrNull(from.getDescription()) ? "" : from.getDescription());
    to.setQcTarget(from.getQcTarget());
    to.setUnits(from.getUnits());
    to.setPrecisionAfterDecimal(from.getPrecisionAfterDecimal());
    to.setCorrespondingField(from.getCorrespondingField());
    to.setAutoUpdateField(from.isAutoUpdateField());
    to.setInstrumentModel(from.getInstrumentModel());
    to.setArchived(from.isArchived());
    applySetChanges(to.getKitDescriptors(), from.getKitDescriptors());
  }

  private void validateChange(QcType qcType, QcType beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (beforeChange != null) {
      long usage = qcTypeStore.getUsage(beforeChange);
      if (usage > 1L) {
        if (isChanged(QcType::getInstrumentModel, qcType, beforeChange)) {
          errors.add(
              new ValidationError("instrumentModelId", "Cannot change because there are already QCs of this type"));
        }
        if (!qcType.getKitDescriptors().isEmpty() && beforeChange.getKitDescriptors().isEmpty()) {
          errors
              .add(new ValidationError("kitDescriptors", "Cannot add kits because there are already QCs of this type"));
        }
        if (!qcType.getControls().isEmpty() && beforeChange.getControls().isEmpty()) {
          errors.add(new ValidationError("controls", "Cannot add controls because there are already QCs of this type"));
        }
        if (qcType.getCorrespondingField() == null) {
          errors.add(ValidationError.forRequired("correspondingField"));
        } else if (qcType.isAutoUpdateField() && qcType.getCorrespondingField() == QcCorrespondingField.NONE) {
          errors.add(new ValidationError("autoUpdateField", "Cannot auto-update with no corresponding field selected"));
        }
      }

      Set<QcControl> toDelete = getControlsToDelete(qcType, beforeChange);
      for (QcControl control : toDelete) {
        long controlUsage = qcTypeStore.getControlUsage(control);
        if (controlUsage > 0L) {
          throw new ValidationException(
              String.format("Cannot remove control '%s' because it is used in %d %s", control.getAlias(), controlUsage,
                  Pluralizer.qcs(controlUsage)));
        }
      }

      Set<KitDescriptor> kitsToRemove = beforeChange.getKitDescriptors().stream()
          .filter(beforeKit -> qcType.getKitDescriptors().stream().anyMatch(kit -> kit.getId() == beforeKit.getId()))
          .collect(Collectors.toSet());
      for (KitDescriptor kit : kitsToRemove) {
        long kitUsage = qcTypeStore.getKitUsage(qcType, kit);
        if (kitUsage > 0L) {
          errors.add(new ValidationError("kitDescriptors",
              String.format("Cannot remove kit '%s' because it is used in %d %s", kit.getName(), kitUsage,
                  Pluralizer.qcs(kitUsage))));
        }
      }
    }

    if (isSetAndChanged(QcType::getName, qcType, beforeChange)
        && qcTypeStore.getByNameAndTarget(qcType.getName(), qcType.getQcTarget()) != null) {
      errors.add(ValidationError.forDuplicate(qcType.getQcTarget().getLabel() + " QC type", "name"));
    }

    for (KitDescriptor kit : qcType.getKitDescriptors()) {
      if (kit.getKitType() != KitType.QC) {
        errors.add(new ValidationError("kitDescriptorId", "Must be a QC kit"));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void saveControls(long savedId, QcType from) throws IOException {
    QcType to = get(savedId);
    Set<QcControl> toDelete = getControlsToDelete(from, to);
    for (QcControl control : toDelete) {
      qcTypeStore.deleteControl(control);
    }

    for (QcControl fromControl : from.getControls()) {
      if (!fromControl.isSaved()) {
        fromControl.setQcType(to);
        qcTypeStore.createControl(fromControl);
      }
    }
  }

  private Set<QcControl> getControlsToDelete(QcType qcType, QcType beforeChange) {
    return beforeChange.getControls().stream()
        .filter(toControl -> qcType.getControls().stream()
            .noneMatch(fromControl -> fromControl.getId() == toControl.getId()))
        .collect(Collectors.toSet());
  }

  @Override
  public List<QcType> list() throws IOException {
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
          result
              .addError(ValidationError.forDeletionUsage(object, usage, "sequencing " + Pluralizer.containers(usage)));
          break;
        case Library:
          result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
          break;
        case Pool:
          result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.pools(usage)));
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
