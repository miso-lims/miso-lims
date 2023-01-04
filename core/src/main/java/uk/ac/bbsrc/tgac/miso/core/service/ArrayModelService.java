package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;

public interface ArrayModelService extends BulkSaveService<ArrayModel>, DeleterService<ArrayModel>,
    ListService<ArrayModel> {

}
