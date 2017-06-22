package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.event.Alert;

public interface AlertService {

  /**
   * Fetch a single saved Alert
   * 
   * @param alertId ID of the alert to fetch
   * @return the Alert
   */
  public Alert get(long alertId) throws IOException;

  /**
   * Fetch all alerts, read and unread, for a user
   * 
   * @param userId ID of the user for whom to fetch the alerts
   * @return all alerts for the user; an empty Collection if the user either doesn't exist or doesn't have any alerts
   */
  public Collection<Alert> listByUserId(long userId) throws IOException;

  /**
   * Fetch alerts, read and unread, for a user
   * 
   * @param userId ID of the user for whom to fetch the alerts
   * @param limit max number of alerts to fetch
   * @return all alerts for the user up, limited in quantity as specified; an empty Collection if the user either doesn't exist or doesn't
   *         have any alerts
   */
  public Collection<Alert> listByUserId(long userId, long limit) throws IOException;

  /**
   * Fetch all unread alerts for a user
   * 
   * @param userId ID of the user for whom to fetch the alerts
   * @return all unread alerts for the user; an empty Collection if the user either doesn't exist or doesn't have any unread alerts
   */
  public Collection<Alert> listUnreadByUserId(long userId) throws IOException;

  /**
   * Fetch all unread alerts for current user
   */
  public Collection<Alert> listUnreadForCurrentUser() throws IOException;

  /**
   * Save a new alert or update an existing one
   * 
   * @param alert the Alert to save or update
   * @return the ID of the saved Alert
   */
  public long save(Alert alert) throws IOException;

}
