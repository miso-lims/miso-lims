package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class ExperimentDto {
  public static class RunPartitionDto {
    private PartitionDto partition;
    private RunDto run;

    public RunPartitionDto() {
    }

    public RunPartitionDto(RunDto run, PartitionDto partition) {
      this.partition = partition;
      this.run = run;
    }

    public PartitionDto getPartition() {
      return partition;
    }

    public RunDto getRun() {
      return run;
    }

    public void setPartition(PartitionDto partition) {
      this.partition = partition;
    }

    public void setRun(RunDto run) {
      this.run = run;
    }

  }
  private String accession;

  private String alias;

  private String description;

  private long id;

  private LibraryDto library;

  private String name;

  private List<RunPartitionDto> partitions;

  private PlatformDto platform;

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

  public LibraryDto getLibrary() {
    return library;
  }

  public String getName() {
    return name;
  }

  public List<RunPartitionDto> getPartitions() {
    return partitions;
  }

  public PlatformDto getPlatform() {
    return platform;
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

  public void setLibrary(LibraryDto library) {
    this.library = library;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPartitions(List<RunPartitionDto> partitions) {
    this.partitions = partitions;
  }

  public void setPlatform(PlatformDto platform) {
    this.platform = platform;
  }

  public void setStudy(StudyDto study) {
    this.study = study;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
