package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.store.AlertStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.service.AlertService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

public class DefaultAlertService implements AlertService {

  @Autowired
  private AlertStore alertStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private SecurityStore securityStore;

  public void setAlertStore(AlertStore alertStore) {
    this.alertStore = alertStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  @Override
  public Alert get(long alertId) throws IOException {
    Alert alert = alertStore.get(alertId);
    return alert;
  }

  @Override
  public Collection<Alert> listByUserId(long userId) throws IOException {
    return alertStore.listByUserId(userId);
  }

  @Override
  public Collection<Alert> listByUserId(long userId, long limit) throws IOException {
    return alertStore.listByUserId(userId, limit);
  }

  @Override
  public Collection<Alert> listUnreadByUserId(long userId) throws IOException {
    return alertStore.listUnreadByUserId(userId);
  }

  @Override
  public long save(Alert alert) throws IOException {
    loadChildEntities(alert);
    if (alert.getAlertId() != DefaultAlert.UNSAVED_ID) {
      // update
      Alert managed = get(alert.getAlertId());
      authorizationManager.throwIfNonAdminOrMatchingOwner(managed.getAlertUser());
      applyChanges(managed, alert);
      return alertStore.save(managed);
    } else {
      // save new
      return alertStore.save(alert);
    }
  }

  private void loadChildEntities(Alert alert) throws IOException {
    if (alert.getAlertUser() != null && alert.getAlertUser().getUserId() != UserImpl.UNSAVED_ID) {
      alert.setAlertUser(securityStore.getUserById(alert.getAlertUser().getUserId()));
    }
  }

  private void applyChanges(Alert target, Alert source) {
    // all updateable except alertId and userId
    target.setAlertLevel(source.getAlertLevel());
    target.setAlertTitle(source.getAlertTitle());
    target.setAlertText(source.getAlertText());
    target.setAlertDate(source.getAlertDate());
    target.setAlertRead(source.getAlertRead());
  }

}
