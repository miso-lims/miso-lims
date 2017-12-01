package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class IlluminaNotificationDto extends NotificationDto {

  private int bclCount;
  private int callCycle;
  private IlluminaChemistry chemistry;
  private int imgCycle;
  private List<Integer> indexLengths;
  private int numCycles;
  private int numReads;
  private Map<Integer, String> poolNames;
  private int readLength;
  private int scoreCycle;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    IlluminaNotificationDto other = (IlluminaNotificationDto) obj;
    if (bclCount != other.bclCount) return false;
    if (callCycle != other.callCycle) return false;
    if (chemistry != other.chemistry) return false;
    if (imgCycle != other.imgCycle) return false;
    if (indexLengths == null) {
      if (other.indexLengths != null) return false;
    } else if (!indexLengths.equals(other.indexLengths)) return false;
    if (numCycles != other.numCycles) return false;
    if (numReads != other.numReads) return false;
    if (poolNames == null) {
      if (other.poolNames != null) return false;
    } else if (!poolNames.equals(other.poolNames)) return false;
    if (readLength != other.readLength) return false;
    if (scoreCycle != other.scoreCycle) return false;
    return true;
  }

  public int getBclCount() {
    return bclCount;
  }

  public int getCallCycle() {
    return callCycle;
  }

  public IlluminaChemistry getChemistry() {
    return chemistry;
  }

  public int getImgCycle() {
    return imgCycle;
  }

  /**
   * Get the lengths of the index reads in this run
   * 
   * This is the number of nucleotides in the index reads of this run. A single-index 6bp run would be encoded as [6], while a dual-index
   * 8bp run would be [8,8]. If no index was done, this would be an empty list.
   */
  public List<Integer> getIndexLengths() {
    return indexLengths;
  }

  @Override
  public Optional<String> getLaneContents(int lane) {
    return poolNames != null && poolNames.containsKey(lane) ? Optional.of(poolNames.get(lane)) : Optional.empty();
  }

  public int getNumCycles() {
    return numCycles;
  }

  public int getNumReads() {
    return numReads;
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ILLUMINA;
  }

  public Map<Integer, String> getPoolNames() {
    return poolNames;
  }

  public int getReadLength() {
    return readLength;
  }

  public int getScoreCycle() {
    return scoreCycle;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + bclCount;
    result = prime * result + callCycle;
    result = prime * result + ((chemistry == null) ? 0 : chemistry.hashCode());
    result = prime * result + imgCycle;
    result = prime * result + ((indexLengths == null) ? 0 : indexLengths.hashCode());
    result = prime * result + numCycles;
    result = prime * result + numReads;
    result = prime * result + ((poolNames == null) ? 0 : poolNames.hashCode());
    result = prime * result + readLength;
    result = prime * result + scoreCycle;
    return result;
  }

  public void setBclCount(int bclCount) {
    this.bclCount = bclCount;
  }

  public void setCallCycle(int callCycle) {
    this.callCycle = callCycle;
  }

  public void setChemistry(IlluminaChemistry chemistry) {
    this.chemistry = chemistry;
  }

  public void setImgCycle(int imgCycle) {
    this.imgCycle = imgCycle;
  }

  public void setIndexLengths(List<Integer> indexLengths) {
    this.indexLengths = indexLengths;
  }

  public void setNumCycles(int numCycles) {
    this.numCycles = numCycles;
  }

  public void setNumReads(int numReads) {
    this.numReads = numReads;
  }

  public void setPoolNames(Map<Integer, String> poolNames) {
    this.poolNames = poolNames;
  }

  public void setReadLength(int readLength) {
    this.readLength = readLength;
  }

  public void setScoreCycle(int scoreCycle) {
    this.scoreCycle = scoreCycle;
  }

  @Override
  public boolean test(SequencingParameters params) {
    return params.getPlatform().getPlatformType() == PlatformType.ILLUMINA &&
        Math.abs(params.getReadLength() - readLength) < 2 && params.isPaired() == isPairedEndRun() && params.getChemistry() == chemistry;
  }

  @Override
  public String toString() {
    return "IlluminaNotificationDto [callCycle=" + callCycle + ", chemistry=" + chemistry + ", imgCycle=" + imgCycle + ", indexLengths="
        + indexLengths + ", numCycles=" + numCycles + ", poolNames=" + poolNames + ", readLength=" + readLength + ", scoreCycle="
        + scoreCycle + ", bclCount=" + bclCount + ", getRunAlias()=" + getRunAlias() + ", getSequencerName()=" + getSequencerName()
        + ", getContainerSerialNumber()=" + getContainerSerialNumber() + ", getLaneCount()=" + getLaneCount() + ", getHealthType()="
        + getHealthType() + ", getSequencerFolderPath()=" + getSequencerFolderPath() + ", isPairedEndRun()=" + isPairedEndRun()
        + ", getSoftware()=" + getSoftware() + ", getStartDate()=" + getStartDate() + ", getCompletionDate()=" + getCompletionDate() + "]";
  }

}
