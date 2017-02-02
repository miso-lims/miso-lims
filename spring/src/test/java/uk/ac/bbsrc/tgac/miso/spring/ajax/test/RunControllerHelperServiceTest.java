package uk.ac.bbsrc.tgac.miso.spring.ajax.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.spring.ajax.RunControllerHelperService;

public class RunControllerHelperServiceTest {

  @InjectMocks
  private RunControllerHelperService runCHS;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public final void testRefactorChamberButtons() throws Exception {
    StringBuilder solidButtonsSB = new StringBuilder();
    solidButtonsSB.append("<input id='chamber1' name='container" + 0 + "Select' onchange='Run.ui.changeSolidChamber(this, " + 0
            + ");' type='radio' value='1'/>1 ");
    solidButtonsSB.append("<input id='chamber2' name='container" + 0 + "Select' onchange='Run.ui.changeSolidChamber(this, " + 0
        + ");' type='radio' value='2'/>2 ");
    solidButtonsSB.append("<input id='chamber4' name='container" + 0 + "Select' onchange='Run.ui.changeSolidChamber(this, " + 0
            + ");' type='radio' value='4'/>4 ");
    solidButtonsSB.append("<input id='chamber8' name='container" + 0 + "Select' onchange='Run.ui.changeSolidChamber(this, " + 0
        + ");' type='radio' value='8'/>8 ");
    solidButtonsSB.append("<input id='chamber16' name='container" + 0 + "Select' onchange='Run.ui.changeSolidChamber(this, " + 0
        + ");' type='radio' value='16'/>16 ");
    String solidString = solidButtonsSB.toString();
    String solidButtonsMethod = runCHS.generateChamberButtons("Solid", 0, 1, 16);
    assertEquals(solidString, solidButtonsMethod);
    
    StringBuilder lsButtonsSB = new StringBuilder();
    lsButtonsSB.append("<input id='chamber1' name='container" + 0 + "Select' onchange='Run.ui.changeLS454Chamber(this, " + 0
        + ");' type='radio' value='1'/>1 ");
    lsButtonsSB.append("<input id='chamber2' name='container" + 0 + "Select' onchange='Run.ui.changeLS454Chamber(this, " + 0
        + ");' type='radio' value='2'/>2 ");
    lsButtonsSB.append("<input id='chamber4' name='container" + 0 + "Select' onchange='Run.ui.changeLS454Chamber(this, " + 0
        + ");' type='radio' value='4'/>4 ");
    lsButtonsSB.append("<input id='chamber8' name='container" + 0 + "Select' onchange='Run.ui.changeLS454Chamber(this, " + 0
        + ");' type='radio' value='8'/>8 ");
    lsButtonsSB.append("<input id='chamber16' name='container" + 0 + "Select' onchange='Run.ui.changeLS454Chamber(this, " + 0
        + ");' type='radio' value='16'/>16 ");
    String lsString = lsButtonsSB.toString();
    String lsButtonsMethod = runCHS.generateChamberButtons("LS454", 0, 1, 16);
    assertEquals(lsString, lsButtonsMethod);
  }

  @Test
  public final void testRefactorRows() throws Exception {
    StringBuilder ib = new StringBuilder();
    ib.append("<tr><td>1 </td><td width='90%'><div id='p_div_" + 0
        + "-0' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[0].pool' partition='" + 0 + "_0'></ul></div></td></tr>");
    ib.append("<tr><td>2 </td><td width='90%'><div id='p_div_" + 0
        + "-1' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[1].pool' partition='" + 0 + "_1'></ul></div></td></tr>");
    ib.append("<tr><td>3 </td><td width='90%'><div id='p_div_" + 0
        + "-2' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[2].pool' partition='" + 0 + "_2'></ul></div></td></tr>");
    ib.append("<tr><td>4 </td><td width='90%'><div id='p_div_" + 0
        + "-3' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[3].pool' partition='" + 0 + "_3'></ul></div></td></tr>");
    ib.append("<tr><td>5 </td><td width='90%'><div id='p_div_" + 0
        + "-4' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[4].pool' partition='" + 0 + "_4'></ul></div></td></tr>");
    ib.append("<tr><td>6 </td><td width='90%'><div id='p_div_" + 0
        + "-5' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[5].pool' partition='" + 0 + "_5'></ul></div></td></tr>");
    ib.append("<tr><td>7 </td><td width='90%'><div id='p_div_" + 0
        + "-6' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[6].pool' partition='" + 0 + "_6'></ul></div></td></tr>");
    ib.append("<tr><td>8 </td><td width='90%'><div id='p_div_" + 0
        + "-7' class='elementListDroppableDiv'><ul class='runPartitionDroppable' bind='sequencerPartitionContainers[" + 0
        + "].partitions[7].pool' partition='" + 0 + "_7'></ul></div></td></tr>");
    String ibRows = ib.toString();
    String ibMethod = runCHS.generateRows(0, 0, 7);
    assertEquals(ibRows, ibMethod);
  }
}
