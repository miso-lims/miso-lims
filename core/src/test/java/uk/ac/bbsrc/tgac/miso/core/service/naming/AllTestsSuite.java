package uk.ac.bbsrc.tgac.miso.core.service.naming;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrLibraryAliasGeneratorTest;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.OicrSampleAliasGeneratorTest;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultLibraryAliasValidatorTest;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.DefaultSampleAliasValidatorTest;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrLibraryAliasValidatorTest;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.OicrSampleAliasValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
    DefaultLibraryAliasValidatorTest.class,
    DefaultSampleAliasValidatorTest.class,
    OicrSampleAliasGeneratorTest.class,
    OicrSampleAliasValidatorTest.class,
    OicrLibraryAliasGeneratorTest.class,
    OicrLibraryAliasValidatorTest.class
})
public class AllTestsSuite {
}
