package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.spring.ajax.LibraryControllerHelperService;

public class LibraryControllerHelperServiceTest {

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
  @Mock
  private LibraryService libraryService;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public final void testChangeLibraryIdBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    when(libraryService.get(anyLong())).thenReturn(library);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    
    final JSONObject json = new JSONObject();
    json.put("libraryId", id);
    json.put("identificationBarcode", idBarcode);

    final JSONObject response = libraryControllerHelperService.changeLibraryIdBarcode(null, json);

    verify(library).setIdentificationBarcode(idBarcode);
    verify(libraryService).update(library);

    assertEquals("New+identification+barcode+successfully+assigned.", response.get("response"));
  }

  @Test
  public final void testChangeLibraryIdBarcodeBlankBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "";
    when(libraryService.get(anyLong())).thenReturn(library);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    
    final JSONObject json = new JSONObject();
    json.put("libraryId", id);
    json.put("identificationBarcode", idBarcode);

    libraryControllerHelperService.changeLibraryIdBarcode(null, json);

    verify(libraryService).update(library);
  }

  @Test
  public final void testChangeLibraryIdBarcodeReturnsError() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    final IOException expected = new IOException("thrown by mock");
    when(libraryService.get(anyLong())).thenReturn(library);
    Mockito.doThrow(expected).when(libraryService).update(library);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    
    final JSONObject json = new JSONObject();
    json.put("libraryId", id);
    json.put("identificationBarcode", idBarcode);

    final JSONObject error = new JSONObject();
    error.put("error", "thrown+by+mock");

    assertEquals(error, libraryControllerHelperService.changeLibraryIdBarcode(null, json));
  }
}
