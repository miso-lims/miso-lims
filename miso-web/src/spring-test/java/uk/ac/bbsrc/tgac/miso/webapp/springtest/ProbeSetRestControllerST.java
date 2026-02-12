package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSetProbe;
import uk.ac.bbsrc.tgac.miso.dto.ProbeDto;
import uk.ac.bbsrc.tgac.miso.dto.ProbeSetDto;

import java.util.*;

public class ProbeSetRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/probesets";

  @Test
  public void testQuery() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE, "Test", Collections.singletonList(1));
  }

  @Test
  public void testCreate() throws Exception {
    ProbeSetDto dto = new ProbeSetDto();
    dto.setName("New Set");

    ProbeSet probeSet = baseTestCreate(CONTROLLER_BASE, dto, ProbeSet.class, 201);
    assertNotNull(probeSet);
    assertEquals(dto.getName(), probeSet.getName());
  }

  @Test
  public void testUpdate() throws Exception {
    ProbeSetDto dto = ProbeSetDto.from(currentSession().get(ProbeSet.class, 1L));
    dto.setName("Updated set name");
    List<ProbeDto> probeDtos = new ArrayList<>(dto.getProbes());
    probeDtos.removeIf(probe -> Objects.equals("sp2", probe.getIdentifier()));
    dto.setProbes(probeDtos);

    ProbeSet updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, ProbeSet.class);
    assertEquals(dto.getName(), updated.getName());
    assertEquals(1, updated.getProbes().size());
    assertFalse(updated.getProbes().stream().anyMatch(probe -> Objects.equals("sp2", probe.getName())));
  }

  @Test
  public void testBulkDelete() throws Exception {
    testBulkDelete(ProbeSet.class, 1, CONTROLLER_BASE);
  }

  @Test
  public void testUpdateProbes() throws Exception {
    ProbeSetDto dto = ProbeSetDto.from(currentSession().get(ProbeSet.class, 1L));
    List<ProbeDto> probeDtos = new ArrayList<>(dto.getProbes());
    probeDtos.remove(0);
    ProbeDto updateProbe = probeDtos.get(0);
    updateProbe.setName("Updated");
    ProbeDto newProbe = new ProbeDto();
    newProbe.setIdentifier("asdf");
    newProbe.setName("New");
    newProbe.setRead("R2");
    newProbe.setPattern("5PNNNNNNNNNN(BC)");
    newProbe.setSequence("AAAACCCCGGGGTTT");
    newProbe.setFeatureType("ANTIBODY_CAPTURE");
    probeDtos.add(newProbe);

    String submitUrl = CONTROLLER_BASE + "/1/probes";
    String pollUrl = CONTROLLER_BASE + "/bulk";
    String response = pollingResponserHelper("put", probeDtos, submitUrl, pollUrl, 202);
    assertEquals("completed", JsonPath.read(response, "$.status"));

    ProbeSet updatedSet = currentSession().get(ProbeSet.class, 1L);
    Set<ProbeSetProbe> updatedProbes = updatedSet.getProbes();
    assertEquals(2, updatedProbes.size());
    assertTrue(updatedProbes.stream().anyMatch(probe -> Objects.equals("Updated", probe.getName())));
    ProbeSetProbe created =
        updatedProbes.stream().filter(probe -> Objects.equals("New", probe.getName())).findAny().orElse(null);
    assertNotNull(created);
    assertEquals(newProbe.getIdentifier(), created.getIdentifier());
    assertEquals(newProbe.getName(), created.getName());
    assertEquals(newProbe.getRead(), created.getRead().name());
    assertEquals(newProbe.getPattern(), created.getPattern());
    assertEquals(newProbe.getSequence(), created.getSequence());
    assertEquals(newProbe.getFeatureType(), created.getFeatureType().name());
  }
}
