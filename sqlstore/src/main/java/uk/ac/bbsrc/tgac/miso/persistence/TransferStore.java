package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;

public interface TransferStore extends SaveDao<Transfer> {

  public long countPendingForGroups(Collection<Group> groups) throws IOException;

  public <T extends TransferItem<?>> void deleteTransferItem(T item) throws IOException;

  public List<Transfer> listByProperties(Lab sender, Group recipient, Project project, Date transferTime) throws IOException;

  public void detachEntity(Boxable object);

}
