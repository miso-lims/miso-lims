package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe.ProbeFeatureType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;

public class ProbeSetDto {

  private Long id;
  private String name;
  private String featureTypeLabel;
  List<ProbeDto> probes;

  public static ProbeSetDto from(ProbeSet from) {
    ProbeSetDto to = new ProbeSetDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setName, from.getName());
    if (from.getProbes() != null) {
      to.setProbes(from.getProbes().stream().map(ProbeDto::from).toList());
      List<ProbeFeatureType> featureTypes = from.getProbes().stream().map(Probe::getFeatureType).distinct().toList();
      if (featureTypes.size() == 1) {
        to.setFeatureTypeLabel(featureTypes.get(0).getLabel());
      }
    }
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFeatureTypeLabel() {
    return featureTypeLabel;
  }

  public void setFeatureTypeLabel(String featureTypeLabel) {
    this.featureTypeLabel = featureTypeLabel;
  }

  public List<ProbeDto> getProbes() {
    return probes;
  }

  public void setProbes(List<ProbeDto> probes) {
    this.probes = probes;
  }

  public ProbeSet to() {
    ProbeSet to = new ProbeSet();
    setLong(to::setId, getId(), false);
    setString(to::setName, getName());
    if (getProbes() != null) {
      to.setProbes(getProbes().stream().map(ProbeDto::toProbeSetProbe).collect(Collectors.toSet()));
    }
    return to;
  }

}
