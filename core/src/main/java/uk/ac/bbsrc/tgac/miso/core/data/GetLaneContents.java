package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Optional;

public interface GetLaneContents {
  Optional<String> getLaneContents(int lane);
}
