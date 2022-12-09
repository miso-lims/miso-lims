ListTarget.containermodel = {
  name: "Container Models",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'sequencing-container-models');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function(config, projectId) {
    if (config.isInstrumentModelPage) {
      return !config.isAdmin ? [] : [{
        name: 'Remove',
        action: function(items) {
          InstrumentModel.removeContainerModels(items.map(Utils.array.getId));
        }
      }];
    } else {
      var actions = BulkTarget.containermodel.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(ListUtils.createBulkDeleteAction('Container Models', 'containermodels', function(model) {
          var platform = Utils.array.findUniqueOrThrow(Utils.array.namePredicate(model.platformType), Constants.platformTypes);
          return model.alias + ' (' + platform.key + ')';
        }));
      }
      return actions;
    }
  },
  createStaticActions: function(config, projectId) {
    if (config.isInstrumentModelPage) {
      return !config.isAdmin ? [] : [{
        name: 'Add',
        handler: function() {
          Utils.showDialog('Add Container Model', 'Search', [{
            label: 'Alias or Barcode',
            type: 'text',
            property: 'query',
            required: true
          }], function(output) {
            Utils.ajaxWithDialog('Searching...', 'GET', Urls.rest.containerModels.search + '?' + Utils.page.param({
              platformType: jQuery('#instrumentModelForm_platformType').val(),
              q: output.query
            }), null, function(models, textStatus, xhr) {
              if (!models || !models.length) {
                Utils.showOkDialog('Add Container Model', ['No container models found.']);
                return;
              }
              Utils.showWizardDialog('Add Container Model', models.map(function(model) {
                return {
                  name: model.alias,
                  handler: function() {
                    InstrumentModel.addContainerModel(model);
                  }
                }
              }));
            });
          });
        }
      }];
    } else {
      return config.isAdmin ? [ListUtils.createStaticAddAction('Container Models', Urls.ui.containerModels.bulkCreate, true)] : [];
    }
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Platform',
      mData: 'platformType',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Fallback',
      mData: 'fallback',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }, {
      sTitle: 'Archived',
      mData: 'archived',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.archived
    }];
  }
};
