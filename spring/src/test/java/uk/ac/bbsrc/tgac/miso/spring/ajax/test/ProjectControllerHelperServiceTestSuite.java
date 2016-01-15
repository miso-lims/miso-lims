package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.ProjectControllerHelperService;

public class ProjectControllerHelperServiceTestSuite {

  @InjectMocks
  private ProjectControllerHelperService projectControllerHelperService;

  @Mock
  private SecurityManager securityManager;
  @Mock
  private RequestManager requestManager;
  @Mock
  private User user;
  @Mock
  private Project project;
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
  public final void testDeleteProjectFile() throws Exception {
    final String fileName = "file_name";
    final long id = 1L;
    when(project.userCanWrite(any(User.class))).thenReturn(true);
    when(requestManager.getProjectById(anyLong())).thenReturn(project);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    when(misoFileManager.getFileNames(any(Class.class), any(String.class))).thenReturn(new ArrayList<String>(Arrays.asList(fileName)));
    final JSONObject json = new JSONObject();

    json.put("hashcode", fileName.hashCode());
    json.put("id", id);

    final JSONObject response = projectControllerHelperService.deleteProjectFile(null, json);

    verify(misoFileManager).deleteFile(any(Class.class), eq(Long.toString(id)), eq(String.valueOf(fileName)));

    assertEquals("OK", response.get("response"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testDeleteProjectFileNoPermission() throws IOException {
    final String fileName = "file_name";
    final long id = 1L;
    when(project.userCanWrite(any(User.class))).thenReturn(false);
    when(requestManager.getProjectById(anyLong())).thenReturn(project);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Johnny Badhat");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    final JSONObject json = new JSONObject();

    json.put("hashcode", fileName.hashCode());
    json.put("id", id);

    final JSONObject response = projectControllerHelperService.deleteProjectFile(null, json);

    verify(misoFileManager, never()).deleteFile(any(Class.class), any(String.class), any(String.class));

    // JSONUtils.SimpleJSONError adds these plus signs to the response.
    assertEquals("Cannot+delete+file+id+1.++Access+denied.", response.get("error"));
  }

  @Test(expected = JSONException.class)
  @SuppressWarnings("unchecked")
  public final void testDeleteProjectFileBadJson() throws Exception {
    final JSONObject json = new JSONObject();
    json.put("no one expects", "foo");
    json.put("inquisition", 12L);

    projectControllerHelperService.deleteProjectFile(null, json);
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testDeleteProjectFileIOException() throws Exception {
    final String fileName = "file_name";
    final long id = 1L;
    when(project.userCanWrite(any(User.class))).thenReturn(true);
    when(requestManager.getProjectById(anyLong())).thenReturn(project);
    when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
    when(authentication.getName()).thenReturn("Dr Admin");
    final SecurityContextImpl context = new SecurityContextImpl();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
    when(misoFileManager.getFileNames(any(Class.class), any(String.class))).thenReturn(new ArrayList<String>(Arrays.asList(fileName)));
    final JSONObject json = new JSONObject();

    json.put("hashcode", fileName.hashCode());
    json.put("id", id);
    doThrow(new IOException("Controlled Boom")).when(misoFileManager).deleteFile(any(Class.class), eq(Long.toString(id)),
        eq(String.valueOf(fileName)));

    final JSONObject response = projectControllerHelperService.deleteProjectFile(null, json);
    assertEquals("Cannot+remove+file%3A+Controlled+Boom", response.get("error"));
  }

}
