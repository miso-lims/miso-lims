package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PlatformService {

  Platform get(long platformId) throws IOException;

  Collection<Platform> list() throws IOException;

  Collection<PlatformType> listActivePlatformTypes() throws IOException;

  Collection<String> listDistinctPlatformTypeNames() throws IOException;
}
