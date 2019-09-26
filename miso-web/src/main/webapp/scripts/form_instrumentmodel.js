if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.instrumentmodel = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean
   *   pageMode: string 'create' or 'edit'
   * }
   */

  return {
    getSaveUrl: function(model) {
      return model.id ? Urls.rest.instrumentModels.update(model.id) : Urls.rest.instrumentModels.create;
    },
    getSaveMethod: function(model) {
      return model.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(model) {
      return Urls.ui.instrumentModels.edit(model.id);
    },
    getSections: function(config, object) {
      return [{
        title: 'Model Information',
        fields: config.isAdmin ? getEditableFields(config, object) : getReadOnlyFields(config, object)
      }];
    },
    confirmSave: function(object, saveCallback) {
      object.positions = InstrumentModel.getInstrumentPositions();
      object.containerModels = InstrumentModel.getContainerModels();
      saveCallback();
    }
  }

  function getReadOnlyFields(config, object) {
    $('#save').remove();
    return [{
      title: 'Instrument Model ID',
      data: 'id',
      type: 'read-only',
      getDisplayValue: function(model) {
        return model.id || 'Unsaved';
      }
    }, {
      title: 'Alias',
      data: 'alias',
      type: 'read-only'
    }, {
      title: 'Description',
      data: 'description',
      type: 'read-only'
    }, {
      title: 'Platform',
      data: 'platformType',
      type: 'read-only',
      getDisplayValue: function(model) {
        return Utils.array.findUniqueOrThrow(Utils.array.namePredicate(model.platformType), Constants.platformTypes).key;
      }
    }, {
      title: 'Instrument Type',
      data: 'instrumentType',
      type: 'read-only',
      getDisplayValue: function(model) {
        return Utils.array.findUniqueOrThrow(function(item) {
          return item.value === model.instrumentType;
        }, Constants.instrumentTypes).label;
      }
    }, {
      title: 'Sequencing Containers per Run',
      data: 'numContainers',
      type: 'read-only'
    }, {
      title: 'Index Sequencing',
      data: 'dataManglingPolicy',
      type: 'read-only',
      getDisplayValue: function(model) {
        return Utils.array.findUniqueOrThrow(function(item) {
          return item.value === model.dataManglingPolicy;
        }, Constants.dataManglingPolicies).label;
      }
    }];
  }

  function getEditableFields(config, object) {
    return [{
      title: 'Instrument Model ID',
      data: 'id',
      type: 'read-only',
      getDisplayValue: function(model) {
        return model.id || 'Unsaved';
      }
    }, {
      title: 'Alias',
      data: 'alias',
      type: 'text',
      required: true,
      maxLength: 100
    }, {
      title: 'Description',
      data: 'description',
      type: 'text',
      maxLength: 255
    }, {
      title: 'Platform',
      data: 'platformType',
      type: 'dropdown',
      include: config.pageMode === 'create',
      required: true,
      getSource: function() {
        return Constants.platformTypes;
      },
      getItemLabel: function(item) {
        return item.key;
      },
      getItemValue: Utils.array.getName
    }, {
      title: 'Platform',
      data: 'platformType',
      include: config.pageMode !== 'create',
      type: 'read-only',
      getDisplayValue: function(model) {
        return Utils.array.findUniqueOrThrow(Utils.array.namePredicate(model.platformType), Constants.platformTypes).key;
      }
    }, {
      title: 'Instrument Type',
      data: 'instrumentType',
      type: 'dropdown',
      include: config.pageMode === 'create',
      required: true,
      getSource: function() {
        return Constants.instrumentTypes;
      },
      getItemLabel: function(item) {
        return item.label;
      },
      getItemValue: function(item) {
        return item.value;
      },
      initial: 'SEQUENCER'
    }, {
      title: 'Instrument Type',
      data: 'instrumentType',
      type: 'read-only',
      include: config.pageMode !== 'create',
      getDisplayValue: function(model) {
        return Utils.array.findUniqueOrThrow(function(item) {
          return item.value === model.instrumentType;
        }, Constants.instrumentTypes).label;
      }
    }, {
      title: 'Sequencing Containers per Run',
      data: 'numContainers',
      type: 'int',
      min: 0,
      required: true
    }, {
      title: 'Index Sequencing',
      data: 'dataManglingPolicy',
      type: 'dropdown',
      required: true,
      getSource: function() {
        return Constants.dataManglingPolicies;
      },
      getItemLabel: function(item) {
        return item.label;
      },
      getItemValue: function(item) {
        return item.value;
      },
      initial: 'NONE'
    }];
  }

})(jQuery);
