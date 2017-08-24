package uk.ac.bbsrc.tgac.miso.dto;

public class ExperimentDto {
  private String accession;

  private String alias;

  private String description;

  private long id;

  private String name;

  private PlatformDto platform;

  private PoolDto pool;

  private RunDto run;

  private StudyDto study;

  private String title;

  public String getAccession() {
    return accession;
  }

  public String getAlias() {
    return alias;
  }

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public PlatformDto getPlatform() {
    return platform;
  }

  public PoolDto getPool() {
    return pool;
  }

  public RunDto getRun() {
    return run;
  }

  public StudyDto getStudy() {
    return study;
  }

  public String getTitle() {
    return title;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatform(PlatformDto platform) {
    this.platform = platform;
  }

  public void setPool(PoolDto pool) {
    this.pool = pool;
  }

  public void setRun(RunDto run) {
    this.run = run;
  }

  public void setStudy(StudyDto study) {
    this.study = study;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
