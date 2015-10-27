package uk.ac.bbsrc.tgac.miso.core.service.naming;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14/09/12
 * @since 0.1.8
 */
public interface NamingSchemeAware<T> {
  MisoNamingScheme<T> getNamingScheme();

  void setNamingScheme(MisoNamingScheme<T> namingScheme);
}
