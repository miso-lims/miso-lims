package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.spring.ajax.SampleControllerHelperService;

public class SampleControllerHelperServiceTestSuite {

  @InjectMocks
  private SampleControllerHelperService sampleControllerHelperService;

  @Mock
  private SecurityManager securityManager;
  @Mock
  private User user;
  @Mock
  private Sample sample;
  @Mock
  private Authentication authentication;
  @Mock
  private MisoFilesManager misoFileManager;
  @Mock
  private SampleService sampleService;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public final void testChangeSampleIdBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    when(sampleService.get(anyLong())).thenReturn(sample);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    
    final JSONObject json = new JSONObject();
    json.put("sampleId", id);
    json.put("identificationBarcode", idBarcode);

    final JSONObject response = sampleControllerHelperService.changeSampleIdBarcode(null, json);

    verify(sample).setIdentificationBarcode(idBarcode);
    verify(sampleService).update(sample);

    assertEquals("New+Identification+Barcode+successfully+assigned.", response.get("response"));
  }

  @Test
  public final void testChangeSampleIdBarcodeBlankBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "";
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    when(sampleService.get(anyLong())).thenReturn(sample);

    final JSONObject json = new JSONObject();
    json.put("sampleId", id);
    json.put("identificationBarcode", idBarcode);

    final JSONObject response = sampleControllerHelperService.changeSampleIdBarcode(null, json);

    verify(sample, never()).setIdentificationBarcode(idBarcode);
    verify(sampleService, never()).update(sample);

    assertEquals("New+identification+barcode+not+recognized", response.get("error"));
  }
}
