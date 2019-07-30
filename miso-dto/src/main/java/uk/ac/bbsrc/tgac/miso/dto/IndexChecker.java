package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;

public interface IndexChecker {

  Set<String> getDuplicateIndicesSequences(Pool pool);

  Set<String> getNearDuplicateIndicesSequences(Pool pool);

  Set<String> getDuplicateIndicesSequences(ListPoolView pool);

  Set<String> getNearDuplicateIndicesSequences(ListPoolView pool);
}
