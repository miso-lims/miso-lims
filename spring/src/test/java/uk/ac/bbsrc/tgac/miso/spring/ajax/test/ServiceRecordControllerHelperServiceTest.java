package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;
import uk.ac.bbsrc.tgac.miso.spring.ajax.ServiceRecordControllerHelperService;

public class ServiceRecordControllerHelperServiceTest {
  
  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private SequencerReferenceService sequencerReferenceService;
  @Mock
  private MisoFilesManager misoFileManager;
  
  @InjectMocks
  private ServiceRecordControllerHelperService chs;
  
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void testDeleteServiceRecord() throws IOException {
    setAuthenticatedUser("me", true);
    JSONObject request = new JSONObject();
    request.put("recordId", 1L);
    
    JSONObject result = chs.deleteServiceRecord(null, request);
    assertNotNull(result.get("response"));
    assertNull(result.get("error"));
  }
  
  @Test
  public void testDeleteServiceRecordNonAdmin() throws IOException {
    setAuthenticatedUser("me", false);
    JSONObject request = new JSONObject();
    request.put("recordId", 1L);
    
    JSONObject result = chs.deleteServiceRecord(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  @Test
  public void testDeleteServiceRecordNoId() throws IOException {
    setAuthenticatedUser("me", true);
    JSONObject request = new JSONObject();
    
    JSONObject result = chs.deleteServiceRecord(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  @Test
  public void testDeleteServiceRecordDeleteException() throws IOException {
    setAuthenticatedUser("me", true);
    JSONObject request = new JSONObject();
    request.put("recordId", 1L);
    
    Mockito.doThrow(new IOException()).when(sequencerReferenceService).deleteServiceRecord(Mockito.anyLong());
    
    JSONObject result = chs.deleteServiceRecord(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  @Test
  public void testDeleteServiceRecordUserException() throws IOException {
    final String username = "me";
    setAuthenticatedUser(username, true);
    Mockito.doThrow(new IOException()).when(securityManager).getUserByLoginName(username);
    JSONObject request = new JSONObject();
    request.put("recordId", 1L);
    
    JSONObject result = chs.deleteServiceRecord(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }

  @Test
  public void testDeleteServiceRecordAttachment() throws IOException {
    setAuthenticatedUser("me", true);
    String testFileName = "testfile";
    JSONObject request = new JSONObject();
    request.put("id", 1L);
    request.put("hashcode", testFileName.hashCode());
    
    Mockito.when(misoFileManager.getFileNames(SequencerServiceRecord.class, "1"))
        .thenReturn(Arrays.asList("oneFile", testFileName, "otherFile"));
    
    JSONObject result = chs.deleteServiceRecordAttachment(null, request);
    assertNotNull(result.get("response"));
    assertNull(result.get("error"));
  }
  
  @Test
  public void testDeleteServiceRecordAttachmentNonAdmin() throws IOException {
    setAuthenticatedUser("me", false);
    String testFileName = "testfile";
    JSONObject request = new JSONObject();
    request.put("id", 1L);
    request.put("hashcode", testFileName.hashCode());
    
    Mockito.when(misoFileManager.getFileNames(SequencerServiceRecord.class, "1"))
        .thenReturn(Arrays.asList("oneFile", testFileName, "otherFile"));
    
    JSONObject result = chs.deleteServiceRecordAttachment(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  @Test
  public void testDeleteServiceRecordAttachmentNoFile() throws IOException {
    setAuthenticatedUser("me", true);
    String testFileName = "testfile";
    JSONObject request = new JSONObject();
    request.put("id", 1L);
    request.put("hashcode", testFileName.hashCode());
    
    Mockito.when(misoFileManager.getFileNames(SequencerServiceRecord.class, "1"))
        .thenReturn(Arrays.asList("oneFile", "otherFile"));
    
    JSONObject result = chs.deleteServiceRecordAttachment(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  @Test
  public void testDeleteServiceRecordAttachmentDeleteException() throws IOException {
    setAuthenticatedUser("me", true);
    String testFileName = "testfile";
    JSONObject request = new JSONObject();
    request.put("id", 1L);
    request.put("hashcode", testFileName.hashCode());
    
    Mockito.when(misoFileManager.getFileNames(SequencerServiceRecord.class, "1"))
        .thenReturn(Arrays.asList("oneFile", testFileName, "otherFile"));
    Mockito.doThrow(new IOException()).when(misoFileManager).deleteFile(SequencerServiceRecord.class, "1", testFileName);
    
    JSONObject result = chs.deleteServiceRecordAttachment(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  @Test
  public void testDeleteServiceRecordAttachmentUserException() throws IOException {
    final String username = "me";
    setAuthenticatedUser(username, true);
    Mockito.doThrow(new IOException()).when(securityManager).getUserByLoginName(username);
    String testFileName = "testfile";
    JSONObject request = new JSONObject();
    request.put("id", 1L);
    request.put("hashcode", testFileName.hashCode());
    
    Mockito.when(misoFileManager.getFileNames(SequencerServiceRecord.class, "1"))
        .thenReturn(Arrays.asList("oneFile", testFileName, "otherFile"));
    
    JSONObject result = chs.deleteServiceRecordAttachment(null, request);
    assertNotNull(result.get("error"));
    assertNull(result.get("response"));
  }
  
  private void setAuthenticatedUser(String name, boolean admin) throws IOException {
    User user = Mockito.mock(User.class);
    Mockito.when(user.isAdmin()).thenReturn(admin);
    Mockito.when(securityManager.getUserByLoginName(name)).thenReturn(user);
    
    Authentication auth = Mockito.mock(Authentication.class);
    Mockito.when(auth.getName()).thenReturn(name);
    SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(context);
  }

}
