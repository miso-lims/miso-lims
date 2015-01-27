package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.event.AttachmentNotificationCallback;
import uk.ac.bbsrc.tgac.miso.core.exception.AttachmentException;

import java.util.Set;

/**
 * The Attachable interface is used to relate an object to any other object, usually a Nameable (it itself inherits from
 * Nameable). This means objects such as {@link uk.ac.bbsrc.tgac.miso.core.workflow.Workflow} and
 * {@link uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess} can be attached to other MISO objects, such as
 * an {@link EntityGroup}.
 *
 * Additionally, Attachables specify a notifyAttached() method with a callback so that any event that occurs within the
 * Attachable can be propagated to the attached object too.
 *
 * @author Rob Davey
 * @date 21/08/14
 * @since 0.2.1-SNAPSHOT
 */
public interface Attachable<T> extends Nameable {
  public void attach(T obj);
  public Set<T> getAttached();
  public void notifyAttached(AttachmentNotificationCallback<T> anc) throws AttachmentException;
}