package uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "InstrumentStatusView")
@Immutable
public class InstrumentStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long instrumentId;

  private String name;

  @OneToMany(mappedBy = "instrumentStatus")
  private List<InstrumentStatusPosition> positions;

  public long getId() {
    return instrumentId;
  }

  public void setId(long id) {
    this.instrumentId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<InstrumentStatusPosition> getPositions() {
    if (positions == null) {
      positions = new ArrayList<>();
    }
    return positions;
  }

  public void setPositions(List<InstrumentStatusPosition> positions) {
    this.positions = positions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(instrumentId, name);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        InstrumentStatus::getId,
        InstrumentStatus::getName);
  }

}
