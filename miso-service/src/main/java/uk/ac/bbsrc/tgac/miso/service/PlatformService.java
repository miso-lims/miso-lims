package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.PlatformPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PlatformService {

  Platform get(long platformId) throws IOException;

  Collection<Platform> list() throws IOException;

  Set<PlatformType> listActivePlatformTypes() throws IOException;

  Collection<String> listDistinctPlatformTypeNames() throws IOException;

  PlatformPosition getPlatformPosition(long positionId) throws IOException;
}
