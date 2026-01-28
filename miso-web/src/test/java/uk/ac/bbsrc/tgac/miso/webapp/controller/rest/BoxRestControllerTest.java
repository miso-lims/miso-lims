package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.integration.BoxScanner;
import uk.ac.bbsrc.tgac.miso.integration.visionmate.VisionMateScan;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.BoxRestController.ScanRequest;

public class BoxRestControllerTest {

  @InjectMocks
  private BoxRestController sut;

  @Mock
  private BoxService boxService;

  private final String scannerName = "test scanner";
  @Mock
  private BoxScanner boxScanner;

  @Mock
  private VisionMateScan scan;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Map<String, BoxScanner> scanners = new HashMap<>();
    scanners.put(scannerName, boxScanner);
    sut.setBoxScanners(scanners);
  }

  @Test
  public final void testGetBoxScanWithReadErrorsReturnsError() throws Exception {
    final long id = 1L;

    Box box = makeEmptyBox();
    when(scan.getRowCount()).thenReturn(box.getSize().getRows());
    when(scan.getColumnCount()).thenReturn(box.getSize().getColumns());
    when(boxScanner.getScan()).thenReturn(scan);
    when(scan.getReadErrorPositions()).thenReturn(Arrays.asList("A01"));
    when(boxService.get(1L)).thenReturn(box);

    ScanRequest request = new ScanRequest();
    request.setScannerName(scannerName);
    assertTrue(sut.getBoxScan(1, request).getErrors().size() > 0);
  }

  private Box makeEmptyBox() {
    Box box = new BoxImpl();
    box.setId(1L);
    box.setAlias("box");
    BoxSize size = new BoxSize();
    size.setRows(8);
    size.setColumns(12);
    box.setSize(size);
    BoxUse use = new BoxUse();
    use.setAlias("use");
    box.setUse(use);
    box.setLocationBarcode("freezer");
    User user = new UserImpl();
    user.setId(1L);
    box.setLastModifier(user);
    return box;
  }

}
