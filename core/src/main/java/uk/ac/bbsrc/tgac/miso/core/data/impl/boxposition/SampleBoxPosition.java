package uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxPosition;

@Entity
@Immutable
@Table(name = "SampleBoxPosition")
@Synchronize("Sample")
public class SampleBoxPosition extends AbstractBoxPosition {

  private static final long serialVersionUID = 1L;

  @Id
  private Long sampleId;

  @Override
  public long getItemId() {
    return sampleId;
  }

  @Override
  public void setItemId(long id) {
    this.sampleId = id;
  }
}
