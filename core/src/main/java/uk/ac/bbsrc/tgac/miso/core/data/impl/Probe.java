package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

@MappedSuperclass
public abstract class Probe implements Identifiable, Serializable {

  public static enum ProbeFeatureType {

    ANTIBODY_CAPTURE("Antibody Capture"), //
    CRISPR("CRISPR Guide Capture"), //
    ANTIGEN_CAPTURE("Antigen Capture"), //
    CUSTOM("Custom");

    private final String label;

    private ProbeFeatureType(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  public static enum Read {
    R1, R2
  }

  private static final long serialVersionUID = 1L;
  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long probeId = UNSAVED_ID;

  private String identifier;
  private String name;

  @Column(name = "readNumber")
  @Enumerated(EnumType.STRING)
  private Read read;
  private String pattern;
  private String sequence;

  @Enumerated(EnumType.STRING)
  private ProbeFeatureType featureType;

  // for CRISPR Guide Capture only
  private String targetGeneId;
  private String targetGeneName;


  @Override
  public long getId() {
    return probeId;
  }

  @Override
  public void setId(long id) {
    this.probeId = id;
  }

  @Override
  public boolean isSaved() {
    return probeId != UNSAVED_ID;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Read getRead() {
    return read;
  }

  public void setRead(Read read) {
    this.read = read;
  }

  public String getPattern() {
    return pattern;
  }

  public void setFeatureType(ProbeFeatureType featureType) {
    this.featureType = featureType;
  }

  public String getSequence() {
    return sequence;
  }

  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public ProbeFeatureType getFeatureType() {
    return featureType;
  }

  public String getTargetGeneId() {
    return targetGeneId;
  }

  public void setTargetGeneId(String targetGeneId) {
    this.targetGeneId = targetGeneId;
  }

  public String getTargetGeneName() {
    return targetGeneName;
  }

  public void setTargetGeneName(String targetGeneName) {
    this.targetGeneName = targetGeneName;
  }

}
