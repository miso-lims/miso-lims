package uk.ac.bbsrc.tgac.miso.webapp.springtest;

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

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
        dto.setAlias("New Workset" + java.util.UUID.randomUUID().toString());
        dto.setDescription("Description for new workset");
        dto.setSampleIds(new ArrayList<>());
        dto.setLibraryIds(new ArrayList<>());
        dto.setLibraryAliquotIds(new ArrayList<>());
        return dto;
    }

    @Test
    @WithMockUser(username = "admin", roles = {"INTERNAL"})
    public void testDtAll() throws Exception{
        List<Integer> ids = Arrays.asList(1, 2);
        testDtRequest(CONTROLLER_BASE + "/dt/all", ids);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"INTERNAL"})
    public void testDtMine() throws Exception{
        List<Integer> ids = Arrays.asList(1);
        testDtRequest(CONTROLLER_BASE + "/dt/mine", ids);
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testDtMineDefaultUser() throws Exception{
        List<Integer> ids = Arrays.asList(2);
        testDtRequest(CONTROLLER_BASE + "/dt/mine", ids);
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testDtUnCategorized() throws Exception{
        List<Integer> ids = Arrays.asList(1, 2);
        testDtRequest(CONTROLLER_BASE + "/dt/uncategorized", ids);
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testQueryWorkset() throws Exception{
        List<Integer> ids = Arrays.asList(1, 2);
        testList(CONTROLLER_BASE + "?q=Workset", ids);
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testCreateWorkset() throws Exception{
        WorksetDto dto = makeCreateDto();
        Workset created = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);
        assertTrue(created.getAlias().startsWith("New Workset"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testUpdateWorkset() throws Exception{
        WorksetDto dto = makeCreateDto();
        dto.setId(2L);
        dto.setAlias("Updated Alias");

        baseTestUpdate(CONTROLLER_BASE, dto, 2, entityClass);

        Workset updated = currentSession().get(Workset.class, 2);
        assertEquals("Updated Alias", updated.getAlias());
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void  testAddSamples() throws Exception{
        List<Long> sampleIds = Arrays.asList(1L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(sampleIds)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void  testAddLibraries() throws Exception{
        List<Long> libraryIds = Arrays.asList(1L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(libraryIds)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void  testAddLibraryAliquots() throws Exception{
        List<Long> aliquotIds = Arrays.asList(1L);
        getMockMvc().perform(post(CONTROLLER_BASE + "/2/libraryaliquots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(aliquotIds)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testRemoveSamples() throws Exception{
        List<Long> sampleIds = Arrays.asList(1L);

        getMockMvc().perform(post(CONTROLLER_BASE + "/2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(sampleIds)))
                .andExpect(status().isNoContent());

        getMockMvc().perform(delete(CONTROLLER_BASE + "/2/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(sampleIds)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testRemoveLibraries() throws Exception{
        List<Long> libraryIds = Arrays.asList(1L);

        getMockMvc().perform(post(CONTROLLER_BASE + "/2/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(libraryIds)))
                .andExpect(status().isNoContent());

        getMockMvc().perform(delete(CONTROLLER_BASE + "/2/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(libraryIds)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testRemoveLibraryAliquots() throws Exception{
        List<Long> aliquotIds = Arrays.asList(1L);

        getMockMvc().perform(post(CONTROLLER_BASE + "/2/libraryaliquots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(aliquotIds)))
                .andExpect(status().isNoContent());

        getMockMvc().perform(delete(CONTROLLER_BASE + "/2/libraryaliquots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(aliquotIds)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testBulkDelete() throws Exception {
        WorksetDto dto = makeCreateDto();
        Workset created = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);

        testBulkDelete(entityClass, (int) created.getId(), CONTROLLER_BASE);
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testMergeWorksets() throws Exception {
        Workset w1 = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        Workset w2 = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);

        Map<String,Object> mergeData = new HashMap<>();
        mergeData.put("ids", Arrays.asList(w1.getId(), w2.getId()));
        String alias = "Merged Workset" + java.util.UUID.randomUUID();
        mergeData.put("alias", alias);
        mergeData.put("description", "Merged");

        getMockMvc().perform(post(CONTROLLER_BASE + "/merge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(mergeData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.alias").value(alias));
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testMoveSamples() throws Exception {
        Workset source = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        Workset target = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        List<Long> sampleIds = Arrays.asList(1L);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(sampleIds)))
                .andExpect(status().isNoContent());

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(sampleIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/samples/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testMoveLibraries() throws Exception {
        Workset source = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        Workset target = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        List<Long> libraryIds = Arrays.asList(1L);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(libraryIds)))
                .andExpect(status().isNoContent());

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(libraryIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraries/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testMoveLibraryAliquots() throws Exception {
        Workset source = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        Workset target = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
        List<Long> aliquotIds = Arrays.asList(1L);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraryaliquots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(aliquotIds)))
                .andExpect(status().isNoContent());

        MoveItemsDto dto = new MoveItemsDto();
        dto.setTargetWorksetId(target.getId());
        dto.setItemIds(aliquotIds);

        getMockMvc().perform(post(CONTROLLER_BASE + "/" + source.getId() + "/libraryaliquots/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(makeJson(dto)))
                .andExpect(status().isNoContent());

    }

}