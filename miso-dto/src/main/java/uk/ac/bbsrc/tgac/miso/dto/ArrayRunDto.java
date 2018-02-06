package uk.ac.bbsrc.tgac.miso.dto;

public class ArrayRunDto {

  private Long id;
  private String alias;
  private String description;
  private String filePath;
  private Long instrumentId;
  private String instrumentName;
  private ArrayDto array;
  private String status;
  private String startDate;
  private String completionDate;
  private String lastModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Long getInstrumentId() {
    return instrumentId;
  }

  public void setInstrumentId(Long instrumentId) {
    this.instrumentId = instrumentId;
  }

  public String getInstrumentName() {
    return instrumentName;
  }

  public void setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
  }

  public ArrayDto getArray() {
    return array;
  }

  public void setArray(ArrayDto array) {
    this.array = array;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(String endDate) {
    this.completionDate = endDate;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

}
