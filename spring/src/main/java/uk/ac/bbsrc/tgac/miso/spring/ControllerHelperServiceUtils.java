package uk.ac.bbsrc.tgac.miso.spring;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;

/**
 * For when you need web utils, since this module can't access `miso-web`.
 *
 */
public class ControllerHelperServiceUtils {
  private static final Logger log = LoggerFactory.getLogger(ControllerHelperServiceUtils.class);

  public interface BarcodePrintAssister<T extends Barcodable> {

    /**
     * Get all the items associated with a project or throw if this is not reasonable. (Optional method)
     * 
     * @param projectId the project in question
     * @return a collection of assoicated items
     * @throws IOException
     * @throws UnsupportedOperationException if not implemented
     */
    public Iterable<T> fetchAll(long projectId) throws IOException;

    /**
     * Load an item from the database by ID.
     * 
     * @param id
     * @return
     * @throws IOException
     */
    public T fetch(long id) throws IOException;

    /**
     * Write an item to the database having updated the identification barcode.
     * 
     * @param item
     * @throws IOException
     */
    public void store(T item) throws IOException;

    /**
     * The JSON property in the request for the list of objects to fetch.
     */
    public String getGroupName();

    /**
     * The JSON property containing the ID of the object to be fetched.
     */
    public String getIdName();
  }

  /**
   * Returns the location of the identificationBarcode image storage folder
   * 
   * @param HttpSession session
   * @return File
   */
  public static File getBarcodeFileLocation(HttpSession session) {
    return new File(session.getServletContext().getRealPath("/temp/"));
  }

  public static <T extends Barcodable> JSONObject printBarcodes(PrinterService printerService, JSONObject json,
      BarcodePrintAssister<T> assister) {
    try {
      if (!json.has("printerId")) {
        return JSONUtils
            .SimpleJSONError("No printer specified.");
      }

      Printer printer = printerService.get(json.getLong("printerId"));
      if (printer == null) {
        return JSONUtils
            .SimpleJSONError("Printer not found.");
      }

      List<Barcodable> thingsToPrint = new ArrayList<>();

      JSONArray ls = JSONArray.fromObject(json.getString(assister.getGroupName()));
      for (Object o : ls) {
        Long id = ((JSONObject) o).getLong(assister.getIdName());
        T barcodeable = assister.fetch(id);
        // autosave the barcode if none has been previously generated
        if (isStringEmptyOrNull(barcodeable.getIdentificationBarcode())) {
          assister.store(barcodeable);
        }
      }
      printer.printBarcode(thingsToPrint);
      return JSONUtils.SimpleJSONResponse(thingsToPrint.size() + " barcodes printed.");
    } catch (IOException e) {
      log.error("print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }

  }

  public static <T extends Barcodable> JSONObject printAllBarcodes(PrinterService printerService,
      JSONObject json,
      BarcodePrintAssister<T> assister) {
    try {
      if (!json.has("printerId")) {
        return JSONUtils
            .SimpleJSONError("No printer specified.");
      }

      Printer printer = printerService.get(json.getLong("printerId"));
      if (printer == null) {
        return JSONUtils
            .SimpleJSONError("Printer not found.");
      }

      List<Barcodable> thingsToPrint = new ArrayList<>();

      for (T barcodeable : assister.fetchAll(json.getLong("projectId"))) {
        // autosave the barcode if none has been previously generated
        if (isStringEmptyOrNull(barcodeable.getIdentificationBarcode())) {
          assister.store(barcodeable);
        }
      }
      printer.printBarcode(thingsToPrint);
      return JSONUtils.SimpleJSONResponse(thingsToPrint.size() + " barcodes printed.");
    } catch (IOException e) {
      log.error("print barcodes", e);
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }
}
