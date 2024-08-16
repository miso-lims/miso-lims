package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

@Entity
@Table(name = "RequisitionQc")
public class RequisitionQC extends QC {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = Requisition.class)
  @JoinColumn(name = "requisitionId")
  private Requisition requisition;

  @OneToMany(mappedBy = "qc", cascade = CascadeType.REMOVE)
  private List<RequisitionQcControlRun> controls;

  public Requisition getRequisition() {
    return requisition;
  }

  public void setRequisition(Requisition requisition) {
    this.requisition = requisition;
  }

  @Override
  public QualityControllable<?> getEntity() {
    return requisition;
  }

  @Override
  public List<RequisitionQcControlRun> getControls() {
    if (controls == null) {
      controls = new ArrayList<>();
    }
    return controls;
  }

}
