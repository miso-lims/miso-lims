package uk.ac.bbsrc.tgac.miso.dto;

public class SequencingParametersDto {

  private Long id;
  private String name;
  private Long instrumentModelId;
  private String instrumentModelAlias;
  private int read1Length = 0;
  private int read2Length = 0;
  private String chemistry;
  private String runType;
  private Long createdById;
  private String creationDate;
  private Long updatedById;
  private String lastUpdated;
  private Integer movieTime;

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

  public Integer getRead1Length() {
    return read1Length;
  }

  public void setRead1Length(Integer read1Length) {
    this.read1Length = read1Length;
  }

  public Integer getRead2Length() {
    return read2Length;
  }

  public void setRead2Length(Integer read2Length) {
    this.read2Length = read2Length;
  }

  public String getChemistry() {
    return chemistry;
  }

  public void setChemistry(String chemistry) {
    this.chemistry = chemistry;
  }

  public String getRunType() {
    return runType;
  }

  public void setRunType(String runType) {
    this.runType = runType;
  }

  public Long getCreatedById() {
    return createdById;
  }

  public void setCreatedById(Long createdById) {
    this.createdById = createdById;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Integer getMovieTime() { return movieTime; }

  public void setMovieTime(Integer movieTime) { this.movieTime = movieTime; }
}
