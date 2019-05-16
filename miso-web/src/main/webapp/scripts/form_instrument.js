if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.instrument = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean,
   *   instrumentTypes: array,
   *   instruments: array
   * }
   */

  return {
    getSaveUrl: function(instrument) {
      return instrument.id ? ('/miso/rest/instruments/' + instrument.id) : '/miso/rest/instruments';
    },
    getSaveMethod: function(instrument) {
      return instrument.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(instrument) {
      return '/miso/instrument/' + instrument.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Instrument Information',
        fields: config.isAdmin ? getAdminFields(config, object) : getReadOnlyFields(config, object)
      }];
    }
  };

  function getReadOnlyFields(config, object) {
    return [{
      title: 'Instrument ID',
      data: 'id',
      type: 'read-only'
    }, {
      title: 'Instrument Type',
      data: 'instrumentType',
      type: 'read-only',
      getDisplayValue: function(instrument) {
        return Utils.array.findUniqueOrThrow(function(type) {
          return type.value === instrument.instrumentType;
        }, config.instrumentTypes).label;
      }
    }, {
      title: 'Instrument Model',
      data: 'instrumentModelAlias',
      type: 'read-only',
      getDisplayValue: function(instrument) {
        return instrument.platformType + ' - ' + instrument.instrumentModelAlias;
      }
    }, {
      title: 'Serial Number',
      data: 'serialNumber',
      type: 'read-only'
    }, {
      title: 'Name',
      data: 'name',
      type: 'read-only'
    }, {
      title: 'Upgraded From',
      data: 'preUpgradeInstrumentId',
      type: 'read-only',
      getDisplayValue: function(instrument) {
        return instrument.preUpgradeInstrumentAlias;
      },
      getLink: function(instrument) {
        return '/miso/instrument/' + instrument.preUpgradeInstrumentId;
      },
      include: !!object.preUpgradeInstrumentId
    }, {
      title: 'Commissioned',
      data: 'dateCommissioned',
      type: 'read-only'
    }, {
      title: 'Status',
      data: 'status',
      type: 'read-only'
    }, {
      title: 'Decommissioned',
      data: 'dateDecommissioned',
      type: 'read-only',
      include: !!object.dateDecommissioned
    }, {
      title: 'Upgraded To',
      data: 'upgradedInstrumentId',
      type: 'read-only',
      getDisplayValue: function(instrument) {
        return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(instrument.upgradedInstrumentId), config.instruments);
      },
      include: !!object.upgradedInstrumentId
    }];
  }

  function getAdminFields(config, object) {
    return [{
      title: 'Instrument ID',
      data: 'id',
      type: 'read-only',
      getDisplayValue: function(instrument) {
        return instrument.id || 'Unsaved';
      }
    }, {
      title: 'Instrument Type',
      data: 'instrumentType',
      type: 'read-only',
      include: !!object.id,
      getDisplayValue: function(instrument) {
        return Utils.array.findUniqueOrThrow(function(type) {
          return type.value === instrument.instrumentType;
        }, config.instrumentTypes).label;
      }
    }, {
      title: 'Instrument Type',
      data: 'instrumentType',
      type: 'dropdown',
      include: !object.id,
      required: true,
      getSource: function() {
        return config.instrumentTypes;
      },
      getItemLabel: function(item) {
        return item.label;
      },
      getItemValue: function(item) {
        return item.value;
      },
      initial: 'SEQUENCER',
      onChange: function(newValue, updateField) {
        updateField('instrumentModelId', {
          source: Constants.instrumentModels.filter(function(model) {
            return model.instrumentType === newValue;
          })
        });
      }
    }, {
      title: 'Instrument Model',
      data: 'instrumentModelId',
      type: 'dropdown',
      required: true,
      getSource: function() {
        return Constants.instrumentModels.filter(function(model) {
          return model.instrumentType === object.instrumentType;
        });
      },
      getItemLabel: function(item) {
        return Utils.array.findUniqueOrThrow(function(type) {
          return type.name === item.platformType;
        }, Constants.platformTypes).key + ' - ' + item.alias;
      },
      getItemValue: Utils.array.getId,
      sortSource: Utils.sorting.standardSortByCallback(function(item) {
        return item.platformType + item.alias;
      })
    }, {
      title: 'Serial Number',
      data: 'serialNumber',
      type: 'text',
      maxLength: 30
    }, {
      title: 'Name',
      data: 'name',
      type: 'text',
      required: true,
      maxLength: 30
    }, {
      title: 'Upgraded From',
      data: 'preUpgradeInstrumentId',
      type: 'read-only',
      getDisplayValue: function(instrument) {
        return instrument.preUpgradeInstrumentAlias;
      },
      getLink: function(instrument) {
        return '/miso/instrument/' + instrument.preUpgradeInstrumentId;
      },
      include: !!object.preUpgradeInstrumentId
    }, {
      title: 'Commissioned',
      data: 'dateCommissioned',
      type: 'date'
    }, {
      title: 'Status',
      data: 'status',
      type: 'dropdown',
      required: true,
      getSource: function() {
        return ['Production', 'Retired', 'Upgraded'];
      },
      onChange: function(newValue, updateField) {
        var decommissioned = {
          disabled: true,
          required: false
        };
        var upgrade = {
          disabled: true,
          required: false
        };
        switch (newValue) {
        case 'Production':
          decommissioned.value = null;
          upgrade.value = null;
          break;
        case 'Retired':
          decommissioned.disabled = false;
          decommissioned.required = true;
          upgrade.value = null;
          break;
        case 'Upgraded':
          decommissioned.disabled = false;
          decommissioned.required = true;
          upgrade.disabled = false;
          upgrade.required = true;
          break;
        default:
          throw new Error('Unexpected status value: ' + newValue);
        }
        updateField('dateDecommissioned', decommissioned);
        updateField('upgradedInstrumentId', upgrade);
      }
    }, {
      title: 'Decommissioned',
      data: 'dateDecommissioned',
      type: 'date',
      disabled: true
    }, {
      title: 'Upgraded To',
      data: 'upgradedInstrumentId',
      type: 'dropdown',
      nullLabel: 'N/A',
      getSource: function() {
        return config.instruments;
      },
      getItemLabel: Utils.array.getName,
      getItemValue: Utils.array.getId,
      sortSource: Utils.sorting.standardSort('name'),
      disabled: true
    }];
  }

})(jQuery);
