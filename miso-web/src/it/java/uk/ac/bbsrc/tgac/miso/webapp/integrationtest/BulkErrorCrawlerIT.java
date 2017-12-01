package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractPage;

public class BulkErrorCrawlerIT extends AbstractIT {
  
  private static final Set<String> urlSlugs;
  static {
    Set<String> slugs = new HashSet<>();
    slugs.add("admin/users");
    slugs.add("admin/groups");
    slugs.add("mainMenu");
    slugs.add("myAccount");
    slugs.add("projects");
    slugs.add("samples");
    slugs.add("libraries");
    slugs.add("dilutions");
    slugs.add("pools");
    slugs.add("poolorders");
    slugs.add("containers");
    slugs.add("runs");
    slugs.add("boxes");
    slugs.add("sequencers");
    slugs.add("kitdescriptors");
    slugs.add("indices");
    slugs.add("studies");
    slugs.add("printers");
    slugs.add("experiments");
    slugs.add("submissions");

    slugs.add("project/1");
    slugs.add("sample/1");
    slugs.add("sample/2");
    slugs.add("sample/3");
    slugs.add("sample/4");
    slugs.add("sample/5");
    slugs.add("sample/6");
    slugs.add("sample/7");
    slugs.add("sample/8");
    slugs.add("sample/9");
    slugs.add("sample/10");
    slugs.add("sample/11");
    slugs.add("sample/12");
    slugs.add("sample/13");
    slugs.add("sample/14");
    slugs.add("library/1");
    slugs.add("pool/1");
    slugs.add("container/1"); // Illumina
    slugs.add("container/2"); // PacBio (empty)
    slugs.add("run/1"); // Illumina
    slugs.add("run/2"); // PacBio
    slugs.add("box/1");
    slugs.add("kitdescriptor/1");
    slugs.add("study/1");
    slugs.add("sample/receipt");
    slugs.add("importexport");
    urlSlugs = Collections.unmodifiableSet(slugs);
  }

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testForStacktracesOnPages() {
    String errors = urlSlugs.stream()
        .filter(slug -> AbstractPage.checkForErrors(getDriver(), getBaseUrl(), slug))
        .collect(Collectors.joining("\n"));
    if (!LimsUtils.isStringEmptyOrNull(errors)) throw new IllegalArgumentException("Errors on page(s): " + errors);
  }

}
