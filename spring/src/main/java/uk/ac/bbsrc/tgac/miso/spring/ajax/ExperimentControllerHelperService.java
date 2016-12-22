/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.spring.ajax.TableHelper;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class ExperimentControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ExperimentControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public JSONObject lookupKitByIdentificationBarcode(HttpSession session, JSONObject json) {
    try {
      if (json.has("barcode")) {
        String barcode = json.getString("barcode");
        KitComponent kit = requestManager.getKitComponentByIdentificationBarcode(barcode);
        if (kit != null) {
          return JSONUtils.SimpleJSONResponse(kit.toString());
        } else {
          // new kit?
          Pattern ls454KitPattern = Pattern.compile("([\\d]{11})([\\d]{8})([\\d]{6})"); // 05365473001 93765920 102010
          Pattern illuminaKitPattern = Pattern.compile("([A-Z0-9]{3}-[\\d]{7})"); // RGT-0520823 - outer kit barcode // 15003926 -
                                                                                  // partNumber // 5454482 - lotNumber
          Pattern solidKitPattern = Pattern.compile("foo"); // 05365473001 93765920 102010

          if (ls454KitPattern.matcher(barcode).matches()) {
            return JSONUtils.SimpleJSONResponse("Looks like a 454 kit");
          } else if (illuminaKitPattern.matcher(barcode).matches()) {
            return JSONUtils.SimpleJSONResponse("Looks like an Illumina kit");
          } else if (solidKitPattern.matcher(barcode).matches()) {
            return JSONUtils.SimpleJSONResponse("Looks like a SOLiD kit");
          } else {
            return JSONUtils.SimpleJSONError("Unrecognised barcode");
          }
        }
      }
    } catch (Exception e) {
      log.debug("Failed to lookup kit: ", e);
      return JSONUtils.SimpleJSONError("Failed to lookup kit");
    }
    return JSONUtils.SimpleJSONError("Cannot process kit barcode");
  }

  public JSONObject lookupKitByLotNumber(HttpSession session, JSONObject json) {
    try {
      if (json.has("lotNumber")) {
        String lotNumber = json.getString("lotNumber");
        KitComponent kit = requestManager.getKitByLotNumber(lotNumber);
        if (kit != null) {
          return JSONUtils.SimpleJSONResponse(kit.toString());
        } else {
          Pattern ls454KitPattern = Pattern.compile("([\\d]{11})([\\d]{8})([\\d]{6})"); // 05365473001 93765920 102010
          Matcher ls454Matcher = ls454KitPattern.matcher(lotNumber);
          Pattern illuminaKitPattern = Pattern.compile("([0-9]{7})"); // 5454482 - lotNumber
          Matcher illuminaMatcher = illuminaKitPattern.matcher(lotNumber);
          Pattern solidKitPattern = Pattern.compile("foo"); // 05365473001 93765920 102010
          Matcher solidMatcher = solidKitPattern.matcher(lotNumber);

          if (ls454Matcher.matches()) {
            log.info("Looks like a 454 kit - getting lot number");
            lotNumber = ls454Matcher.group(2);
          } else if (illuminaMatcher.matches()) {
            log.info("Looks like an Illumina kit - getting lot number");
            lotNumber = illuminaMatcher.group(1);
          } else if (solidMatcher.matches()) {
            log.info("Looks like a SOLiD kit - getting lot number");
            lotNumber = solidMatcher.group(1);
          } else {
            return JSONUtils.SimpleJSONError("Unrecognised barcode");
          }
        }
      }
    } catch (Exception e) {
      log.debug("Failed to lookup kit: ", e);
      return JSONUtils.SimpleJSONError("Failed to lookup kit");
    }
    return JSONUtils.SimpleJSONError("Cannot process kit barcode");
  }

  public JSONObject lookupKitDescriptorByPartNumber(HttpSession session, JSONObject json) {
    try {
      if (json.has("partNumber")) {
        String partNumber = json.getString("partNumber");

        Pattern fullLs454KitPattern = Pattern.compile("([\\d]{11})([\\d]{8})([\\d]{6})"); // 05365473001 93765920 102010
        Matcher fullLs454Matcher = fullLs454KitPattern.matcher(partNumber);

        Pattern ls454KitPattern = Pattern.compile("([\\d]{11})"); // 05365473001 93765920 102010
        Matcher ls454Matcher = ls454KitPattern.matcher(partNumber);

        Pattern illuminaKitPattern = Pattern.compile("([0-9]{8})"); // 15003926 - partNumber
        Matcher illuminaMatcher = illuminaKitPattern.matcher(partNumber);

        Pattern solidKitPattern = Pattern.compile("foo"); // 05365473001 93765920 102010
        Matcher solidMatcher = solidKitPattern.matcher(partNumber);

        if (fullLs454Matcher.matches()) {
          log.info("Looks like a 454 kit - getting part number");
          partNumber = fullLs454Matcher.group(1);
        } else if (ls454Matcher.matches()) {
          log.info("Looks like an 454 kit - getting part number");
          partNumber = ls454Matcher.group(1);
        } else if (illuminaMatcher.matches()) {
          log.info("Looks like an Illumina kit - getting part number");
          partNumber = illuminaMatcher.group(1);
        } else if (solidMatcher.matches()) {
          log.info("Looks like a SOLiD kit - getting part number");
          partNumber = solidMatcher.group(1);
        } else {
          return null;
        }

        KitDescriptor kitDescriptor = requestManager.getKitDescriptorByPartNumber(partNumber);
        if (kitDescriptor != null) {
          Map<String, Object> m = new HashMap<>();
          m.put("id", kitDescriptor.getId());
          m.put("name", kitDescriptor.getName());
          return JSONUtils.JSONObjectResponse(m);
        }
      }
    } catch (Exception e) {
      log.debug("Failed to lookup kit: ", e);
      return JSONUtils.SimpleJSONError("Failed to lookup kit");
    }
    return JSONUtils.SimpleJSONError("Cannot process kit barcode");
  }

  // library
  public JSONObject getLibraryKitDescriptors(HttpSession session, JSONObject json) {
    try {
      if (json.has("experimentId")) {
        String experimentId = json.getString("experimentId");
        String multiplexed = json.getString("multiplexed");
        Experiment e = requestManager.getExperimentById(new Long(experimentId));

        Collection<KitDescriptor> kits = requestManager.listKitDescriptorsByType(KitType.LIBRARY);
        StringBuilder lkits = new StringBuilder();
        lkits.append("[");
        int count = 0;
        for (KitDescriptor k : kits) {
          if (e.getPlatform().getPlatformType().equals(k.getPlatformType())) {
            lkits.append("{'name':'" + k.getName() + "', 'id':'" + k.getId() + "', 'partNumber':'" + k.getPartNumber() + "'}");
            if (count < kits.size()) lkits.append(",");
            count++;
          }
        }
        lkits.append("]");

        StringBuilder mkits = null;
        if (multiplexed.equals("true")) {
          mkits = new StringBuilder();
          Collection<KitDescriptor> mkitds = requestManager.listKitDescriptorsByType(KitType.MULTIPLEXING);
          mkits.append("[");
          count = 0;
          for (KitDescriptor k : mkitds) {
            if (e.getPlatform().getPlatformType().equals(k.getPlatformType())) {
              mkits.append(
                  "{'name':'" + k.getName() + "', 'id':'" + k.getId() + "', 'partNumber':'" + k.getPartNumber() + "'}");
              if (count < mkitds.size()) mkits.append(",");
              count++;
            }
          }
          mkits.append("]");
        }

        Map<String, Object> m = new HashMap<>();
        m.put("experimentId", experimentId);
        m.put("multiplexed", multiplexed);
        m.put("libraryKitDescriptors", JSONArray.fromObject(lkits.toString()));
        if (mkits != null) {
          m.put("multiplexKitDescriptors", JSONArray.fromObject(mkits.toString()));
        }
        return JSONUtils.JSONObjectResponse(m);
      }
    } catch (Exception e) {
      log.debug("Failed to generate kit selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate kit selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select library kits");
  }

  public JSONObject addLibraryKit(HttpSession session, JSONObject json) {
    // TODO CS : Implement
    // try {
    // if (json.has("experimentId")) {
    // String experimentId = json.getString("experimentId");
    // String kitDescriptor = json.getString("kitDescriptor");
    // String lotNumber = json.getString("lotNumber");
    //
    // LibraryKit lk = new LibraryKit();
    // KitDescriptor kd = requestManager.getKitDescriptorById(new Long(kitDescriptor));
    // lk.setKitDescriptor(kd);
    // lk.setLotNumber(lotNumber);
    // if (!json.has("kitDate") || isStringEmptyOrNull(json.getString("kitDate"))) {
    // lk.setKitDate(new Date());
    // }
    //
    // Experiment e = requestManager.getExperimentById(new Long(experimentId));
    // e.addKitComponent(lk);
    // e.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
    // requestManager.saveExperiment(e);
    // Integer newStock = kd.getStockLevel() - 1;
    // kd.setStockLevel(newStock);
    // requestManager.saveKitDescriptor(kd);
    // }
    // return JSONUtils.SimpleJSONResponse("Saved kit!");
    // } catch (IOException e) {
    // log.error("add library kit", e);
    // return JSONUtils.SimpleJSONError("Failed to save library kit");
    // }
    return JSONUtils.SimpleJSONError("Not Implemented");
  }

  // empcr
  public JSONObject getEmPcrKitDescriptors(HttpSession session, JSONObject json) {
    try {
      if (json.has("experimentId")) {
        String experimentId = json.getString("experimentId");
        Experiment e = requestManager.getExperimentById(new Long(experimentId));

        Collection<KitDescriptor> kits = requestManager.listKitDescriptorsByType(KitType.EMPCR);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int count = 0;
        for (KitDescriptor k : kits) {
          if (e.getPlatform().getPlatformType().equals(k.getPlatformType())) {
            sb.append("{'name':'" + k.getName() + "', 'id':'" + k.getId() + "', 'partNumber':'" + k.getPartNumber() + "'}");
            if (count < kits.size()) sb.append(",");
            count++;
          }
        }
        sb.append("]");

        Map<String, Object> m = new HashMap<>();
        m.put("experimentId", experimentId);
        m.put("emPcrKitDescriptors", JSONArray.fromObject(sb.toString()));
        return JSONUtils.JSONObjectResponse(m);
      }
    } catch (Exception e) {
      log.debug("Failed to generate kit selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate kit selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select EmPCR kits");
  }

  public JSONObject addEmPcrKit(HttpSession session, JSONObject json) {
    // TODO CS : Implement
    // try {
    // if (json.has("experimentId")) {
    // String experimentId = json.getString("experimentId");
    // String kitDescriptor = json.getString("kitDescriptor");
    // String lotNumber = json.getString("lotNumber");
    //
    // EmPcrKit lk = new EmPcrKit();
    // KitDescriptor kd = requestManager.getKitDescriptorById(new Long(kitDescriptor));
    // lk.setKitDescriptor(kd);
    // lk.setLotNumber(lotNumber);
    // if (!json.has("kitDate") || isStringEmptyOrNull(json.getString("kitDate"))) {
    // lk.setKitDate(new Date());
    // }
    //
    // Experiment e = requestManager.getExperimentById(new Long(experimentId));
    // e.addKitComponent(lk);
    // e.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
    // requestManager.saveExperiment(e);
    // Integer newStock = kd.getStockLevel() - 1;
    // kd.setStockLevel(newStock);
    // requestManager.saveKitDescriptor(kd);
    // }
    // return JSONUtils.SimpleJSONResponse("Saved kit!");
    // } catch (IOException e) {
    // log.error("add EmPCR kit", e);
    // return JSONUtils.SimpleJSONError("Failed to save EmPCR kit");
    // }
    return JSONUtils.SimpleJSONError("Not implemented");

  }

  // clustering
  public JSONObject getClusteringKitDescriptors(HttpSession session, JSONObject json) {
    try {
      if (json.has("experimentId")) {
        String experimentId = json.getString("experimentId");
        Experiment e = requestManager.getExperimentById(new Long(experimentId));

        Collection<KitDescriptor> kits = requestManager.listKitDescriptorsByType(KitType.CLUSTERING);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int count = 0;
        for (KitDescriptor k : kits) {
          if (e.getPlatform().getPlatformType().equals(k.getPlatformType())) {
            sb.append("{'name':'" + k.getName() + "', 'id':'" + k.getId() + "', 'partNumber':'" + k.getPartNumber() + "'}");
            if (count < kits.size()) sb.append(",");
            count++;
          }
        }
        sb.append("]");

        Map<String, Object> m = new HashMap<>();
        m.put("experimentId", experimentId);
        m.put("clusteringKitDescriptors", JSONArray.fromObject(sb.toString()));
        return JSONUtils.JSONObjectResponse(m);
      }
    } catch (Exception e) {
      log.debug("Failed to generate kit selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate kit selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select clustering kits");
  }

  public JSONObject addClusteringKit(HttpSession session, JSONObject json) {
    // TODO CS : Implement
    // try {
    // if (json.has("experimentId")) {
    // String experimentId = json.getString("experimentId");
    // String kitDescriptor = json.getString("kitDescriptor");
    // String lotNumber = json.getString("lotNumber");
    //
    // ClusterKit lk = new ClusterKit();
    // KitDescriptor kd = requestManager.getKitDescriptorById(new Long(kitDescriptor));
    // lk.setKitDescriptor(kd);
    // lk.setLotNumber(lotNumber);
    // if (!json.has("kitDate") || isStringEmptyOrNull(json.getString("kitDate"))) {
    // lk.setKitDate(new Date());
    // }
    //
    // Experiment e = requestManager.getExperimentById(new Long(experimentId));
    // e.addKitComponent(lk);
    // e.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
    // requestManager.saveExperiment(e);
    // Integer newStock = kd.getStockLevel() - 1;
    // kd.setStockLevel(newStock);
    // requestManager.saveKitDescriptor(kd);
    // }
    // return JSONUtils.SimpleJSONResponse("Saved kit!");
    // } catch (IOException e) {
    // log.error("failed to save clustering kit", e);
    // return JSONUtils.SimpleJSONError("Failed to save clustering kit");
    // }
    return JSONUtils.SimpleJSONError("Not implemented");

  }

  // sequencing
  public JSONObject getSequencingKitDescriptors(HttpSession session, JSONObject json) {
    try {
      if (json.has("experimentId")) {
        String experimentId = json.getString("experimentId");
        Experiment e = requestManager.getExperimentById(new Long(experimentId));

        Collection<KitDescriptor> kits = requestManager.listKitDescriptorsByType(KitType.SEQUENCING);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int count = 0;
        for (KitDescriptor k : kits) {
          if (e.getPlatform().getPlatformType().equals(k.getPlatformType())) {
            sb.append("{'name':'" + k.getName() + "', 'id':'" + k.getId() + "', 'partNumber':'" + k.getPartNumber() + "'}");
            if (count < kits.size()) sb.append(",");
            count++;
          }
        }
        sb.append("]");

        Map<String, Object> m = new HashMap<>();
        m.put("experimentId", experimentId);
        m.put("sequencingKitDescriptors", JSONArray.fromObject(sb.toString()));
        return JSONUtils.JSONObjectResponse(m);
      }
    } catch (Exception e) {
      log.debug("Failed to generate kit selection: ", e);
      return JSONUtils.SimpleJSONError("Failed to generate kit selection");
    }
    return JSONUtils.SimpleJSONError("Cannot select sequencing kits");
  }

  public JSONObject addSequencingKit(HttpSession session, JSONObject json) {
    // TODO CS : Implement
    // try {
    // if (json.has("experimentId")) {
    // String experimentId = json.getString("experimentId");
    // String kitDescriptor = json.getString("kitDescriptor");
    // String lotNumber = json.getString("lotNumber");
    //
    // SequencingKit lk = new SequencingKit();
    // KitDescriptor kd = requestManager.getKitDescriptorById(new Long(kitDescriptor));
    // lk.setKitDescriptor(kd);
    // lk.setLotNumber(lotNumber);
    // if (!json.has("kitDate") || isStringEmptyOrNull(json.getString("kitDate"))) {
    // lk.setKitDate(new Date());
    // }
    //
    // Experiment e = requestManager.getExperimentById(new Long(experimentId));
    // e.addKitComponent(lk);
    // e.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
    // requestManager.saveExperiment(e);
    // Integer newStock = kd.getStockLevel() - 1;
    // kd.setStockLevel(newStock);
    // requestManager.saveKitDescriptor(kd);
    // }
    // return JSONUtils.SimpleJSONResponse("Saved kit!");
    // } catch (IOException e) {
    // log.error("failed to save sequencing kit", e);
    // return JSONUtils.SimpleJSONError("Failed to save sequencing kit");
    // }
    return JSONUtils.SimpleJSONError("Not implemented");

  }

  public JSONObject listExperimentsDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Experiment experiment : requestManager.listAllExperiments()) {
        JSONArray inner = new JSONArray();
        inner.add(TableHelper.hyperLinkify("/miso/experiment/" + experiment.getId(), experiment.getName()));
        inner.add(TableHelper.hyperLinkify("/miso/experiment/" + experiment.getId(), experiment.getAlias()));
        inner.add(experiment.getDescription());
        inner.add(experiment.getPlatform().getPlatformType().getKey() + " " + experiment.getPlatform().getInstrumentModel());

        jsonArray.add(inner);
      }
      j.put("experimentsArray", jsonArray);
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}