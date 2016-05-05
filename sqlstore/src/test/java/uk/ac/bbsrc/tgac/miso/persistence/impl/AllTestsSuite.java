package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HibernateInstituteDaoTest.class, HibernateLabDaoTest.class, HibernateIdentityDaoTest.class,
    HibernateSampleDaoTest.class, HibernateSampleNumberPerProjectDaoTest.class, HibernateLibraryAdditionalInfoDaoTest.class,
    HiberateTagBarcodeStrategyDaoTest.class })

public class AllTestsSuite {
}
