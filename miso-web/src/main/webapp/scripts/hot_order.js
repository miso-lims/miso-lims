HotTarget.order = (function() {
  return {
    createUrl: '/miso/rest/poolorder',
    updateUrl: '/miso/rest/poolorder/',
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
        header: 'Platform',
        data: 'platform',
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
          var platforms = Constants.platforms.filter(function(platform) {
            return platform.platformType == order.pool.platformType && platform.active;
          }).map(function(platform) {
            return platform.instrumentModel;
          }).sort();
          setOptions({
            source: platforms
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
            return param.name == flat.sequencingParameters;
          }, Constants.sequencingParameters);
        },
        depends: 'platform',
        update: function(order, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          if (value) {
            var platform = Utils.array.findFirstOrNull(function(platform) {
              return platform.instrumentModel == value;
            }, Constants.platforms);
            var params = Constants.sequencingParameters.filter(function(parameters) {
              return parameters.platform.id == platform.id;
            }).map(function(parameters) {
              return parameters.name;
            }).sort();
          } else {
            var params = [];
          }
          setOptions({
            source: params
          });
        }
      }, HotUtils.makeColumnForInt('Partitions', true, 'partitions', HotUtils.validator.requiredNumber)];
    },
    getCustomActions: function(table) {
      return [];
    },
    getBulkActions: function(config) {
      return [];
    }
  };
})();
