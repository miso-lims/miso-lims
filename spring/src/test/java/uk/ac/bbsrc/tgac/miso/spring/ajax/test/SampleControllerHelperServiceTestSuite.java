package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.SampleControllerHelperService;

public class SampleControllerHelperServiceTestSuite {

  @InjectMocks
  private SampleControllerHelperService sampleControllerHelperService;

  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private User user;
  @Mock
  private Sample sample;
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
  public final void testChangeSampleIdBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    when(requestManager.getSampleById(anyLong())).thenReturn(sample);
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
    verify(requestManager).saveSample(sample);

    assertEquals("New+Identification+Barcode+successfully+assigned.", response.get("response"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testChangeSampleIdBarcodeBlankBarcode() throws Exception {
    final long id = 1L;
    final String idBarcode = "";
    when(requestManager.getSampleById(anyLong())).thenReturn(sample);

    final JSONObject json = new JSONObject();
    json.put("sampleId", id);
    json.put("identificationBarcode", idBarcode);

    final JSONObject response = sampleControllerHelperService.changeSampleIdBarcode(null, json);

    verify(sample, never()).setIdentificationBarcode(idBarcode);
    verify(requestManager, never()).saveSample(sample);

    assertEquals("New+identification+barcode+not+recognized", response.get("error"));
  }

  @Test
  public final void testChangeLibraryIdBarcodeReturnsError() throws Exception {
    final long id = 1L;
    final String idBarcode = "idBarcode";
    final IOException expected = new IOException("thrown by mock");
    when(requestManager.getSampleById(anyLong())).thenReturn(sample);
    when(requestManager.saveSample(sample)).thenThrow(expected);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    final JSONObject json = new JSONObject();
    json.put("sampleId", id);
    json.put("identificationBarcode", idBarcode);

    final JSONObject error = new JSONObject();
    error.put("error", "thrown+by+mock");

    assertEquals(error, sampleControllerHelperService.changeSampleIdBarcode(null, json));
  }
}
