package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.PoolControllerHelperService;

public class PoolControllerHelperServiceTestSuite {

  @InjectMocks
  private PoolControllerHelperService poolControllerHelperService;
  
  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private User user;
  
  @SuppressWarnings("rawtypes")
  @Mock
  private Pool pool;
  @Mock
  private Authentication authentication;
  @Mock
  private MisoFilesManager misoFileManager;

  @Before
  public void setUp() throws Exception {
     MockitoAnnotations.initMocks(this);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public final void testChangePoolIdBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    when(requestManager.getPoolById(anyLong())).thenReturn(pool);
    
    final JSONObject json = new JSONObject();
    json.put("poolId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject response = poolControllerHelperService.changePoolIdBarcode(null,  json);
    
    verify(pool).setIdentificationBarcode(idBarcode);
    verify(requestManager).savePool(pool);
    
    assertEquals("New+identification+barcode+successfully+assigned.", response.get("response"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public final void testChangePoolIdBarcodeBlankBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "";
    when(requestManager.getPoolById(anyLong())).thenReturn(pool);
    
    final JSONObject json = new JSONObject();
    json.put("poolId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject response = poolControllerHelperService.changePoolIdBarcode(null,  json);
    
    verify(pool, never()).setIdentificationBarcode(idBarcode);
    verify(requestManager, never()).savePool(pool);
    
    assertEquals("New+identification+barcode+not+recognized", response.get("error"));
  }
}
