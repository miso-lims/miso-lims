package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;

import java.io.IOException;
import java.util.List;

/**
 * Interface to persist relationships between {@link Attachable} objects to .
 *
 * @author Rob Davey
 * @date 04/09/14
 */
public interface AttachableStore {
  public boolean unattachAll(Attachable attachable) throws IOException;
  public boolean unattachByAttachedId(Attachable attachable, Nameable attached) throws IOException;
  public List<Attachable<? extends Nameable>> listAttachableByAttached(Nameable attached) throws IOException;
  public List<? extends Nameable> listAttachedByAttachable(Attachable<? extends Nameable> attachable) throws IOException;
  public long save(Attachable<? extends Nameable> attachable) throws IOException;
}
