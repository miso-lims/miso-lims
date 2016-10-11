package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractProject;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleCVSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleDerivedInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleLCMTubeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;

public class MisoTargetUtils {

  private MisoTargetUtils() {
    throw new AssertionError("Unintended instantiation of static utility class");
  }

  public static DataSource makeDataSource(String url, String username, String password) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to load JDBC driver", e);
    }
    BasicDataSource datasource = new BasicDataSource();
    datasource.setDriverClassName("com.mysql.jdbc.Driver");
    datasource.setUrl(url);
    datasource.setUsername(username);
    datasource.setPassword(password);
    return datasource;
  }

  public static SessionFactory makeSessionFactory(DataSource datasource) throws IOException {
    LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
    bean.setDataSource(datasource);
    // TODO: use setPackagesToScan instead after fixing annotations
    // bean.setPackagesToScan("uk.ac.bbsrc.tgac.miso.core.data");
    bean.setAnnotatedClasses(
        new Class[] {
            TissueOriginImpl.class,
            TissueTypeImpl.class,
            SampleClassImpl.class,
            SubprojectImpl.class,
            DetailedQcStatusImpl.class,
            SamplePurposeImpl.class,
            SampleGroupImpl.class,
            TissueMaterialImpl.class,
            SampleAliquotImpl.class,
            SampleStockImpl.class,
            UserImpl.class,
            AbstractSample.class,
            IdentityImpl.class,
            AbstractProject.class,
            PoolOrderImpl.class,
            PoolOrderCompletion.class,
            ProjectImpl.class,
            SampleImpl.class,
            SampleDerivedInfo.class,
            DetailedSampleImpl.class,
            KitDescriptor.class,
            SampleValidRelationshipImpl.class,
            SampleNumberPerProjectImpl.class,
            SampleTissueImpl.class,
            SampleTissueProcessing.class,
            SampleCVSlideImpl.class,
            SampleLCMTubeImpl.class,
            SequencingParametersImpl.class,
            InstituteImpl.class,
            LabImpl.class,
            ReferenceGenomeImpl.class,
            AbstractLibrary.class,
            LibraryAdditionalInfoImpl.class,
            LibraryDesign.class,
            LibraryType.class,
            Index.class,
            IndexFamily.class
        });
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    properties.setProperty("hibernate.show_sql", "false");
    properties.setProperty("hibernate.current_session_context_class", "thread");
    bean.setHibernateProperties(properties);
    bean.afterPropertiesSet();
    return bean.getObject();
  }

}
