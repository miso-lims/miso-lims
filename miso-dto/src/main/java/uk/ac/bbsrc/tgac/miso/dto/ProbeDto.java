package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe.ProbeFeatureType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe.Read;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSetProbe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleProbe;

public class ProbeDto {

  private Long id;
  private String identifier;
  private String name;
  private String read;
  private String pattern;
  private String sequence;
  private String featureType;
  private String targetGeneId;
  private String targetGeneName;

  public static ProbeDto from(Probe from) {
    ProbeDto to = new ProbeDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setIdentifier, from.getIdentifier());
    setString(to::setName, from.getName());
    setString(to::setRead, maybeGetProperty(from.getRead(), Read::name));
    setString(to::setPattern, from.getPattern());
    setString(to::setSequence, from.getSequence());
    setString(to::setFeatureType, maybeGetProperty(from.getFeatureType(), Probe.ProbeFeatureType::name));
    setString(to::setTargetGeneId, from.getTargetGeneId());
    setString(to::setTargetGeneName, from.getTargetGeneName());
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getRead() {
    return read;
  }

  public void setRead(String read) {
    this.read = read;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getSequence() {
    return sequence;
  }

  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  public String getFeatureType() {
    return featureType;
  }

  public void setFeatureType(String featureType) {
    this.featureType = featureType;
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

  public ProbeSetProbe toProbeSetProbe() {
    ProbeSetProbe probe = new ProbeSetProbe();
    toProbe(probe);
    return probe;
  }

  public SampleProbe toSampleProbe() {
    SampleProbe probe = new SampleProbe();
    toProbe(probe);
    return probe;
  }

  private void toProbe(Probe to) {
    setLong(to::setId, getId(), false);
    setString(to::setIdentifier, getIdentifier());
    setString(to::setName, getName());
    setObject(to::setRead, getRead(), Read::valueOf);
    setString(to::setPattern, getPattern());
    setString(to::setSequence, getSequence());
    setObject(to::setFeatureType, getFeatureType(), ProbeFeatureType::valueOf);
    setString(to::setTargetGeneId, getTargetGeneId());
    setString(to::setTargetGeneName, getTargetGeneName());
  }
}
