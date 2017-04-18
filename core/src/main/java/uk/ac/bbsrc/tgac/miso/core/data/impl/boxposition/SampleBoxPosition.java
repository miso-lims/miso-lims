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

  @Id
  private Long sampleId;

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

}
