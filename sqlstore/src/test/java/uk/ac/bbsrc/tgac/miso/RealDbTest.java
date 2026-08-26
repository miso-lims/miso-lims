package uk.ac.bbsrc.tgac.miso;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Extend this class to test against your real database. The database connection must be configured in a
 * {@code src/test/resources/real-db-test.properties} file, which requires the following properties.
 * </p>
 * <br>
 * <ul>
 * <li>realdbtest.user</li>
 * <li>realdbtest.password</li>
 * <li>realdbtest.url</li>
 * </ul>
 * <br>
 * <p>
 * This is identical to the flyway.properties file, except replacing "flyway" with "realdbtest". This file is git-ignored as well.
 * </p>
 * <br>
 * <p>
 * Classes extending this one are for temporary testing only, and should never be committed, but consider moving the test methods to a class
 * that extends AbstractDAOTest if they are useful tests to keep.
 * </p>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("/real-db-test-context.xml")
@Transactional
public abstract class RealDbTest {

}
