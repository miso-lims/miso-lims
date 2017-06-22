package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;

public interface TargetedSequencingService {

  TargetedSequencing get(long targetedSequencingId) throws IOException;

  Collection<TargetedSequencing> list() throws IOException;
}
