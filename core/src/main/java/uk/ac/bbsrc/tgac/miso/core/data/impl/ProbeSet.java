package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class ProbeSet implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long probeSetId = UNSAVED_ID;

  private String name;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "probeSetId", nullable = false)
  private Set<ProbeSetProbe> probes;

  @Override
  public long getId() {
    return probeSetId;
  }

  @Override
  public void setId(long id) {
    this.probeSetId = id;
  }

  @Override
  public boolean isSaved() {
    return probeSetId != UNSAVED_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDeleteType() {
    return "Probe Set";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

  public Set<ProbeSetProbe> getProbes() {
    if (probes == null) {
      probes = new HashSet<>();
    }
    return probes;
  }

  public void setProbes(Set<ProbeSetProbe> probes) {
    this.probes = probes;
  }

}
