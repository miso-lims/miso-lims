package uk.ac.bbsrc.tgac.miso.dto;

public interface UpstreamQcFailableDto {

  public Long getEffectiveQcFailureId();

  public void setEffectiveQcFailureId(Long effectiveQcFailureId);

  public String getEffectiveQcFailureLevel();

  public void setEffectiveQcFailureLevel(String effectiveQcFailureLevel);

}
