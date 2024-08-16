package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.SampleBoxableView;

@Entity
@Table(name = "SampleChangeLog", indexes = {
    @Index(name = "SampleChangeLog_sampleId_changeTime", columnList = "sampleId, changeTime")})
public class SampleChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sampleChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId", nullable = false, updatable = false)
  private Identifiable sample;

  @Override
  public Long getId() {
    return sample.getId();
  }

  @Override
  public void setId(Long id) {
    sample.setId(id);
  }

  public Long getSampleChangeLogId() {
    return sampleChangeLogId;
  }

  public Identifiable getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public void setSample(SampleBoxableView sample) {
    this.sample = sample;
  }

}
