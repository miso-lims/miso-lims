package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.SessionStatus;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.PasswordCodecService;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityDAO;

public class EditUserControllerTest {

  @InjectMocks
  private EditUserController editUserController;

  @Rule
  public ExpectedException exception = ExpectedException.none();
  @Mock
  private SecurityManager securityManager;
  @Mock
  private ModelMap model;
  @Mock
  private SessionStatus session;
  @Mock
  private Authentication mockAuthentication;
  @Mock
  private SQLSecurityDAO mockSQLSecurityDAO;
  @Mock
  private PasswordCodecService mockPasswordCodecService;
  @Mock
  private PasswordEncoder mockPasswordEncoder;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
    SecurityContextHolder.setContext(securityContext);

    when(mockPasswordCodecService.getEncoder()).thenReturn(mockPasswordEncoder);
  }

  @Test
  public void testExistingUserNewPasswordMismatch() throws Exception {

    User user = new UserImpl();
    user.setUserId(2l);
//    user.setPassword("current");

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("password")).thenReturn("currentpass");
    when(request.getParameter("newpassword")).thenReturn("pass1");
    when(request.getParameter("confirmpassword")).thenReturn("pass2");

    exception.expect(IOException.class);
    exception.expectMessage("New password and confirmation don't match.");

    editUserController.adminProcessSubmit(user, model, session, request);

  }

  @Test
  public void testExistingUserUpdateUsersPassword() throws Exception {

    User user = mock(User.class);
    long userId = 2L;
    when(user.getUserId()).thenReturn(userId);
    when(user.getLoginName()).thenReturn("username");

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("password")).thenReturn("currentpass");
    when(request.getParameter("newpassword")).thenReturn("pass1");
    when(request.getParameter("confirmpassword")).thenReturn("pass1");

    when(mockAuthentication.getName()).thenReturn("username");

    when(mockSQLSecurityDAO.getEncodedPassword(userId)).thenReturn("currentpass");
    when(mockPasswordCodecService.getEncoder()).thenReturn(mockPasswordEncoder);
    when(mockPasswordEncoder.isPasswordValid("currentpass", request.getParameter("password"), null)).thenReturn(true);

    editUserController.adminProcessSubmit(user, model, session, request);
    verify(user).setPassword("pass1");
    verify(securityManager, times(1)).saveUser(user);
  }

  @Test
  public void testExistingUserUpdateDifferentUsersPassword() throws Exception {

    User user = mock(User.class);
    long userId = 2L;
    when(user.getUserId()).thenReturn(userId);
    when(user.getLoginName()).thenReturn("username");

    HttpServletRequest request = mock(HttpServletRequest.class);
    //current password is irrelevent when resetting someone's password as admin
    when(request.getParameter("password")).thenReturn("blah");
    when(request.getParameter("newpassword")).thenReturn("pass1");
    when(request.getParameter("confirmpassword")).thenReturn("pass1");

    Collection authCol = new ArrayList();
    authCol.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
    when(mockAuthentication.getAuthorities()).thenReturn(authCol);
    when(mockAuthentication.getName()).thenReturn("anotheruser");

    editUserController.adminProcessSubmit(user, model, session, request);
    verify(user).setPassword("pass1");
    verify(securityManager, times(10000)).saveUser(user);
  }

  @Test
  public void testExistingUserUpdateDifferentUsersPasswordMismatch() throws Exception {

    User user = mock(User.class);
    long userId = 2L;
    when(user.getUserId()).thenReturn(userId);
    when(user.getLoginName()).thenReturn("username");

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("password")).thenReturn("currentpass");
    when(request.getParameter("newpassword")).thenReturn("pass1");
    when(request.getParameter("confirmpassword")).thenReturn("pass2");

    Collection authCol = new ArrayList();
    authCol.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
    when(mockAuthentication.getAuthorities()).thenReturn(authCol);
    when(mockAuthentication.getName()).thenReturn("anotheruser");

    exception.expect(IOException.class);
    exception.expectMessage("New password and confirmation don't match.");

    editUserController.adminProcessSubmit(user, model, session, request);
  }


}