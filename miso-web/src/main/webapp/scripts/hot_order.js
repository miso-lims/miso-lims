HotTarget.order = (function() {
  return {
    createUrl: '/miso/rest/poolorders',
    updateUrl: '/miso/rest/poolorders/',
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(lib, errorHandler) {
    },
    createColumns: function(config, create, data) {
      return [{
        header: 'Pool Name',
        data: 'poolName',
        readOnly: true,
        include: true,
        unpack: function(order, flat, setCellMeta) {
          flat.poolName = order.pool.name;
        },
        pack: function(order, flat, errorHandler) {
        }
      }, {
        header: 'Pool Alias',
        data: 'poolAlias',
        readOnly: true,
        include: true,
        unpack: function(order, flat, setCellMeta) {
          flat.poolAlias = order.pool.alias;
        },
        pack: function(order, flat, errorHandler) {
        }
      }, {
        header: 'Description',
        data: 'description',
        include: true,
        unpack: function(order, flat, setCellMeta) {
          flat.description = order.description || null;
        },
        pack: function(order, flat, errorHandler) {
          order.description = flat.description;
        }
      }, {
        header: 'Instrument Model',
        data: 'instrumentModel',
        type: 'dropdown',
        trimDropdown: false,
        validator: HotUtils.validator.requiredAutocomplete,
        source: [''],
        include: true,
        unpack: function(order, flat, setCellMeta) {
        },
        pack: function(order, flat, errorHandler) {
        },
        depends: '*start',
        update: function(order, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          var instrumentModels = Constants.instrumentModels.filter(function(model) {
            return model.platformType == order.pool.platformType && model.active;
          }).map(function(model) {
            return model.alias;
          }).sort();
          setOptions({
            source: instrumentModels
          });
        }
      }, {
        header: 'Sequencing Parameters',
        data: 'sequencingParameters',
        type: 'dropdown',
        trimDropdown: false,
        validator: HotUtils.validator.requiredAutocomplete,
        source: [''],
        include: true,
        unpack: function(order, flat, setCellMeta) {
          if (order.parameters) {
            flat.sequencingParameters = Utils.array.findFirstOrNull(function(param) {
              return param.id == order.parameters.id;
            }, Constants.sequencingParameters);
          }
        },
        pack: function(order, flat, errorHandler) {
          order.parameters = Utils.array.findFirstOrNull(function(param) {
            return param.name == flat.sequencingParameters && param.instrumentModel.alias == flat.instrumentModel;
          }, Constants.sequencingParameters);
        },
        depends: 'instrumentModel',
        update: function(order, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          var params = null;
          if (value) {
            var instrumentModel = Utils.array.findFirstOrNull(function(instrumentModel) {
              return instrumentModel.alias == value;
            }, Constants.instrumentModels);
            params = Constants.sequencingParameters.filter(function(parameters) {
              return parameters.instrumentModel.id == instrumentModel.id;
            }).map(function(parameters) {
              return parameters.name;
            }).sort();
          } else {
            params = [];
          }
          setOptions({
            source: params
          });
        }
      }, HotUtils.makeColumnForInt('Partitions', true, 'partitions', HotUtils.validator.requiredPositiveInt)];
    },
    getCustomActions: function(table) {
      return [];
    },
    getBulkActions: function(config) {
      return [];
    }
  };
})();
