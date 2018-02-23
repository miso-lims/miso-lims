package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.ServiceRecordStore;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

public class DefaultServiceRecordServiceTest {

  @Mock
  private AuthorizationManager authorizationManager;

  @Mock
  private MisoFilesManager misoFilesManager;

  @Mock
  private ServiceRecordStore serviceRecordDao;

  @Mock
  private DeletionStore deletionStore;

  @Mock
  private InstrumentService instrumentService;

  @InjectMocks
  private DefaultServiceRecordService sut;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testDeleteAttachments() throws IOException {
    // Deletion is already tested separately. This is only to test the interaction with MisoFilesManager to delete attachments afterwards
    ServiceRecord record = new ServiceRecord();
    record.setId(1L);

    Mockito.when(misoFilesManager.getFileNames(ServiceRecord.class, "1")).thenReturn(Arrays.asList("file"));
    Mockito.when(serviceRecordDao.get(1L)).thenReturn(record);
    sut.delete(record);

    Mockito.verify(misoFilesManager, Mockito.times(1)).deleteFile(ServiceRecord.class, "1", "file");
  }

}
