package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;

public interface StorageLocationMapService extends DeleterService<StorageLocationMap>, ProviderService<StorageLocationMap> {

  public List<StorageLocationMap> list() throws IOException;

  public long create(MultipartFile file, String description) throws IOException;

  public long update(StorageLocationMap object) throws IOException;

}
