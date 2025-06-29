package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class Deliverable implements Deletable, Serializable {
  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @Column(name = "deliverableId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long deliverableId = UNSAVED_ID;

  private String name;
  private boolean analysisReviewRequired = true;

  @ManyToOne
  @JoinColumn(name = "categoryId")
  private DeliverableCategory category;

  @Override
  public long getId() {
    return deliverableId;
  }

  @Override
  public void setId(long id) {
    this.deliverableId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAnalysisReviewRequired() {
    return analysisReviewRequired;
  }

  public void setAnalysisReviewRequired(boolean analysisReviewRequired) {
    this.analysisReviewRequired = analysisReviewRequired;
  }

  public DeliverableCategory getCategory() {
    return category;
  }

  public void setCategory(DeliverableCategory category) {
    this.category = category;
  }

  @Override
  public String getDeleteType() {
    return "Deliverable";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this, name, category);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        Deliverable::getName,
        Deliverable::getCategory);
  }
}
