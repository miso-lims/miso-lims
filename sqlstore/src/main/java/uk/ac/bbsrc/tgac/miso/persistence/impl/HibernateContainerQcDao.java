package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.persistence.ContainerQcStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateContainerQcDao extends HibernateQcStore<ContainerQC> implements ContainerQcStore {

  public HibernateContainerQcDao() {
    super(SequencerPartitionContainerImpl.class, ContainerQC.class);
  }

  @Override
  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
      throws IOException {
    SequencerPartitionContainer container =
        (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, id);
    correspondingField.updateField(container, value, units);
    currentSession().merge(container);
  }

  @Override
  public String getIdProperty() {
    return SequencerPartitionContainerImpl_.CONTAINER_ID;
  }

}
