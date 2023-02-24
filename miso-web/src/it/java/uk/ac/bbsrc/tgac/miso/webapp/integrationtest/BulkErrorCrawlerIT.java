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
        slugs.add("lab/list");
        slugs.add("arraymodel/list");
        slugs.add("partitionqctype/list");
        slugs.add("runlibraryqcstatus/list");
        slugs.add("scientificname/list");
        slugs.add("referencegenome/list");
        slugs.add("pipeline/list");
        slugs.add("studytype/list");
        slugs.add("workstation/list");
        slugs.add("sop/list");
        slugs.add("worksetcategory/list");
        slugs.add("worksetstage/list");
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
        slugs.add("storagelabel/list");
        slugs.add("metric/list");
        slugs.add("assay/list");
        slugs.add("assaytest/list");
        slugs.add("requisition/list");

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
        slugs.add("library/batch/2017-07-24_u1_s3_k1-KITLOTONE");
        slugs.add("assay/new");
        slugs.add("assay/1");
        slugs.add("assay/1?locked=false");
        slugs.add("requisition/new");
        slugs.add("requisition/1");

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
        slugs.add("instrument/101/servicerecord/new");
        slugs.add("instrument/101/servicerecord/150");

        slugs.add("freezer/new");
        slugs.add("freezer/3");
        slugs.add("freezer/3/servicerecord/new");
        slugs.add("freezer/3/servicerecord/153");
        slugs.add("librarytemplate/new");
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
        slugs.add("assaytest/bulk/new?quantity=3");
        slugs.add("box/bulk/new?quantity=5");
        slugs.add("librarytemplate/bulk/new?quantity=3");
        slugs.add("librarytemplate/1/indices/add?quantity=3");
        slugs.add("librarytemplate/1/indices/edit?positions=A01,A02");
        slugs.add("attachmentcategories/bulk/new?quantity=3");
        slugs.add("sampletype/bulk/new?quantity=3");
        slugs.add("sequencingcontroltype/bulk/new?quantity=3");
        slugs.add("librarytype/bulk/new?quantity=3");
        slugs.add("libraryselection/bulk/new?quantity=3");
        slugs.add("librarystrategy/bulk/new?quantity=3");
        slugs.add("libraryspikein/bulk/new?quantity=3");
        slugs.add("targetedsequencing/bulk/new?quantity=3");
        slugs.add("runpurpose/bulk/new?quantity=3");
        slugs.add("sequencingparameters/bulk/new?quantity=3");
        slugs.add("containermodel/bulk/new?quantity=3");
        slugs.add("boxsize/bulk/new?quantity=3");
        slugs.add("boxuse/bulk/new?quantity=3");
        slugs.add("lab/bulk/new?quantity=3");
        slugs.add("arraymodel/bulk/new?quantity=3");
        slugs.add("partitionqctype/bulk/new?quantity=3");
        slugs.add("runlibraryqcstatus/bulk/new?quantity=2");
        slugs.add("scientificname/bulk/new?quantity=3");
        slugs.add("referencegenome/bulk/new?quantity=3");
        slugs.add("studytype/bulk/new?quantity=3");
        slugs.add("workstation/bulk/new?quantity=2");
        slugs.add("worksetcategory/bulk/new?quantity=2");
        slugs.add("worksetstage/bulk/new?quantity=2");

        slugs.add("tissuematerial/bulk/new?quantity=3");
        slugs.add("tissueorigin/bulk/new?quantity=3");
        slugs.add("tissuetype/bulk/new?quantity=3");
        slugs.add("tissuepiecetype/bulk/new?quantity=3");
        slugs.add("samplepurpose/bulk/new?quantity=3");
        slugs.add("subproject/bulk/new?quantity=3");
        slugs.add("stain/bulk/new?quantity=3");
        slugs.add("staincategory/bulk/new?quantity=3");
        slugs.add("detailedqcstatus/bulk/new?quantity=3");
        slugs.add("librarydesigncode/bulk/new?quantity=3");
        slugs.add("librarydesign/bulk/new?quantity=3");
        slugs.add("storagelabel/bulk/new?quantity=3");
        slugs.add("metric/bulk/new?quantity=3");

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
                .put("sample/bulk/new",
                        (driver, baseUrl) -> BulkSamplePage.getForCreate(driver, baseUrl, 5, null, "Aliquot"))
                .put("sample/bulk/edit",
                        (driver, baseUrl) -> BulkSamplePage.getForEdit(driver, baseUrl, Lists.newArrayList(302L, 202L)))
                .put("sample/bulk/propagate",
                        (driver, baseUrl) -> BulkSamplePage.getForPropagate(driver, baseUrl, Arrays.asList(302L, 202L),
                                Arrays.asList(1), "Aliquot"))
                .put("library/bulk/propagate",
                        (driver, baseUrl) -> BulkLibraryPage.getForPropagate(driver, baseUrl, Arrays.asList(304L, 305L),
                                Arrays.asList(1)))
                .put("library/bulk/edit",
                        (driver, baseUrl) -> BulkLibraryPage.getForEdit(driver, baseUrl,
                                Arrays.asList(601L, 602L, 603L, 604L)))
                .put("library/bulk/receive",
                        (driver, baseUrl) -> BulkLibraryPage.getForReceive(driver, baseUrl, 3, null, 15L))
                .put("libraryaliquot/bulk/propagate",
                        (driver, baseUrl) -> BulkLibraryAliquotPage.getForPropagate(driver, baseUrl,
                                Arrays.asList(601L, 602L, 603L)))
                .put("libraryaliquot/bulk/edit",
                        (driver, baseUrl) -> BulkLibraryAliquotPage.getForEdit(driver, baseUrl,
                                Arrays.asList(901L, 902L)))
                .put("libraryaliquot/bulk/repropagate",
                        (driver, baseUrl) -> BulkLibraryAliquotPage.getForRepropagate(driver, baseUrl,
                                Arrays.asList(901L, 902L)))
                .put("qc/bulk/addFrom/Sample",
                        (driver, baseUrl) -> BulkQCPage.getForAddSample(driver, baseUrl, Arrays.asList(2201L, 4447L), 1,
                                1))
                .put("qc/bulk/editFrom/Sample",
                        (driver, baseUrl) -> BulkQCPage.getForEditSample(driver, baseUrl, Arrays.asList(2201L), 1))
                .put("qc/bulk/addFrom/Library",
                        (driver, baseUrl) -> BulkQCPage.getForAddLibrary(driver, baseUrl,
                                Arrays.asList(601L, 602L, 603L), 1, 1))
                .put("qc/bulk/editFrom/Library",
                        (driver, baseUrl) -> BulkQCPage.getForEditLibrary(driver, baseUrl, Arrays.asList(2201L), 1))
                .build());

        postPages = Collections.unmodifiableMap(new MapBuilder<String, Map<String, String>>()
                .put("arraymodel/bulk/edit", unmodifiableMap("ids", "1"))
                .put("assaytest/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("attachmentcategories/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("box/bulk/edit", unmodifiableMap("ids", "500,501"))
                .put("boxuse/bulk/edit", unmodifiableMap("ids", "6,5"))
                .put("boxsize/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("containermodel/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("index/bulk/edit", unmodifiableMap("ids", "1,2,3,4"))
                .put("sampletype/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("sop/bulk/new", unmodifiableMap("quantity", "2"))
                .put("sop/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("stain/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("staincategory/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("studytype/bulk/edit", unmodifiableMap("ids", "2,4"))
                .put("pipeline/bulk/new", unmodifiableMap("quantity", "2"))
                .put("pipeline/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("runlibraryqcstatus/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("worksetcategory/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("worksetstage/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("workstation/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("samplepurpose/bulk/edit", unmodifiableMap("ids", "10,9"))
                .put("scientificname/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("storagelabel/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("sequencingorder/bulk/new", unmodifiableMap("poolIds", "120001,120002"))
                .put("sequencingcontroltype/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("subproject/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("tissuematerial/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("metric/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("tissueorigin/bulk/edit", unmodifiableMap("ids", "4,3"))
                .put("tissuepiecetype/bulk/edit", unmodifiableMap("ids", "1"))
                .put("tissuetype/bulk/edit", unmodifiableMap("ids", "11,7"))
                .put("lab/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("libraryaliquot/bulk/merge", unmodifiableMap("ids", "901,902"))
                .put("libraryaliquot/bulk/pool-separate", unmodifiableMap("ids", "901,902"))
                .put("libraryaliquot/bulk/pool",
                        Collections
                                .unmodifiableMap(new MapBuilder<String, String>().put("ids", "901,902")
                                        .put("quantity", "2").build()))
                .put("librarydesign/bulk/edit", unmodifiableMap("ids", "6,7,8"))
                .put("librarydesigncode/bulk/edit", unmodifiableMap("ids", "3,4,5"))
                .put("libraryselection/bulk/edit", unmodifiableMap("ids", "4,20,26"))
                .put("libraryspikein/bulk/edit", unmodifiableMap("ids", "2,3"))
                .put("librarystrategy/bulk/edit", unmodifiableMap("ids", "1,2,20"))
                .put("librarytemplate/bulk/edit", unmodifiableMap("ids", "1"))
                .put("librarytype/bulk/edit", unmodifiableMap("ids", "28,27,17"))
                .put("partitionqctype/bulk/edit", unmodifiableMap("ids", "2,3"))
                .put("pool/bulk/edit", unmodifiableMap("ids", "801,802"))
                .put("pool/bulk/merge",
                        Collections.unmodifiableMap(
                                new MapBuilder<String, String>().put("ids", "801,802").put("proportions", "1,1")
                                        .build()))
                .put("referencegenome/bulk/edit", unmodifiableMap("ids", "1,2,3"))
                .put("runpurpose/bulk/edit", unmodifiableMap("ids", "2,1"))
                .put("sequencingparameters/bulk/edit", unmodifiableMap("ids", "2,4"))
                .put("targetedsequencing/bulk/edit", unmodifiableMap("ids", "1,2"))
                .put("detailedqcstatus/bulk/edit", unmodifiableMap("ids", "1,2"))
                .build());
    }

    private static Map<String, String> unmodifiableMap(String key, String value) {
        return Collections.unmodifiableMap(new MapBuilder<String, String>().put(key, value).build());
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
