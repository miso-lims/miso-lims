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
      if (config.projectId != null && config.projectId >= 0) {
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
      if (config.projectId != null && config.projectId >= 0) {
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
          mData: "name",
        },
        {
          sTitle: "Email",
          mData: "email",
          mRender: function (data, type, full) {
            if (type === "display") {
              return '<a href="mailto:' + data + '">' + data + "</a>";
            }
            return data;
          },
        },
      ];
      if (config.projectId != null && config.projectId >= 0) {
        columns.push({
          sTitle: "Contact Role",
          mData: function (item, type) {
            console.log(item);
            return item.contactRole.name;
          },
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
    Utils.showWizardDialog(
      "Select Contact Role (Required)",
      Constants.contactRoles.map(function (contactRole) {
        return {
          name: contactRole.name,
          handler: function () {
            contact.contactRole = contactRole;
            Project.addContact(contact);
          },
        };
      })
    );
  }
})(jQuery);
