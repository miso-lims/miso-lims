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
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.LibraryControllerHelperService;

public class LibraryControllerHelperServiceTestSuite {

  @InjectMocks
  private LibraryControllerHelperService libraryControllerHelperService;
  
  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private User user;
  @Mock
  private Library library;
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
  public final void testChangeLibraryIdBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    when(requestManager.getLibraryById(anyLong())).thenReturn(library);
    
    final JSONObject json = new JSONObject();
    json.put("libraryId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject response = libraryControllerHelperService.changeLibraryIdBarcode(null,  json);
    
    verify(library).setIdentificationBarcode(idBarcode);
    verify(requestManager).saveLibrary(library);
    
    assertEquals("New+identification+barcode+successfully+assigned.", response.get("response"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public final void testChangeLibraryIdBarcodeBlankBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "";
    when(requestManager.getLibraryById(anyLong())).thenReturn(library);
    
    final JSONObject json = new JSONObject();
    json.put("libraryId", id);
    json.put("identificationBarcode", idBarcode);
    
    final JSONObject response = libraryControllerHelperService.changeLibraryIdBarcode(null,  json);
    
    verify(library, never()).setIdentificationBarcode(idBarcode);
    verify(requestManager, never()).saveLibrary(library);
    
    assertEquals("New+identification+barcode+not+recognized", response.get("error"));
  }
}
