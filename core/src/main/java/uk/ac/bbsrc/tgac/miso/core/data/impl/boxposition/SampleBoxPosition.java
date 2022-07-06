package uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

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
