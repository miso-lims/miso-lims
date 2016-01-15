package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;

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

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import uk.ac.bbsrc.tgac.miso.spring.ajax.BoxControllerHelperService;

public class BoxControllerHelperServiceTestSuite {

  @InjectMocks
  private BoxControllerHelperService boxControllerHelperService;
  
  @Mock
  private SecurityManager securityManager;
  @Mock 
  private RequestManager requestManager;
  @Mock
  private MisoFilesManager misoFileManager;
  @Mock
  private User user;
  @Mock
  private Box box;
  @Mock 
  private Sample sample;
  @Mock
  private Library library;
  @Mock 
  private Authentication authentication;
  @Mock 
  private Boxable boxable;
  @Mock
  private BoxScanner boxScanner;
  @Mock 
  private VisionMateScan scan;
  @Mock
  private File file;
  
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public final void testDeleteBox() throws Exception {
    final long id = 1L;
    when(requestManager.getBoxById(anyLong())).thenReturn(box);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    
    final JSONObject json = new JSONObject();
    json.put("boxId", id);
    final JSONObject response = boxControllerHelperService.deleteBox(null, json);
    
    verify(requestManager).deleteBox(requestManager.getBoxById(id));
    
    assertEquals("Box+deleted", response.get("response"));
  }
  
  @Test(expected = JSONException.class)
  public final void testDeleteBoxBadJson() throws Exception {
    when(requestManager.getBoxById(anyLong())).thenReturn(box);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    
    final JSONObject json = new JSONObject();
    json.put("jack in the", 12L);
    json.put("boxId", "box");
    
    boxControllerHelperService.deleteBox(null, json);
  }
  
  @Test
  public final void testGetBoxScanWithReadErrorsReturnsError() throws Exception {
    final long id = 1L;
    final JSONObject error = new JSONObject();
    final JSONObject errorComponents = new JSONObject();
    errorComponents.put("errorPositions", "[]");
    errorComponents.put("successPositions", "[]");
    errorComponents.put("message", "The scanner can not read some positions. Please remove or fix and then rescan: ");
    errorComponents.put("type", "Read Error");
    error.put("errors", errorComponents);
    
    when(boxScanner.getScan()).thenReturn(scan);
    when(scan.hasReadErrors()).thenReturn(true);
    
    JSONObject json = new JSONObject();
    json.put("boxId", id);
    assertEquals(error, boxControllerHelperService.getBoxScan(null, json));
  }
  
  @Test
  public final void testExportBoxContentsFormNoBoxId() throws Exception {
    when(requestManager.getBoxById(anyLong())).thenReturn(box);
    
    final JSONObject json = new JSONObject();
    json.put("boxId", null);
    
    final JSONObject response = boxControllerHelperService.exportBoxContentsForm(null, json);
    
    verify(misoFileManager, never()).getNewFile(any(Class.class), any(String.class), any(String.class));
    
    assertEquals("Missing+boxId", response.get("error"));
  }
  
  @Test(expected=Exception.class)
  public final void testExportBoxContentsFormError() throws Exception {
    final long id = 1L;
    final Exception error = new Exception("thrown by mock");
    ArrayList<String> array = new ArrayList<String>();
    array.add("a:a:a");
    array.add("b:b:b");
    array.add("c:c:c");
    when(requestManager.getBoxById(anyLong())).thenThrow(error);
    
    final JSONObject json = new JSONObject();
    json.put("boxId", id);
    
    boxControllerHelperService.exportBoxContentsForm(null, json);
  }
}
