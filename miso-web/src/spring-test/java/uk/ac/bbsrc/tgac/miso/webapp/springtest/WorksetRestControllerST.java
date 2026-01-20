package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.dto.WorksetDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.WorksetRestController.MoveItemsDto;

public class WorksetRestControllerST extends AbstractST {

    private static final String CONTROLLER_BASE = "/rest/worksets";
    private static final Class<Workset> entityClass = Workset.class;

    private WorksetDto makeCreateDto() {
        WorksetDto dto = new WorksetDto();
        dto.setAlias("New Workset");
        dto.setDescription("Description for new workset");
        dto.setSampleIds(Arrays.asList(1L));
        dto.setLibraryIds(new ArrayList<>());
        dto.setLibraryAliquotIds(new ArrayList<>());
        return dto;
    }

    @Test
    public void testDtAll() throws Exception{
        List<Integer> ids = Arrays.asList(1, 2);
        testDtRequest(CONTROLLER_BASE + "/dt/all", ids);
    }

    @Test
    public void testDtMine() throws Exception{
        List<Integer> ids = Arrays.asList(2);
        testDtRequest(CONTROLLER_BASE + "/dt/mine", ids);
    }

    @Test
    public void testDtUnCategorized() throws Exception{
        List<Integer> ids = Arrays.asList(1, 2);
        testDtRequest(CONTROLLER_BASE + "/dt/uncategorized", ids);
    }

    @Test
    public void testQueryWorkset() throws Exception{
        List<Integer> ids = Arrays.asList(1);
        testList(CONTROLLER_BASE + "?q=Workset one", ids);
    }

    @Test
    public void testCreateWorkset() throws Exception{
        WorksetDto dto = makeCreateDto();
        Workset created = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);
        assertTrue(created.getAlias().startsWith("New Workset"));
        assertEquals(1, created.getWorksetSamples().size());
        assertEquals(1L, created.getWorksetSamples().iterator().next().getItem().getId());
        assertEquals(0, created.getWorksetLibraries().size());
        assertEquals(0, created.getWorksetLibraryAliquots().size());
        assertEquals(0, created.getWorksetPools().size());
        assertEquals(null, created.getCategory());
        assertEquals(null, created.getStage());
    }

    @Test
    public void testUpdateWorkset() throws Exception{
        Workset workset = currentSession().get(Workset.class, 2L);
        WorksetDto dto = Dtos.asDto(workset);
        dto.setAlias("Updated Alias");

        baseTestUpdate(CONTROLLER_BASE, dto, 2, entityClass);

        Workset updated = currentSession().get(Workset.class, 2);
        assertEquals("Updated Alias", updated.getAlias());
    }

    @Test
    public void  testAddSamples() throws Exception{
        List<Long> sampleIds = Arrays.asList(100001L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(sampleIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 2L);
        assertEquals(2, workset.getWorksetSamples().size());
    }

    @Test
    public void  testAddLibraries() throws Exception{
        List<Long> libraryIds = Arrays.asList(100001L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(libraryIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 2L);
        assertEquals(1, workset.getWorksetLibraries().size());
    }

    @Test
    public void  testAddLibraryAliquots() throws Exception{
        List<Long> aliquotIds = Arrays.asList(120001L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/libraryaliquots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(aliquotIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 2L);
        assertEquals(1, workset.getWorksetLibraryAliquots().size());

    }

    @Test
    public void  testAddPools() throws Exception{
        List<Long> poolIds = Arrays.asList(120001L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/pools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(poolIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 2L);
        assertEquals(1, workset.getWorksetPools().size());

    }

    @Test
    public void testRemoveSamples() throws Exception{
        List<Long> sampleIds = Arrays.asList(100001L);

        getMockMvc().perform(delete(CONTROLLER_BASE + "/1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(sampleIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 1L);
        assertEquals(2, workset.getWorksetSamples().size());
    }

    @Test
    public void testRemoveLibraries() throws Exception{
        List<Long> libraryIds = Arrays.asList(100001L);

        getMockMvc().perform(delete(CONTROLLER_BASE + "/1/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(libraryIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 1L);
        assertEquals(2, workset.getWorksetLibraries().size());

    }

    @Test
    public void testRemoveLibraryAliquots() throws Exception{
        List<Long> aliquotIds = Arrays.asList(120001L);

        getMockMvc().perform(delete(CONTROLLER_BASE + "/1/libraryaliquots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(aliquotIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 1L);
        assertEquals(1, workset.getWorksetLibraryAliquots().size());

    }

    @Test
    public void testRemovePools() throws Exception{
        List<Long> poolIds = Arrays.asList(120001L);

        getMockMvc().perform(delete(CONTROLLER_BASE + "/1/pools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(poolIds)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset workset = currentSession().get(Workset.class, 1L);
        assertEquals(1, workset.getWorksetPools().size());

    }

    @Test
    @WithMockUser(username = "user",  password = "user", roles = {"INTERNAL"})
    public void testOwnerBulkDelete() throws Exception {
        testBulkDelete(entityClass, 2, CONTROLLER_BASE);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testAdminBulkDelete() throws Exception {
        testBulkDelete(entityClass, 2, CONTROLLER_BASE);
    }

    @Test
    @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
    public void testFailBulkDelete() throws Exception {
        getMockMvc().perform(delete(CONTROLLER_BASE)
                .param("ids","2"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void testMergeWorksets() throws Exception {

        Workset ws1 = currentSession().get(Workset.class, 1L);
        Workset ws2 = currentSession().get(Workset.class, 2L);
        assertNotNull(ws1);
        assertNotNull(ws2);

        Set<Long> expectedSampleIds = new HashSet<>();
        ws1.getWorksetSamples().forEach(wsSample -> expectedSampleIds.add(wsSample.getItem().getId()));
        ws2.getWorksetSamples().forEach(wsSample -> expectedSampleIds.add(wsSample.getItem().getId()));

        assertFalse("Workset 1 has no samples", ws1.getWorksetSamples().isEmpty());
        assertFalse("Workset 2 has no samples", ws2.getWorksetSamples().isEmpty());

        String alias = "Merged";

        Map<String,Object> mergeData = new HashMap<>();
        mergeData.put("ids", Arrays.asList(1,2));
        mergeData.put("alias", alias);

        MvcResult result = getMockMvc().perform(post(CONTROLLER_BASE + "/merge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(mergeData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.alias").value(alias))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        Integer mergedId = JsonPath.read(body, "$.id");

        Workset merged = currentSession().get(Workset.class, mergedId.longValue());
        assertNotNull(merged);
        assertEquals(alias, merged.getAlias());

        Set<Long> actualSampleIds = new HashSet<>();
        merged.getWorksetSamples().forEach(wsSample -> actualSampleIds.add(wsSample.getItem().getId()));

        assertEquals(expectedSampleIds, actualSampleIds);
    }

    @Test
    public void testMoveSamples() throws Exception {
        Workset source = currentSession().get(Workset.class, 1L);
        Workset target = currentSession().get(Workset.class, 2L);
        List<Long> sampleIds = Arrays.asList(100001L);

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(sampleIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/samples/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset updatedSource = currentSession().get(Workset.class, 1L);
        Workset updatedTarget = currentSession().get(Workset.class, 2L);

        assertEquals(2, updatedSource.getWorksetSamples().size());
        assertEquals(2, updatedTarget.getWorksetSamples().size());

    }

    @Test
    public void testMoveLibraries() throws Exception {
        Workset source = currentSession().get(Workset.class, 1L);
        Workset target = currentSession().get(Workset.class, 2L);
        List<Long> libraryIds = Arrays.asList(100001L);

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(libraryIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraries/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset updatedSource = currentSession().get(Workset.class, 1L);
        Workset updatedTarget = currentSession().get(Workset.class, 2L);

        assertEquals(2, updatedSource.getWorksetLibraries().size());
        assertEquals(1, updatedTarget.getWorksetLibraries().size());

    }

    @Test
    public void testMoveLibraryAliquots() throws Exception {
        Workset source = currentSession().get(Workset.class, 1L);
        Workset target = currentSession().get(Workset.class, 2L);
        List<Long> aliquotIds = Arrays.asList(120001L);

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(aliquotIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraryaliquots/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset updatedSource = currentSession().get(Workset.class, 1L);
        Workset updatedTarget = currentSession().get(Workset.class, 2L);

        assertEquals(1, updatedSource.getWorksetLibraryAliquots().size());
        assertEquals(1, updatedTarget.getWorksetLibraryAliquots().size());

    }

    @Test
    public void testMovePools() throws Exception {
        Workset source = currentSession().get(Workset.class, 1L);
        Workset target = currentSession().get(Workset.class, 2L);
        List<Long> poolIds = Arrays.asList(120001L);

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(aliquotIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraryaliquots/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

        currentSession().clear();

        Workset updatedSource = currentSession().get(Workset.class, 1L);
        Workset updatedTarget = currentSession().get(Workset.class, 2L);

        assertEquals(1, updatedSource.getWorksetPools().size());
        assertEquals(1, updatedTarget.getWorksetPools().size());

    }

}