HotTarget.sequencingorder = (function() {
  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('sequencing_orders');
    },
    getCreateUrl: function() {
      return Urls.rest.sequencingOrders.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.sequencingOrders.update(id);
    },
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(lib, errorHandler) {
    },
    createColumns: function(config, create, data) {
      return [
          {
            header: 'Pool Name',
            data: 'poolName',
            readOnly: true,
            include: true,
            unpack: function(order, flat, setCellMeta) {
              flat.poolName = order.pool.name;
            },
            pack: function(order, flat, errorHandler) {
            }
          },
          {
            header: 'Pool Alias',
            data: 'poolAlias',
            readOnly: true,
            include: true,
            unpack: function(order, flat, setCellMeta) {
              flat.poolAlias = order.pool.alias;
            },
            pack: function(order, flat, errorHandler) {
            }
          },
          HotUtils.makeColumnForConstantsList('Purpose', true, 'purposeAlias', 'purposeId', 'id', 'alias', Constants.runPurposes, true, {}),
          {
            header: 'Description',
            data: 'description',
            include: true,
            unpack: function(order, flat, setCellMeta) {
              flat.description = order.description || null;
            },
            pack: function(order, flat, errorHandler) {
              order.description = flat.description;
            }
          },
          {
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
          },
          {
            header: 'Container Model',
            data: 'containerModel',
            type: 'dropdown',
            trimDropdown: false,
            source: [''],
            include: true,
            unpack: function(order, flat, setCellMeta) {
              if (order.containerModelId) {
                flat.containerModel = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(order.containerModelId),
                    Constants.containerModels).array;
              }
            },
            pack: function(order, flat, errorHandler) {
              if (flat.containerModel) {
                order.containerModelId = Utils.array.findUniqueOrThrow(Utils.array.aliasPredicate(flat.containerModel),
                    Constants.containerModels).id;
              }
            },
            depends: ['*start', 'instrumentModel'],
            update: function(order, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var source = [];
              if (flat.instrumentModel) {
                var selectedInstrumentModel = Constants.instrumentModels.find(Utils.array.aliasPredicate(flat.instrumentModel));
                if (selectedInstrumentModel) {
                  source = selectedInstrumentModel.containerModels.map(Utils.array.getAlias);
                }
              }
              setOptions({
                source: source,
                validator: HotUtils.validator[(order.id && !order.containerModelId) ? 'requiredEmpty' : 'requiredAutocomplete']
              });
              setReadOnly(order.id && !order.containerModelId);
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
                return param.name == flat.sequencingParameters && param.instrumentModelAlias == flat.instrumentModel;
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
                  return parameters.instrumentModelId == instrumentModel.id;
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
          }, HotUtils.makeColumnForInt('Partitions', true, 'partitions', HotUtils.validator.integer(true, 1))];
    },
    getCustomActions: function(table) {
      return [];
    },
    getBulkActions: function(config) {
      return [];
    }
  };
})();
