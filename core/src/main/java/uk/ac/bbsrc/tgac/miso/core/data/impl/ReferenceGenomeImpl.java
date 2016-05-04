package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;

@Entity
@Table(name = "ReferenceGenome")
public class ReferenceGenomeImpl implements ReferenceGenome {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long referenceGenomeId;

  @Column(unique = true, nullable = false)
  private String alias;

  @Override
  public Long getId() {
    return referenceGenomeId;
  }

  @Override
  public void setId(Long referenceGenomeId) {
        this.referenceGenomeId = referenceGenomeId;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String toString() {
    return "ReferenceGenomeImpl [referenceGenomeId=" + referenceGenomeId + ", alias=" + alias + "]";
  }

}