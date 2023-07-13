var Project = (function () {
  var assaysListId = "assays_section";
  var contactsListId = "contacts_section";

  var form = null;
  var listConfig = {};

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config;
    },

    setAssays: function (assays) {
      FormUtils.setTableData(ListTarget.assay, listConfig, assaysListId, assays, form);
    },

    getAssays: function () {
      return FormUtils.getTableData(assaysListId);
    },

    addAssay: function (addAssay) {
      var assays = Project.getAssays();
      assays.push(addAssay);
      Project.setAssays(assays);
    },

    removeAssays: function (removeAssays) {
      var assays = Project.getAssays().filter(function (assay) {
        return !removeAssays.some(function (removal) {
          return removal.id === assay.id;
        });
      });
      Project.setAssays(assays);
    },

    setContacts: function (contacts) {
      FormUtils.setTableData(ListTarget.contact, listConfig, contactsListId, contacts, form);
    },

    getContacts: function () {
      return FormUtils.getTableData(contactsListId);
    },

    addContact: function (addContact) {
      var contacts = Project.getContacts();
      contacts.push(addContact);
      Project.setContacts(contacts);
    },

    removeContacts: function (removeContacts) {
      var contacts = Project.getContacts().filter(function (contact) {
        return !removeContacts.some(function (removal) {
          return removal.id === contact.id;
        });
      });
      Project.setContacts(contacts);
    },
  };
})();
