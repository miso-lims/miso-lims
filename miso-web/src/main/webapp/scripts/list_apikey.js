ListTarget.apikey = (function () {
  var TYPE_LABEL = "API Keys";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("qc_integration", "rest-api");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      var actions = [
        ListUtils.createBulkDeleteAction(TYPE_LABEL, "apikeys", function (apikey) {
          return apikey.user.fullName;
        }),
      ];
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return [
        {
          name: "Add",
          handler: function () {
            var fields = [
              {
                label: "Name",
                property: "name",
                type: "text",
                required: true,
              },
            ];
            Utils.showDialog("Create API Key", "Create", fields, function (results) {
              Utils.ajaxWithDialog(
                "Creating API Key",
                "POST",
                Urls.rest.apiKeys.create,
                {
                  name: results.name,
                },
                function (apiKey) {
                  Utils.showOkDialog(
                    "New API Key",
                    [
                      "New API key created. Make sure to save the key below, as it cannot be retrieved later.",
                      "Name: " + apiKey.user.fullName,
                      "Key: " + apiKey.key,
                    ],
                    Utils.page.pageReload
                  );
                }
              );
            });
          },
        },
      ];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Key",
          mData: "key",
        },
        {
          sTitle: "Name",
          mData: "user",
          mRender: function (data, type, full) {
            return data.fullName;
          },
        },
        {
          sTitle: "Creator",
          mData: "creatorName",
        },
        {
          sTitle: "Created",
          mData: "created",
          mRender: ListUtils.render.dateWithTimeTooltip,
        },
      ];
    },
    searchTermSelector: function (searchTerms) {
      return [
        searchTerms["id"],
        searchTerms["requisitionStatus"],
        searchTerms["entered"],
        searchTerms["creator"],
        searchTerms["changed"],
        searchTerms["changedby"],
      ];
    },
  };
})();
