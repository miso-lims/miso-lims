package uk.ac.bbsrc.tgac.miso.dto;

public class ArrayRunDto {

  private Long id;
  private String alias;
  private String description;
  private String filePath;
  private Long instrumentId;
  private String instrumentName;
  private Long arrayId;
  private String arrayAlias;
  private String status;
  private Boolean qcPassed;
  private String qcUserName;
  private String qcDate;
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

  public Long getArrayId() {
    return arrayId;
  }

  public void setArrayId(Long arrayId) {
    this.arrayId = arrayId;
  }

  public String getArrayAlias() {
    return arrayAlias;
  }

  public void setArrayAlias(String arrayAlias) {
    this.arrayAlias = arrayAlias;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Boolean getQcPassed() {
      return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed){
      this.qcPassed = qcPassed;
  }

  public String getQcUserName(){
      return qcUserName;
  }

  public void setQcUserName(String qcUserName){
      this.qcUserName = qcUserName;
  }

  public String getQcDate(){
      return qcDate;
  }

  public void setQcDate(String qcDate) {
      this.qcDate = qcDate;
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
