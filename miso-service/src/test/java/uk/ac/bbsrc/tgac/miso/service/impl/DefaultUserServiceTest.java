package uk.ac.bbsrc.tgac.miso.service.impl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;

import static org.junit.Assert.*;

public class DefaultUserServiceTest {

  private static final String ENCODED = "ENCODED";

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SecurityManager securityManager;

  @InjectMocks
  private DefaultUserService sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(ENCODED);
    Mockito.when(securityManager.isPasswordMutable()).thenReturn(true);
  }

  @Test
  public void testValidateAndEncodePasswordTooShort() {
    try {
      sut.validateAndEncodePassword("$H0rtP4sS", true);
      fail("Expected ValidationException");
    } catch (ValidationException exception) {
      assertTrue(exception.getErrors().stream().map(ValidationError::getMessage)
          .anyMatch("Must be at least 15 characters long"::equals));
    }
  }

  @Test
  public void testValidateAndEncodePasswordNotComplex() {
    try {
      sut.validateAndEncodePassword("lackscomplexity", true);
      fail("Expected ValidationException");
    } catch (ValidationException exception) {
      assertTrue(exception.getErrors().stream().map(ValidationError::getMessage)
          .anyMatch(message -> message.startsWith("Must contain at least 3")));
    }
  }

  @Test
  public void testValidateAndEncodePasswordGood() {
    String password = sut.validateAndEncodePassword("P@ssw0rd1SF1ft33n", true);
    assertNotNull(password);
    assertEquals(ENCODED, password);
  }

}
