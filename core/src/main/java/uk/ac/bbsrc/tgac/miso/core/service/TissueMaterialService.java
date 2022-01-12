package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;

public interface TissueMaterialService extends BulkSaveService<TissueMaterial>, DeleterService<TissueMaterial>,
    ListService<TissueMaterial> {

}