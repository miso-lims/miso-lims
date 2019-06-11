package uk.ac.bbsrc.tgac.miso.service;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface TissueTypeService extends DeleterService<TissueType>, ListService<TissueType>, SaveService<TissueType> {

}
