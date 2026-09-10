package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

public enum RequisitionProperty {

  ID("ID") {
    @Override
    public String extract(Requisition requisition) {
      return Long.toString(requisition.getId());
    }
  },

  ALIAS("Alias") {
    @Override
    public String extract(Requisition requisition) {
      return requisition.getAlias();
    }
  },

  ASSAY_ALIAS("Assay Alias") {

    @Override
    public String extract(Requisition requisition) {
      if (requisition.getAssays() == null || requisition.getAssays().isEmpty()) {
        return null;
      }
      return requisition.getAssays().stream()
          .map(Assay::getAlias)
          .collect(Collectors.joining(";"));
    }

  },

  ASSAY_VERSION("Assay Version") {

    @Override
    public String extract(Requisition requisition) {
      if (requisition.getAssays() == null || requisition.getAssays().isEmpty()) {
        return null;
      }
      return requisition.getAssays().stream()
          .map(Assay::getVersion)
          .collect(Collectors.joining(";"));
    }

  },

  DESCRIPTION("Description") {
    @Override
    public String extract(Requisition requisition) {
      return requisition.getDescription();
    }
  },

  CONTACTS("Contacts") {

    @Override
    public String extract(Requisition requisition) {
      if (requisition.getContacts() == null || requisition.getContacts().isEmpty()) {
        return null;
      }
      return requisition.getContacts().stream()
          .map(requisitionContact -> requisitionContact.getContact().getName()
              + " <" + requisitionContact.getContact().getEmail() + ">")
          .distinct()
          .collect(Collectors.joining(", "));
    }

  };

  private final String label;

  private RequisitionProperty(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public abstract String extract(Requisition requisition);
}
