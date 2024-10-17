package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "SampleQC")
public class SampleQC extends QC {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sample_sampleId")
  private Sample sample;

  @OneToMany(mappedBy = "qc", cascade = CascadeType.REMOVE)
  private List<SampleQcControlRun> controls;

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public QualityControllable<?> getEntity() {
    return sample;
  }

  @Override
  public List<SampleQcControlRun> getControls() {
    if (controls == null) {
      controls = new ArrayList<>();
    }
    return controls;
  }

}
