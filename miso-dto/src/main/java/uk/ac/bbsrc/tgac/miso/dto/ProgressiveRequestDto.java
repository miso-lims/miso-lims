package uk.ac.bbsrc.tgac.miso.dto;

public class ProgressiveRequestDto {
  private int epoch;
  private long token;

  public int getEpoch() {
    return epoch;
  }

  public long getToken() {
    return token;
  }

  public void setEpoch(int epoch) {
    this.epoch = epoch;
  }

  public void setToken(long token) {
    this.token = token;
  }

  public void update(ProgressiveResponseDto response) {
    token = response.getToken();
    epoch = response.getEpoch();
  }
}
