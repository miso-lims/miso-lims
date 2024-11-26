package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;

public interface TransferService extends DeleterService<Transfer>, ListService<Transfer>, SaveService<Transfer> {

  public long countPendingForUser(User user) throws IOException;

  public List<Transfer> listByProperties(Lab sender, Group recipient, Project project, Date transferTime)
      throws IOException;

  /**
   * Adds a sample to a transfer by persisting a TransferSample which links them. This is intended for
   * receipt or internal transfers only, which should not cause further changes to the sample, and as
   * such, no validation of the sample is performed.
   * 
   * @param transferSample
   * @throws IOException
   * @throws IllegalArgumentException if a distribution transfer is involved
   */
  public void addTransferSample(TransferSample transferSample) throws IOException;

  /**
   * Adds a library to a transfer by persisting a TransferLibrary which links them. This is intended
   * for receipt or internal transfers only, which should not cause further changes to the library,
   * and as such, no validation of the library is performed.
   * 
   * @param transferLibrary
   * @throws IOException
   * @throws IllegalArgumentException if a distribution transfer is involved
   */
  public void addTransferLibrary(TransferLibrary transferLibrary) throws IOException;

}
