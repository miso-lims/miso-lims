package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType_;
import uk.ac.bbsrc.tgac.miso.persistence.QualityControlTypeStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateQcTypeDao extends HibernateSaveDao<QcType> implements QualityControlTypeStore {

  public HibernateQcTypeDao() {
    super(QcType.class);
  }

  @Override
  public List<QcType> listByNameAndTarget(String name, QcTarget target) throws IOException {
    QueryBuilder<QcType, QcType> builder = new QueryBuilder<>(currentSession(), QcType.class, QcType.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QcType_.name), name));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QcType_.qcTarget), target));
    return builder.getResultList();
  }

  @Override
  public long getUsage(QcType qcType) throws IOException {
    LongQueryBuilder<?> builder = getBuilderForQcTarget(qcType);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QC_.TYPE), qcType));
    return builder.getCount();
  }

  private LongQueryBuilder<?> getBuilderForQcTarget(QcType qcType) {
    switch (qcType.getQcTarget()) {
      case Container:
        return new LongQueryBuilder<>(currentSession(), ContainerQC.class);
      case Library:
        return new LongQueryBuilder<>(currentSession(), LibraryQC.class);
      case Pool:
        return new LongQueryBuilder<>(currentSession(), PoolQC.class);
      case Sample:
        return new LongQueryBuilder<>(currentSession(), SampleQC.class);
      default:
        throw new IllegalArgumentException(
            "Unhandled QC target: " + qcType.getQcTarget() == null ? "null" : qcType.getQcTarget().getLabel());
    }
  }

  @Override
  public QcControl getControl(long id) throws IOException {
    return (QcControl) currentSession().get(QcControl.class, id);
  }

  @Override
  public long createControl(QcControl control) throws IOException {
    currentSession().persist(control);
    return control.getId();
  }

  @Override
  public void deleteControl(QcControl control) throws IOException {
    currentSession().remove(control);
  }

  @Override
  public long getControlUsage(QcControl control) throws IOException {
    LongQueryBuilder<?> builder = getBuilderForTargetQcControlRun(control);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QcControlRun_.CONTROL), control));
    return builder.getCount();
  }

  @Override
  public long getKitUsage(QcType qcType, KitDescriptor kit) throws IOException {
    LongQueryBuilder<?> builder = getBuilderForQcTarget(qcType);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QC_.TYPE), qcType));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(QC_.KIT), kit));
    return builder.getCount();
  }

  private LongQueryBuilder<?> getBuilderForTargetQcControlRun(QcControl control) {
    switch (control.getQcType().getQcTarget()) {
      case Container:
        return new LongQueryBuilder<>(currentSession(), ContainerQcControlRun.class);
      case Library:
        return new LongQueryBuilder<>(currentSession(), LibraryQcControlRun.class);
      case Pool:
        return new LongQueryBuilder<>(currentSession(), PoolQcControlRun.class);
      case Sample:
        return new LongQueryBuilder<>(currentSession(), SampleQcControlRun.class);
      default:
        throw new IllegalArgumentException(
            "Unhandled QC target: " + control.getQcType().getQcTarget() == null ? "null"
                : control.getQcType().getQcTarget().getLabel());
    }
  }

}
