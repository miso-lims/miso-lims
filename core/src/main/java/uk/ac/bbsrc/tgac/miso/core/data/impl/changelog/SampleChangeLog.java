package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.SampleBoxableView;

@Entity
@Table(appliesTo = "SampleChangeLog", indexes = {
    @Index(name = "SampleChangeLog_sampleId_changeTime", columnNames = {"sampleId", "changeTime"})})
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
