package uk.ac.bbsrc.tgac.miso.core.data.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;

@Entity
@Table(name = "ReferenceGenome")
public class ReferenceGenomeImpl implements ReferenceGenome {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long referenceGenomeId = UNSAVED_ID;

  @Column(unique = true, nullable = false)
  private String alias;

  @ManyToOne
  @JoinColumn(name = "defaultScientificNameId")
  private ScientificName defaultScientificName;

  @Override
  public long getId() {
    return referenceGenomeId;
  }

  @Override
  public void setId(long referenceGenomeId) {
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

  @Override
  public int hashCode() {
    return new HashCodeBuilder(9, 335)
        .append(alias)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ReferenceGenomeImpl other = (ReferenceGenomeImpl) obj;
    return new EqualsBuilder()
        .append(alias, other.alias)
        .isEquals();
  }

  @Override
  public ScientificName getDefaultScientificName() {
    return defaultScientificName;
  }

  @Override
  public void setDefaultScientificName(ScientificName defaultSciName) {
    this.defaultScientificName = defaultSciName;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Reference Genome";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

}
