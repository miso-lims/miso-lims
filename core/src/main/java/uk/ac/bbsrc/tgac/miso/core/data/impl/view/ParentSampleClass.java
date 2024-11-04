package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "SampleClass")
public class ParentSampleClass implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long sampleClassId;

  private String alias;

  private String sampleCategory;

  public long getId() {
    return sampleClassId;
  }

  public void setId(long id) {
    this.sampleClassId = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
  }

}
