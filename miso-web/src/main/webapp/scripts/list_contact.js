ListTarget.contact = (function ($) {
  return {
    name: "Contacts",
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "contacts");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.contact.getBulkActions(config);
      if (isProjectPage(config)) {
        return [
          {
            name: "Remove",
            action: Project.removeContacts,
          },
        ];
      }
      actions.push(
        ListUtils.createBulkDeleteAction("Contacts", "contacts", function (item) {
          return item.name + " <" + item.email + ">";
        })
      );

      return actions;
    },
    createStaticActions: function (config, projectId) {
      if (isProjectPage(config)) {
        return [
          {
            name: "Add",
            handler: showAddContactDialog,
          },
        ];
      }
      return [ListUtils.createStaticAddAction("Contacts", Urls.ui.contacts.bulkCreate, true)];
    },
    createColumns: function (config, projectId) {
      var columns = [
        {
          sTitle: "Name",
          mData: isProjectPage(config) ? "contactName" : "name",
        },
        {
          sTitle: "Email",
          mData: isProjectPage(config) ? "contactEmail" : "email",
          mRender: function (data, type, full) {
            if (type === "display") {
              return '<a href="mailto:' + data + '">' + data + "</a>";
            }
            return data;
          },
        },
      ];
      if (isProjectPage(config)) {
        columns.push({
          sTitle: "Contact Role",
          mData: "contactRole",
        });
      }
      return columns;
    },
  };

  function showAddContactDialog() {
    Contacts.selectContactDialog(false, false, function (contact) {
      selectContactRoleDialog(contact);
    });
  }

  function selectContactRoleDialog(contact) {
    var byName = Utils.sorting.standardSort("name");
    var contactRoles = Constants.contactRoles.sort(byName);
    Utils.showWizardDialog(
      "Select Contact Role",
      contactRoles.map(function (contactRole) {
        return {
          name: contactRole.name,
          handler: function () {
            // setting contact variable in ProjectContactDto format
            var projectContact = {
              contactId: contact.id,
              contactName: contact.name,
              contactEmail: contact.email,
              contactRoleId: contactRole.id,
              contactRole: contactRole.name,
            };
            Project.addContact(projectContact);
          },
        };
      })
    );
  }

  function isProjectPage(config) {
    return config.projectId != null && config.projectId >= 0;
  }
})(jQuery);
