package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;

public interface StorageLabelDao extends BulkSaveDao<StorageLabel> {

  StorageLabel getByLabel(String label) throws IOException;

  long getUsage(StorageLabel label) throws IOException;

}
