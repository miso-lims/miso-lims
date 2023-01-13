var Contacts = (function ($) {
  return {
    makeContactLabel: function (name, email) {
      return name + " <" + email + ">";
    },

    selectContactDialog: function (includeInternal, showSaveOption, callback) {
      var options = [
        {
          name: "Search contacts",
          handler: function () {
            searchContacts(showSaveOption, callback);
          },
        },
        {
          name: "New contact",
          handler: function () {
            newContact(showSaveOption, callback);
          },
        },
      ];

      if (includeInternal) {
        options.unshift({
          name: "Search internal users",
          handler: function () {
            searchInternal(showSaveOption, callback);
          },
        });
      }

      Utils.showWizardDialog("Change Contact", options);
    },
  };

  function searchInternal(showSaveOption, callback) {
    Utils.showDialog(
      "Search Internal Users",
      "Search",
      [
        {
          label: "Name",
          property: "q",
          type: "text",
          required: true,
        },
      ],
      function (results) {
        doSearch(Urls.rest.users.search, results.q, showSaveOption, callback);
      }
    );
  }

  function searchContacts(showSaveOption, callback) {
    Utils.showDialog(
      "Search Contacts",
      "Search",
      [
        {
          label: "Name",
          property: "q",
          type: "text",
          required: true,
        },
      ],
      function (results) {
        doSearch(Urls.rest.contacts.search, results.q, showSaveOption, callback);
      }
    );
  }

  function doSearch(url, query, showSaveOption, callback) {
    Utils.ajaxWithDialog(
      "Searching",
      "GET",
      url +
        "?" +
        Utils.page.param({
          q: query,
        }),
      null,
      function (data) {
        var options = data.map(function (item) {
          return {
            name: Contacts.makeContactLabel(item.name, item.email),
            handler: function () {
              callback(item);
            },
          };
        });
        options.push({
          name: "New contact",
          handler: function () {
            newContact(showSaveOption, callback);
          },
        });
        Utils.showWizardDialog(options.length > 1 ? "Select Contact" : "No Results", options);
      }
    );
  }

  function newContact(showSaveOption, callback) {
    var fields = [
      {
        label: "Name",
        property: "name",
        type: "text",
        required: true,
      },
      {
        label: "Email",
        property: "email",
        type: "text",
        regex: Utils.validation.emailRegex,
        required: true,
      },
    ];

    if (showSaveOption) {
      fields.push({
        label: "Save contact",
        property: "save",
        type: "checkbox",
      });
    }

    Utils.showDialog("New Contact", "Continue", fields, callback);
  }
})(jQuery);
