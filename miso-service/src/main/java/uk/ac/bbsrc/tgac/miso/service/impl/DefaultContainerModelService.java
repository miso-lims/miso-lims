package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.store.SequencingContainerModelStore;
import uk.ac.bbsrc.tgac.miso.service.ContainerModelService;

public class DefaultContainerModelService implements ContainerModelService {
  @Autowired
  private SequencingContainerModelStore containerModelDao;

  @Override
  public SequencingContainerModel get(long id) throws IOException {
    return containerModelDao.getModel(id);
  }

  @Override
  public SequencingContainerModel find(Platform platform, String search, int partitionCount) throws IOException {
    return containerModelDao.findModel(platform, search, partitionCount);
  }

  @Override
  public List<SequencingContainerModel> list() throws IOException {
    return containerModelDao.listModels();
  }
}
