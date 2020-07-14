package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkQCPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.TestUtils;

public class BulkErrorCrawlerIT extends AbstractIT {
  
  private static final Set<String> urlSlugs;
  private static final Set<String> adminSlugs;
  private static final Map<String, Map<String, String>> postPages;
  private static final Map<String, BiConsumer<WebDriver, String>> factoryPages;

  static {
    Set<String> slugs = new HashSet<>();
    // Misc pages
    slugs.add("mainMenu");
    slugs.add("myAccount");
    slugs.add("tools/indexdistance");
    slugs.add("tools/indexsearch");
    slugs.add("tools/identitysearch");

    // List pages
    slugs.add("projects");
    slugs.add("samples");
    slugs.add("libraries");
    slugs.add("libraryaliquots");
    slugs.add("poolorders");
    slugs.add("pools");
    slugs.add("worksets");
    slugs.add("boxes");
    slugs.add("transfer/list");

    slugs.add("sequencingorders/all");
    slugs.add("sequencingorders/outstanding");
    slugs.add("sequencingorders/in-progress");
    slugs.add("containers");
    slugs.add("runs");
    slugs.add("arrays");
    slugs.add("arrayruns");
    slugs.add("instruments");

    slugs.add("storagelocations");
    slugs.add("locationmap/list");
    slugs.add("librarytemplates");
    slugs.add("kitdescriptors");
    slugs.add("indexfamily/list");
    slugs.add("qctype/list");
    slugs.add("attachmentcategories/list");
    slugs.add("sampletype/list");
    slugs.add("sequencingcontroltype/list");
    slugs.add("librarytype/list");
    slugs.add("libraryselection/list");
    slugs.add("librarystrategy/list");
    slugs.add("libraryspikein/list");
    slugs.add("targetedsequencing/list");
    slugs.add("runpurpose/list");
    slugs.add("sequencingparameters/list");
    slugs.add("containermodel/list");
    slugs.add("instrumentmodel/list");
    slugs.add("boxsize/list");
    slugs.add("boxuse/list");
    slugs.add("institute/list");
    slugs.add("lab/list");
    slugs.add("arraymodel/list");
    slugs.add("partitionqctype/list");
    slugs.add("scientificname/list");
    slugs.add("referencegenome/list");
    slugs.add("studytype/list");
    slugs.add("workstation/list");
    slugs.add("sop/list");
    slugs.add("printers");
    slugs.add("deletions");

    slugs.add("sampleclass/list");
    slugs.add("tissuematerial/list");
    slugs.add("tissueorigin/list");
    slugs.add("tissuetype/list");
    slugs.add("tissuepiecetype/list");
    slugs.add("samplepurpose/list");
    slugs.add("subproject/list");
    slugs.add("stain/list");
    slugs.add("staincategory/list");
    slugs.add("detailedqcstatus/list");
    slugs.add("librarydesigncode/list");
    slugs.add("librarydesign/list");

    slugs.add("studies");
    slugs.add("experiments");
    slugs.add("submissions");

    // Form pages
    slugs.add("project/new");
    slugs.add("project/1");
    slugs.add("sample/1"); // Identity
    slugs.add("sample/2"); // Tissue
    slugs.add("sample/3"); // Slide
    slugs.add("sample/4"); // Curls
    slugs.add("sample/5"); // Tissue Piece
    slugs.add("sample/6"); // DNA Stock
    slugs.add("sample/7"); // RNA Stock
    slugs.add("sample/8"); // DNA aliquot
    slugs.add("sample/9"); // RNA aliquot
    slugs.add("sample/12"); // smRNA
    slugs.add("library/1");
    slugs.add("libraryaliquot/1");
    slugs.add("poolorder/new");
    slugs.add("poolorder/1");
    slugs.add("pool/new");
    slugs.add("pool/1");
    slugs.add("workset/new");
    slugs.add("workset/1");
    slugs.add("box/new");
    slugs.add("box/1");
    slugs.add("transfer/new");
    slugs.add("transfer/1");

    slugs.add("container/new/2");
    slugs.add("container/1"); // Illumina
    slugs.add("container/2"); // PacBio (empty)
    slugs.add("run/new/100");
    slugs.add("run/1"); // Illumina
    slugs.add("run/2"); // PacBio
    slugs.add("array/new");
    slugs.add("array/1");
    slugs.add("arrayrun/new");
    slugs.add("arrayrun/1");
    slugs.add("instrument/1");

    slugs.add("freezer/new");
    slugs.add("freezer/3");
    slugs.add("librarytemplate/1");
    slugs.add("kitdescriptor/new");
    slugs.add("kitdescriptor/1");
    slugs.add("indexfamily/1");
    slugs.add("qctype/102");
    slugs.add("instrumentmodel/1");
    slugs.add("sampleclass/new");
    slugs.add("sampleclass/1");

    slugs.add("study/new");
    slugs.add("study/1");
    slugs.add("experiment/1");
    slugs.add("submission/new?experimentIds=1");
    slugs.add("submission/1");
    slugs.add("user/3");

    // Bulk pages
    slugs.add("libraryaliquot/bulk/merge?ids=901%2C902");
    slugs.add("libraryaliquot/bulk/pool-separate?ids=901%2C902");
    slugs.add("libraryaliquot/bulk/pool?ids=901%2C902&quantity=2");
    slugs.add("box/bulk/new?quantity=5");
    slugs.add("box/bulk/edit?ids=500%2C501");

    slugs.add("librarytemplate/bulk/edit?ids=1");
    slugs.add("index/bulk/edit?ids=1%2C2%2C3%2C4");
    slugs.add("qctype/bulk/edit?ids=102%2C103");
    slugs.add("attachmentcategories/bulk/new?quantity=3");
    slugs.add("attachmentcategories/bulk/edit?ids=1%2C2%2C3");
    slugs.add("sampletype/bulk/new?quantity=3");
    slugs.add("sampletype/bulk/edit?ids=1%2C2");
    slugs.add("sequencingcontroltype/bulk/new?quantity=3");
    slugs.add("sequencingcontroltype/bulk/edit?ids=1%2C2");
    slugs.add("librarytype/bulk/new?quantity=3");
    slugs.add("librarytype/bulk/edit?ids=28%2C27%2C17");
    slugs.add("libraryselection/bulk/new?quantity=3");
    slugs.add("libraryselection/bulk/edit?ids=4%2C20%2C26");
    slugs.add("librarystrategy/bulk/new?quantity=3");
    slugs.add("librarystrategy/bulk/edit?ids=1%2C2%2C20");
    slugs.add("libraryspikein/bulk/new?quantity=3");
    slugs.add("libraryspikein/bulk/edit?ids=2%2C1");
    slugs.add("targetedsequencing/bulk/new?quantity=3");
    slugs.add("targetedsequencing/bulk/edit?ids=1%2C2");
    slugs.add("runpurpose/bulk/new?quantity=3");
    slugs.add("runpurpose/bulk/edit?ids=2%2C1");
    slugs.add("sequencingparameters/bulk/new?quantity=3");
    slugs.add("sequencingparameters/bulk/edit?ids=2%2C4");
    slugs.add("containermodel/bulk/new?quantity=3");
    slugs.add("containermodel/bulk/edit?ids=1%2C2");
    slugs.add("boxsize/bulk/new?quantity=3");
    slugs.add("boxsize/bulk/edit?ids=1%2C2");
    slugs.add("boxuse/bulk/new?quantity=3");
    slugs.add("boxuse/bulk/edit?ids=6%2C5");
    slugs.add("institute/bulk/new?quantity=3");
    slugs.add("institute/bulk/edit?ids=1");
    slugs.add("lab/bulk/new?quantity=3");
    slugs.add("lab/bulk/edit?ids=1%2C2");
    slugs.add("arraymodel/bulk/new?quantity=3");
    slugs.add("arraymodel/bulk/edit?ids=1");
    slugs.add("partitionqctype/bulk/new?quantity=3");
    slugs.add("partitionqctype/bulk/edit?ids=2%2C3");
    slugs.add("scientificname/bulk/new?quantity=3");
    slugs.add("scientificname/bulk/edit?ids=1%2C2%2C3");
    slugs.add("referencegenome/bulk/new?quantity=3");
    slugs.add("referencegenome/bulk/edit?ids=1%2C2%2C3");
    slugs.add("studytype/bulk/new?quantity=3");
    slugs.add("studytype/bulk/edit?ids=2%2C4");
    slugs.add("workstation/bulk/new?quantity=2");
    slugs.add("workstation/bulk/edit?ids=1%2C2");

    slugs.add("tissuematerial/bulk/new?quantity=3");
    slugs.add("tissuematerial/bulk/edit?ids=1%2C2%2C3");
    slugs.add("tissueorigin/bulk/new?quantity=3");
    slugs.add("tissueorigin/bulk/edit?ids=4%2C3");
    slugs.add("tissuetype/bulk/new?quantity=3");
    slugs.add("tissuetype/bulk/edit?ids=11%2C7");
    slugs.add("tissuepiecetype/bulk/new?quantity=3");
    slugs.add("tissuepiecetype/bulk/edit?ids=1");
    slugs.add("samplepurpose/bulk/new?quantity=3");
    slugs.add("samplepurpose/bulk/edit?ids=10%2C9");
    slugs.add("subproject/bulk/new?quantity=3");
    slugs.add("subproject/bulk/edit?ids=1%2C2%2C3");
    slugs.add("stain/bulk/new?quantity=3");
    slugs.add("stain/bulk/edit?ids=1%2C2");
    slugs.add("staincategory/bulk/new?quantity=3");
    slugs.add("staincategory/bulk/edit?ids=1%2C2%2C3");
    slugs.add("detailedqcstatus/bulk/new?quantity=3");
    slugs.add("detailedqcstatus/bulk/edit?ids=1%2C2");
    slugs.add("librarydesigncode/bulk/new?quantity=3");
    slugs.add("librarydesigncode/bulk/edit?ids=8%2C7");
    slugs.add("librarydesign/bulk/new?quantity=3");

    urlSlugs = Collections.unmodifiableSet(slugs);

    // admin-only pages
    Set<String> moreSlugs = new HashSet<>();
    moreSlugs.add("instrumentmodel/new");
    moreSlugs.add("qctype/new");
    moreSlugs.add("indexfamily/new");
    moreSlugs.add("instrument/new");
    moreSlugs.add("admin/users");
    moreSlugs.add("admin/groups");
    moreSlugs.add("admin/user/new");
    moreSlugs.add("admin/user/1");
    moreSlugs.add("admin/group/new");
    moreSlugs.add("admin/group/1");

    adminSlugs = Collections.unmodifiableSet(moreSlugs);

    factoryPages = Collections.unmodifiableMap(new MapBuilder<String, BiConsumer<WebDriver, String>>()
        .put("sample/bulk/new", (driver, baseUrl) -> BulkSamplePage.getForCreate(driver, baseUrl, 5, null, 15L))
        .put("sample/bulk/edit", (driver, baseUrl) -> BulkSamplePage.getForEdit(driver, baseUrl, Lists.newArrayList(302L, 202L)))
        .put("sample/bulk/propagate",
            (driver, baseUrl) -> BulkSamplePage.getForPropagate(driver, baseUrl, Arrays.asList(302L, 202L), Arrays.asList(1), 11L))
        .put("library/bulk/propagate",
            (driver, baseUrl) -> BulkLibraryPage.getForPropagate(driver, baseUrl, Arrays.asList(304L, 305L), Arrays.asList(1)))
        .put("library/bulk/edit", (driver, baseUrl) -> BulkLibraryPage.getForEdit(driver, baseUrl, Arrays.asList(601L, 602L, 603L, 604L)))
        .put("library/bulk/receive", (driver, baseUrl) -> BulkLibraryPage.getForReceive(driver, baseUrl, 3, null, 15L))
        .put("libraryaliquot/bulk/propagate",
            (driver, baseUrl) -> BulkLibraryAliquotPage.getForPropagate(driver, baseUrl, Arrays.asList(601L, 602L, 603L)))
        .put("libraryaliquot/bulk/edit", (driver, baseUrl) -> BulkLibraryAliquotPage.getForEdit(driver, baseUrl, Arrays.asList(901L, 902L)))
        .put("libraryaliquot/bulk/repropagate",
            (driver, baseUrl) -> BulkLibraryAliquotPage.getForRepropagate(driver, baseUrl, Arrays.asList(901L, 902L)))
        .put("qc/bulk/addFrom/Sample", (driver, baseUrl) -> BulkQCPage.getForAddSample(driver, baseUrl, Arrays.asList(2201L, 4447L), 1, 1))
        .put("qc/bulk/editFrom/Sample", (driver, baseUrl) -> BulkQCPage.getForEditSample(driver, baseUrl, Arrays.asList(2201L), 1))
        .put("qc/bulk/addFrom/Library",
            (driver, baseUrl) -> BulkQCPage.getForAddLibrary(driver, baseUrl, Arrays.asList(601L, 602L, 603L), 1, 1))
        .put("qc/bulk/editFrom/Library", (driver, baseUrl) -> BulkQCPage.getForEditLibrary(driver, baseUrl, Arrays.asList(2201L), 1))
        .build());

    postPages = Collections.unmodifiableMap(new MapBuilder<String, Map<String, String>>()
        .put("sop/bulk/new", Collections.unmodifiableMap(new MapBuilder<String, String>().put("quantity", "2").build()))
        .put("sop/bulk/edit", Collections.unmodifiableMap(new MapBuilder<String, String>().put("ids", "1,2").build()))
        .build());
  }

  @Test
  public void testPages() {
    login();
    long errors = urlSlugs.stream()
        .filter(slug -> TestUtils.checkForErrors(getDriver(), getBaseUrl(), slug))
        .count();
    assertEquals(0L, errors);
  }

  @Test
  public void testAdminPages() {
    loginAdmin();
    long errors = adminSlugs.stream()
        .filter(slug -> TestUtils.checkForErrors(getDriver(), getBaseUrl(), slug))
        .count();
    assertEquals(0L, errors);
  }

  @Test
  public void testFactoryPages() {
    login();
    long errors = factoryPages.entrySet().stream()
        .filter(page -> TestUtils.checkForErrors(getDriver(), getBaseUrl(), page.getKey(), page.getValue()))
        .count();
    assertEquals(0L, errors);
  }

  @Test
  public void testPostPages() {
    login();
    long errors = postPages.entrySet().stream()
        .filter(page -> TestUtils.checkForErrors(getDriver(), getBaseUrl(), page.getKey(), page.getValue()))
        .count();
    assertEquals(0L, errors);
  }

}
