package uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;

@Entity
@Immutable
@Table(name = "DilutionBoxPosition")
@Synchronize("LibraryDilution")
public class DilutionBoxPosition extends AbstractBoxPosition {

  @Id
  private Long dilutionId;

  public Long getDilutionId() {
    return dilutionId;
  }

  public void setSampleId(Long dilutionId) {
    this.dilutionId = dilutionId;
  }

}
