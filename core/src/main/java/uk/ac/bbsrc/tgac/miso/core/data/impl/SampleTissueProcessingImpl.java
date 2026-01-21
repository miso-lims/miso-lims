package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("TissueProcessing")
public class SampleTissueProcessingImpl extends DetailedSampleImpl implements SampleTissueProcessing {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "indexId")
  private SampleIndex index;

  @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SampleProbe> probes;

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitSampleTissueProcessing(this);
  }

  @Override
  public SampleIndex getIndex() {
    return index;
  }

  @Override
  public void setIndex(SampleIndex index) {
    this.index = index;
  }

  @Override
  public Set<SampleProbe> getProbes() {
    if (probes == null) {
      probes = new HashSet<>();
    }
    return probes;
  }

  @Override
  public void setProbes(Set<SampleProbe> probes) {
    this.probes = probes;
  }

}
