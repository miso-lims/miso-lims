ListTarget.sampletype = {
  name: "Sample Types",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  headerMessage: {
    text: 'WARNING: Adding or modifying sample types may cause your data to be invalid for ENA submission',
    level: 'important'
  },
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.sampletype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push({
        name: "Delete",
        action: function(items) {
          var lines = ['Are you sure you wish to delete the following sample types? This cannot be undone.'];
          var ids = [];
          jQuery.each(items, function(index, sampletype) {
            lines.push('* ' + sampletype.name);
            ids.push(sampletype.id);
          });
          Utils.showConfirmDialog('Delete Sample Types', 'Delete', lines, function() {
            Utils.ajaxWithDialog('Deleting Sample Types', 'POST', '/miso/rest/sampletypes/bulk-delete', ids, function() {
              Utils.page.pageReload();
            });
          });
        }
      });
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [{
      "name": "Add",
      "handler": function() {

        Utils.showDialog('Create Sample Types', 'Create', [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          required: true,
          value: 1
        }], function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Sample Types', ["That's a peculiar number of sample types to create."]);
            return;
          }
          window.location = '/miso/sampletype/bulk/new?' + jQuery.param({
            quantity: result.quantity,
          });
        });
      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Name",
      "mData": "name",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Archived",
      "mData": "archived",
      "include": true,
      "iSortPriority": 0,
      "mRender": ListUtils.render.booleanChecks
    }, ];
  }
};
