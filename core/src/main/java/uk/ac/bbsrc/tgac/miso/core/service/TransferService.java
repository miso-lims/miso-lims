package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;

public interface TransferService extends DeleterService<Transfer>, ListService<Transfer>, SaveService<Transfer> {

  public long countPendingForUser(User user) throws IOException;

  public List<Transfer> listByProperties(Lab sender, Group recipient, Project project, Date transferTime) throws IOException;

  public void addTransferSample(TransferSample transferSample) throws IOException;

}
