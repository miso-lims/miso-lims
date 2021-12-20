package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusService extends DeleterService<DetailedQcStatus>, BulkSaveService<DetailedQcStatus>,
    ListService<DetailedQcStatus> {

}