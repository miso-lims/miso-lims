package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;

public class DefaultUserServiceTest {

  private AutoCloseable mockito;

  private static final String ENCODED = "ENCODED";

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SecurityManager securityManager;

  @InjectMocks
  private DefaultUserService sut;

  @BeforeEach
  public void setup() {
    mockito = MockitoAnnotations.openMocks(this);
    Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(ENCODED);
    Mockito.when(securityManager.isPasswordMutable()).thenReturn(true);
  }

  @AfterEach
  public void cleanUp() throws Exception {
    mockito.close();
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
