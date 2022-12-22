ListTarget.attachmentcategory = {
  name: "Attachment Categories",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'attachment-categories');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function(config, projectId) {
    var actions = BulkTarget.attachmentcategory.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Attachment Categories', 'attachmentcategories',
          Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return [{
      "name": "Add",
      "handler": function() {

        Utils.showDialog('Create Attachment Categories', 'Create', [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          value: 1,
          required: true
        }], function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Attachment Categories', ["That's a peculiar number of attachment categories to create."]);
            return;
          }
          window.location = Urls.ui.attachmentCategories.bulkCreate + '?' + Utils.page.param({
            quantity: result.quantity,
          });
        });
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Alias",
      "mData": "alias",
      "include": true,
      "iSortPriority": 0
    }];
  }
};
