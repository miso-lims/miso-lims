package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

/**
 * Results from a run scanner. After this is received, the next request should be set up using
 * {@link ProgressiveRequestDto#update(ProgressiveResponseDto)}.
 */
public class ProgressiveResponseDto {
  private boolean moreAvailable;
  private int epoch;
  private long token;
  private List<NotificationDto> updates;

  public int getEpoch() {
    return epoch;
  }

  public long getToken() {
    return token;
  }

  /**
   * This recent DTOs found by the run scanner.
   * 
   * The run scanner is designed to over-deliver, so runs previously seen maybe delivered. Additionally, the run scanner will re-probe
   * unfinished runs, resulting in runs with the same run name but different content.
   *
   * There may also be multiple runs with the same name that are from different sequencers depending on the user configuration of run
   * scanner.
   */
  public List<NotificationDto> getUpdates() {
    return updates;
  }

  public boolean isMoreAvailable() {
    return moreAvailable;
  }

  public void setMoreAvailable(boolean moreAvailable) {
    this.moreAvailable = moreAvailable;
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
