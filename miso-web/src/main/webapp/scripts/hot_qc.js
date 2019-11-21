HotTarget.qc = function(qcTarget) {

  function getQcType(id, name) {
    if (id) {
      return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(id), Constants.qcTypes);
    } else if (name) {
      return Utils.array.findUniqueOrThrow(function(qcType) {
        return qcType.qcTarget === qcTarget && qcType.name === name && !qcType.archived;
      }, Constants.qcTypes);
    } else {
      return null;
    }
  }

  return {
    getCreateUrl: function() {
      return Urls.rest.qcs.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.qcs.update(id);
    },
    requestConfiguration: function(config, callback) {
      callback(config);
    },

    fixUp: function(qc, errorHandler) {
      if (qc.controls) {
        // remove unused control elements
        for (var i = qc.controls.length - 1; i >= 0; i--) {
          if (!qc.controls[i].controlId) {
            qc.controls.splice(i, 1);
          }
        }
      }
    },

    createColumns: function(config, create, data) {
      var columns = [
          {
            header: qcTarget + ' Alias',
            data: 'entityAlias',
            readOnly: true,
            include: true,
            unpackAfterSave: false,
            unpack: function(qc, flat, setCellMeta) {
              flat.entityAlias = qc.entityAlias;
            },
            pack: function(qc, flat, errorHandler) {
            }
          },
          {
            header: 'Date',
            data: 'date',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: false,
            include: true,
            unpack: function(qc, flat, setCellMeta) {
              flat.date = qc.date || null;
            },
            pack: function(qc, flat, errorHandler) {
              qc.date = flat.date;
            }
          },
          {
            header: 'Type',
            data: 'typeName',
            type: 'dropdown',
            trimDropdown: false,
            allowHtml: true,
            source: Constants.qcTypes.filter(function(qcType) {
              return qcType.qcTarget == qcTarget && !qcType.archived;
            }).map(Utils.array.getName).sort(),
            unpack: function(qc, flat, setCellMeta) {
              flat.typeName = qc.type ? qc.type.name : "";
              flat.typeId = qc.type ? qc.type.id : null;
              setCellMeta({
                source: Constants.qcTypes.filter(function(qcType) {
                  return qcType.qcTarget === qcTarget && (!qcType.archived || (qc.type && qc.type.id === qcType.id));
                }).map(Utils.array.getName).sort()
              })
            },
            pack: function(qc, flat, errorHandler) {
              if (create) {
                qc.type = Utils.array.findFirstOrNull(function(qcType) {
                  return qcType.qcTarget == qcTarget && qcType.name == flat.typeName && !qcType.archived;
                }, Constants.qcTypes);
              }
            },
            readOnly: !create,
            validator: HotUtils.validator.requiredAutocomplete,
            include: true
          },
          {
            header: 'Instrument',
            include: true,
            data: 'instrument',
            type: 'dropdown',
            trimDropdown: false,
            source: [],
            depends: 'typeName',
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType && qcType.instrumentModelId) {
                setReadOnly(false);
                setOptions({
                  source: config.instruments.filter(
                      function(instrument) {
                        return instrument.instrumentModelId === qcType.instrumentModelId
                            && (!instrument.dateDecommissioned || instrument.id === qc.instrumentId);
                      }).map(function(instrument) {
                    return instrument.name;
                  }),
                  validator: HotUtils.validator.requiredAutocomplete
                });
              } else {
                setData(null);
                setReadOnly(true);
                setOptions({
                  source: [],
                  validator: HotUtils.validator.requiredEmpty
                });
              }
            },
            unpack: function(qc, flat, setCellMeta) {
              if (qc.instrumentId) {
                flat.instrument = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(qc.instrumentId), config.instruments).name;
              } else {
                flat.instrument = null;
              }
            },
            pack: function(qc, flat, errorHandler) {
              if (flat.instrument) {
                qc.instrumentId = Utils.array.findUniqueOrThrow(Utils.array.namePredicate(flat.instrument), config.instruments).id;
              } else {
                qc.instrumentId = null;
              }
            }
          },
          HotUtils.makeColumnForText('Kit Lot', true, 'kitLot', {
            depends: 'typeName',
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType && qcType.kitDescriptorId) {
                setReadOnly(false);
                setOptions({
                  validator: HotUtils.validator.requiredTextNoSpecialChars
                });
              } else {
                setData(null);
                setReadOnly(true);
                setOptions({
                  validator: HotUtils.validator.requiredEmpty
                });
              }
            }
          }),
          {
            header: 'Result',
            data: 'results',
            type: 'text',
            validator: HotUtils.validator.decimal(16, 10, true).validator,
            include: true,
            depends: 'typeName',
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType == null) {
                setData(null);
                setReadOnly(true);
                return;
              }
              setReadOnly(false);

              if (qcType.precisionAfterDecimal < 0) {
                setOptions({
                  type: 'checkbox',
                  renderer: 'checkbox',
                  editor: 'checkbox',
                  validator: null
                });
                setData(false);
              } else {
                setOptions({
                  type: 'text',
                  renderer: 'text',
                  editor: 'text',
                  validator: HotUtils.validator.decimal(qcType.precisionAfterDecimal + 6, qcType.precisionAfterDecimal, true, true).validator
                });
                setData(null);
              }

            },
            unpack: function(qc, flat, setCellMeta) {
              if (qc.type != null && qc.type.precisionAfterDecimal < 0) {
                flat.results = qc.results > 0;
              } else {
                flat.results = qc.results;
              }
            },
            pack: function(qc, flat, errorHandler) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType && qcType.precisionAfterDecimal < 0) {
                qc.results = flat.results ? 1.0 : 0.0;
              } else {
                qc.results = flat.results;
              }
            }
          }, {
            header: 'Units',
            data: 'units',
            type: 'text',
            allowHtml: true,
            renderer: "html",
            include: true,
            depends: 'typeName',
            readOnly: true,
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              flat.units = Utils.array.maybeGetProperty(getQcType(flat.typeId, flat.typeName), 'units');
            },
            unpack: function(qc, flat, setCellMeta) {
              // Do nothing; this never comes from the server
            },
            pack: function(qc, flat, errorHandler) {
              // Do nothing; this never goes to the server
            }
          }, HotUtils.makeColumnForText('Description', true, 'description', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          })];

      var controlCount = config.controlCount || data.map(function(item) {
        return item.controls ? item.controls.length : 0;
      }).reduce(function(accumulator, current) {
        return Math.max(accumulator, current)
      }, 0);
      if (config.addControls) {
        controlCount += config.addControls;
      }
      for (var i = 1; i <= controlCount; i++) {
        (function() {
          var controlNumber = i;
          columns.push({
            header: 'Control ' + controlNumber,
            include: true,
            data: 'control' + controlNumber,
            type: 'dropdown',
            trimDropdown: false,
            source: [],
            depends: 'typeName',
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType == null || !qcType.controls || !qcType.controls.length) {
                setData(null);
                setReadOnly(true);
                setOptions({
                  source: [],
                  validator: null
                });
              } else {
                setReadOnly(false);
                setOptions({
                  source: qcType.controls.map(Utils.array.getAlias),
                  validator: controlNumber === 1 ? HotUtils.validator.requiredAutocomplete : HotUtils.validator.permitEmptyDropdown
                });
              }
            },
            unpack: function(obj, flat, setCellMeta) {
              if (obj.controls !== null && obj.controls.length >= controlNumber) {
                flat['control' + controlNumber] = Utils.array.findUniqueOrThrow(Utils.array
                    .idPredicate(obj.controls[controlNumber - 1].controlId), obj.type.controls).alias;
              } else {
                flat['control' + controlNumber] = null;
              }
            },
            pack: function(obj, flat, errorHandler) {
              if (!obj.controls) {
                obj.controls = [];
              }
              if (!obj.controls[controlNumber - 1]) {
                obj.controls[controlNumber - 1] = {};
              }
              if (flat['control' + controlNumber]) {
                var qcType = getQcType(flat.typeId, flat.typeName);
                obj.controls[controlNumber - 1].controlId = Utils.array.findUniqueOrThrow(Utils.array.aliasPredicate(flat['control'
                    + controlNumber]), qcType.controls).id;
              } else {
                obj.controls[controlNumber - 1].controlId = null;
              }
            }
          }, {
            header: 'Control ' + controlNumber + ' LOT#',
            include: true,
            data: 'control' + controlNumber + 'Lot',
            type: 'text',
            depends: ['typeName', 'control' + controlNumber],
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType == null || !qcType.controls || !qcType.controls.length || !flat['control' + controlNumber]) {
                setData(null);
                setReadOnly(true);
                setOptions({
                  validator: null
                });
              } else {
                setReadOnly(false);
                setOptions({
                  validator: HotUtils.validator.requiredTextNoSpecialChars
                });
              }
            },
            unpack: function(obj, flat, setCellMeta) {
              if (obj.controls !== null && obj.controls.length >= controlNumber) {
                flat['control' + controlNumber + 'Lot'] = obj.controls[controlNumber - 1].lot;
              } else {
                flat['control' + controlNumber + 'Lot'] = null;
              }
            },
            pack: function(obj, flat, errorHandler) {
              if (!obj.controls) {
                obj.controls = [];
              }
              if (!obj.controls[controlNumber - 1]) {
                obj.controls[controlNumber - 1] = {};
              }
              obj.controls[controlNumber - 1].lot = flat['control' + controlNumber + 'Lot'];
            }
          }, {
            header: 'Control ' + controlNumber + ' Passed?',
            data: 'control' + controlNumber + 'Passed',
            include: true,
            type: 'dropdown',
            trimDropdown: false,
            source: [],
            depends: ['typeName', 'control' + controlNumber],
            update: function(qc, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcType = getQcType(flat.typeId, flat.typeName);
              if (qcType == null || !qcType.controls || !qcType.controls.length || !flat['control' + controlNumber]) {
                setData(null);
                setReadOnly(true);
                setOptions({
                  source: [],
                  validator: null
                });
              } else {
                setReadOnly(false);
                setOptions({
                  source: ['True', 'False'],
                  validator: HotUtils.validator.requiredAutocomplete
                });
              }
            },
            unpack: function(obj, flat, setCellMeta) {
              if (obj.controls !== null && obj.controls.length >= controlNumber) {
                flat['control' + controlNumber + 'Passed'] = obj.controls[controlNumber - 1].qcPassed ? 'True' : 'False';
              } else {
                flat['control' + controlNumber + 'Passed'] = null;
              }
            },
            pack: function(obj, flat, errorHandler) {
              if (!obj.controls) {
                obj.controls = [];
              }
              if (!obj.controls[controlNumber - 1]) {
                obj.controls[controlNumber - 1] = {};
              }
              obj.controls[controlNumber - 1].qcPassed = flat['control' + controlNumber + 'Passed'] === 'True' ? true : false;
            }
          });
        })();
      }

      return columns;
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          Utils.showDialog('Edit QCs', 'Edit', [{
            property: 'controls',
            type: 'int',
            label: 'Add controls per QC',
            value: 0
          }], function(result) {
            if (!Number.isInteger(result.controls) || result.controls < 0) {
              Utils.showOkDialog('Error', ['Invalid number of controls entered']);
              return;
            }
            window.location = Urls.ui.qcs.bulkEdit(qcTarget) + '?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(','),
              addControls: result.controls
            });
          });
        }
      }];
    }
  };
};
