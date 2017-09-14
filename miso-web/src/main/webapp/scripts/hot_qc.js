/**
 * Sample-specific Handsontable code
 */
HotTarget.qc = function(qcTarget) {
  return {
    createUrl: '/miso/rest/qc/',
    updateUrl: '/miso/rest/qc/',
    requestConfiguration: function(config, callback) {
      callback(config);
    },

    fixUp: function(qc, errorHandler) {

    },

    createColumns: function(config, create, data) {
      return [{
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
      }, {
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
      }, {
        header: 'Type',
        data: 'typeName',
        type: 'dropdown',
        source: Constants.qcTypes.filter(function(qcType) {
          return qcType.qcTarget == qcTarget;
        }).map(Utils.array.getName).sort(),
        unpack: function(qc, flat, setCellMeta) {
          flat.typeName = qc.type ? qc.type.name : "";
        },
        pack: function(qc, flat, errorHandler) {
          if (create) {
            qc.type = Utils.array.findFirstOrNull(function(qcType) {
              return qcType.qcTarget == qcTarget && qcType.name == flat.typeName;
            }, Constants.qcTypes);
          }
        },
        readOnly: !create,
        validator: HotUtils.validator.requiredAutocomplete,
        include: true
      }, {
        header: 'Result',
        data: 'results',
        type: 'numeric',
        include: true,
        depends: 'typeName',
        update: function(qc, flat, value, setReadOnly, setOptions, setData) {
          var qcType = Utils.array.findFirstOrNull(function(qcType) {
            return qcType.qcTarget == qcTarget && qcType.name == flat.typeName;
          }, Constants.qcTypes);
          if (qcType == null) {
            setReadOnly(true);
            return;
          }
          setReadOnly(false);

          if (qcType.precisionAfterDecimal < 0) {
            setOptions({
              type: 'checkbox'
            });
            setData(false);
          } else {
            setOptions({
              type: 'numeric',
              format: '0.' + '0'.repeat(qcType.precisionAfterDecimal)
            });
            setData(0);
          }

        },
        unpack: function(qc, flat, setCellMeta) {
          flat.results = qc.results;
        },
        pack: function(qc, flat, errorHandler) {
          var qcType = Utils.array.findFirstOrNull(function(qcType) {
            return qcType.qcTarget == qcTarget && qcType.name == flat.typeName;
          }, Constants.qcTypes);

          if (qcType.precisionAfterDecimal < 0) {
            qc.results = flat.results ? 1.0 : 0.0;
          } else {
            qc.results = flat.results;
          }
        }
      }, {
        header: 'Units',
        data: 'units',
        type: 'text',
        renderer: function(instance, td, row, col, prop, value, cellProperties) {
          td.innerHTML = value;
        },
        include: true,
        depends: 'typeName',
        readOnly: true,
        update: function(qc, flat, value, setReadOnly, setOptions, setData) {
          flat.units = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(qcType) {
            return qcType.qcTarget == qcTarget && qcType.name == flat.typeName;
          }, Constants.qcTypes), 'units');
        },
        unpack: function(qc, flat, setCellMeta) {
          // Do nothing; this never comes from the server
        },
        pack: function(qc, flat, errorHandler) {
          // Do nothing; this never goes to the server
        }
      }];
    },

    bulkActions: [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/qc/bulk/edit/' + qcTarget + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }, ]
  };
};
