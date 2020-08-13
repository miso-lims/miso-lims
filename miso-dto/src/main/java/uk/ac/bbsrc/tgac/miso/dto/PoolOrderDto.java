package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;

public class PoolOrderDto {

  public static class OrderAliquotDto {

    private Long id; // aliquot ID, used by list.js bulk select column
    private LibraryAliquotDto aliquot;
    private Integer proportion;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public LibraryAliquotDto getAliquot() {
      return aliquot;
    }

    public void setAliquot(LibraryAliquotDto aliquot) {
      this.aliquot = aliquot;
    }

    public Integer getProportion() {
      return proportion;
    }

    public void setProportion(Integer proportion) {
      this.proportion = proportion;
    }

  }

  private Long id;
  private String alias;
  private String description;
  private Long purposeId;
  private String purposeAlias;
  private Long parametersId;
  private String parametersName;
  private Integer partitions;
  private Long containerModelId;
  private boolean draft;
  private List<OrderAliquotDto> orderAliquots;
  private boolean duplicateIndices;
  private Set<String> duplicateIndicesSequences;
  private boolean nearDuplicateIndices;
  private Set<String> nearDuplicateIndicesSequences;
  private String status;
  private Long poolId;
  private String poolAlias;
  private Long sequencingOrderId;
  private String longestIndex;

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

  public Long getPurposeId() {
    return purposeId;
  }

  public void setPurposeId(Long purposeId) {
    this.purposeId = purposeId;
  }

  public String getPurposeAlias() {
    return purposeAlias;
  }

  public void setPurposeAlias(String purposeAlias) {
    this.purposeAlias = purposeAlias;
  }

  public Long getParametersId() {
    return parametersId;
  }

  public void setParametersId(Long parametersId) {
    this.parametersId = parametersId;
  }

  public String getParametersName() {
    return parametersName;
  }

  public void setParametersName(String parametersName) {
    this.parametersName = parametersName;
  }

  public Integer getPartitions() {
    return partitions;
  }

  public void setPartitions(Integer partitions) {
    this.partitions = partitions;
  }

  public Long getContainerModelId() {
    return containerModelId;
  }

  public void setContainerModelId(Long containerModelId) {
    this.containerModelId = containerModelId;
  }

  public boolean isDraft() {
    return draft;
  }

  public void setDraft(boolean draft) {
    this.draft = draft;
  }

  public List<OrderAliquotDto> getOrderAliquots() {
    return orderAliquots;
  }

  public void setOrderAliquots(List<OrderAliquotDto> libraries) {
    this.orderAliquots = libraries;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getPoolId() {
    return poolId;
  }

  public void setPoolId(Long poolId) {
    this.poolId = poolId;
  }

  public String getPoolAlias() {
    return poolAlias;
  }

  public void setPoolAlias(String poolAlias) {
    this.poolAlias = poolAlias;
  }

  public Long getSequencingOrderId() {
    return sequencingOrderId;
  }

  public void setSequencingOrderId(Long sequencingOrderId) {
    this.sequencingOrderId = sequencingOrderId;
  }

  public boolean isDuplicateIndices() {
    return duplicateIndices;
  }

  public void setDuplicateIndices(boolean duplicateIndices) {
    this.duplicateIndices = duplicateIndices;
  }

  public Set<String> getDuplicateIndicesSequences() {
    return duplicateIndicesSequences;
  }

  public void setDuplicateIndicesSequences(Set<String> duplicateIndicesSequences) {
    this.duplicateIndicesSequences = duplicateIndicesSequences;
  }

  public boolean isNearDuplicateIndices() {
    return nearDuplicateIndices;
  }

  public void setNearDuplicateIndices(boolean nearDuplicateIndices) {
    this.nearDuplicateIndices = nearDuplicateIndices;
  }

  public Set<String> getNearDuplicateIndicesSequences() {
    return nearDuplicateIndicesSequences;
  }

  public void setNearDuplicateIndicesSequences(Set<String> nearDuplicateIndicesSequences) {
    this.nearDuplicateIndicesSequences = nearDuplicateIndicesSequences;
  }

  public String getLongestIndex(){
    return longestIndex;
  }

  public void setLongestIndex(String longestIndex){
    this.longestIndex = longestIndex;
  }

}
