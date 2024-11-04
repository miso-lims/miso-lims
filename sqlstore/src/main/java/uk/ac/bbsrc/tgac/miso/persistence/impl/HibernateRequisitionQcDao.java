package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.RequisitionQC;
import uk.ac.bbsrc.tgac.miso.persistence.RequisitionQcStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRequisitionQcDao extends HibernateQcStore<RequisitionQC> implements RequisitionQcStore {

  public HibernateRequisitionQcDao() {
    super(Requisition.class, RequisitionQC.class);
  }

  @Override
  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
      throws IOException {
    Requisition requisition = (Requisition) currentSession().get(Requisition.class, id);
    requisition.updateFromQc(correspondingField, value, units);
    currentSession().merge(requisition);
  }

  @Override
  public String getIdProperty() {
    return Requisition_.REQUISITION_ID;
  }

}
