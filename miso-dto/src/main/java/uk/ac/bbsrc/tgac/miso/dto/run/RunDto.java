package uk.ac.bbsrc.tgac.miso.dto.run;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.WritableUrls;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = RunDto.class, name = "Base"),
    @JsonSubTypes.Type(value = IlluminaRunDto.class, name = "Illumina"),
    @JsonSubTypes.Type(value = IonTorrentRunDto.class, name = "IonTorrent"),
    @JsonSubTypes.Type(value = Ls454RunDto.class, name = "LS454"),
    @JsonSubTypes.Type(value = OxfordNanoporeRunDto.class, name = "OxfordNanopore"),
    @JsonSubTypes.Type(value = PacBioRunDto.class, name = "PacBio"),
    @JsonSubTypes.Type(value = SolidRunDto.class, name = "Solid") })
@JsonTypeName(value = "Base")
public class RunDto implements WritableUrls {
  private Long id;
  private String name;
  private String alias;
  private String description;
  private String status;
  private String lastModified;
  private String accession;
  private String platformType;
  private Long instrumentModelId;
  private String instrumentModelAlias;
  private String instrumentName;
  private Long instrumentId;
  private String startDate;
  private String endDate;
  private String progress;
  private String url;
  private Long sequencingParametersId;
  private String sequencingParametersName;
  private String runPath;
  private List<ContainerDto> containers;

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

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public Long getInstrumentModelId() {
    return instrumentModelId;
  }

  public void setInstrumentModelId(Long instrumentModelId) {
    this.instrumentModelId = instrumentModelId;
  }

  public String getInstrumentModelAlias() {
    return instrumentModelAlias;
  }

  public void setInstrumentModelAlias(String instrumentModelAlias) {
    this.instrumentModelAlias = instrumentModelAlias;
  }

  public String getInstrumentName() {
    return instrumentName;
  }

  public void setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
  }

  public Long getInstrumentId() {
    return instrumentId;
  }

  public void setInstrumentId(Long instrumentId) {
    this.instrumentId = instrumentId;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/run/{id}", getId()));
  }

  public Long getSequencingParametersId() {
    return sequencingParametersId;
  }

  public void setSequencingParametersId(Long sequencingParametersId) {
    this.sequencingParametersId = sequencingParametersId;
  }

  public String getSequencingParametersName() {
    return sequencingParametersName;
  }

  public void setSequencingParametersName(String sequencingParametersName) {
    this.sequencingParametersName = sequencingParametersName;
  }

  public String getRunPath() {
    return runPath;
  }

  public void setRunPath(String runPath) {
    this.runPath = runPath;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public String getProgress() {
    return progress;
  }

  public void setProgress(String progress) {
    this.progress = progress;
  }

  public List<ContainerDto> getContainers() {
    return containers;
  }

  public void setContainers(List<ContainerDto> containers) {
    this.containers = containers;
  }
}
