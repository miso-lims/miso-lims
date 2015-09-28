package uk.ac.bbsrc.tgac.miso.core.event;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.exception.AttachmentException;

/**
 * Interface representing a callback scenario that can be fired whenever an Attachable is attached to an object of type T,
 * or when something happens in the Attachable that can then call a method on the object T.
 *
 * @author Rob Davey
 * @date 26/08/14
 * @since 0.2.1-SNAPSHOT
 */
public interface AttachmentNotificationCallback<T> {
  public void callback(Attachable a, T obj) throws AttachmentException;
}