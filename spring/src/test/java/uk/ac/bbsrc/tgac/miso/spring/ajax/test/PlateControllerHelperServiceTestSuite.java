package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.PlateControllerHelperService;

public class PlateControllerHelperServiceTestSuite {

  @InjectMocks
  private PlateControllerHelperService plateControllerHelperService;
  
  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private User user;
  
  @SuppressWarnings("rawtypes")
  @Mock
  private Plate plate;
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
  public final void testChangePlateIdBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    when(requestManager.getPlateById(anyLong())).thenReturn(plate);
    
    final JSONObject json = new JSONObject();
    json.put("plateId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject response = plateControllerHelperService.changePlateIdBarcode(null,  json);
    
    verify(plate).setIdentificationBarcode(idBarcode);
    verify(requestManager).savePlate(plate);
    
    assertEquals("New+identification+barcode+successfully+assigned.", response.get("response"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public final void testChangePlateIdBarcodeBlankBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "";
    when(requestManager.getPlateById(anyLong())).thenReturn(plate);
    
    final JSONObject json = new JSONObject();
    json.put("plateId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject response = plateControllerHelperService.changePlateIdBarcode(null,  json);
    
    verify(plate, never()).setIdentificationBarcode(idBarcode);
    verify(requestManager, never()).savePlate(plate);
    
    assertEquals("New+identification+barcode+not+recognized", response.get("error"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public final void testChangePlateIdBarcodeReturnsError() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    final IOException expected = new IOException("thrown by mock");
    when(requestManager.getPlateById(anyLong())).thenReturn(plate);
    when(requestManager.savePlate(plate)).thenThrow(expected);
    
    
    final JSONObject json = new JSONObject();
    json.put("plateId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject error = new JSONObject();
    error.put("error", "thrown+by+mock");
    
    assertEquals(error, plateControllerHelperService.changePlateIdBarcode(null,  json));
  }
}
