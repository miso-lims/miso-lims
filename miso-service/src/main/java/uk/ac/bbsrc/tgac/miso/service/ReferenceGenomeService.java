package uk.ac.bbsrc.tgac.miso.service;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface ReferenceGenomeService
    extends DeleterService<ReferenceGenome>, ListService<ReferenceGenome>, SaveService<ReferenceGenome> {

}