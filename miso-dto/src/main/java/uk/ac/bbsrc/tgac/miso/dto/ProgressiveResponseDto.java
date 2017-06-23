package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class ProgressiveResponseDto {
  private int epoch;
  private long token;
  private List<NotificationDto> updates;

  public int getEpoch() {
    return epoch;
  }

  public long getToken() {
    return token;
  }

  public List<NotificationDto> getUpdates() {
    return updates;
  }

  public void setEpoch(int epoch) {
    this.epoch = epoch;
  }

  public void setToken(long token) {
    this.token = token;
  }

  public void setUpdates(List<NotificationDto> updates) {
    this.updates = updates;
  }
}
