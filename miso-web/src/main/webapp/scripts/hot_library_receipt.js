/**
 * Library-receipt-specific Handsontable code
 */

HotTarget.libraryReceipt = (function() {
  return {
    getCreateUrl: function() {
      return Urls.rest.libraries.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.libraries.update(id);
    },
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: HotTarget.library.fixUp,
    createColumns: function(config, create, data) {
      var samColumns = HotTarget.sample.createColumns(config, create, data);
      var libColumns = HotTarget.library.createColumns(config, create, data);

      samColumns.forEach(function(col, colIndex) {
        col.initialData = col.data;
        col.data = 'sample.' + col.data;
        if (col.depends) {
          if (Array.isArray(col.depends)) {
            col.depends = col.depends.map(function(val) {
              return 'sample.' + val;
            });
          } else {
            col.depends = 'sample.' + col.depends;
          }
        }

        col.libraryUnpack = col.unpack;
        col.unpack = function(lib, flat, setCellMeta) {
          if (!flat.sample) {
            flat.sample = {};
          }
          col.libraryUnpack(lib.sample, flat.sample, setCellMeta);
        };

        col.libraryPack = col.pack;
        col.pack = function(lib, flat, errorHandler) {
          if (!flat.sample) {
            flat.sample = {};
          }
          // Sometimes sample properties don't go into the nested subobject, but become a field called
          // "sample.[name]". If this happens, it breaks validation on save. Move it back.
          if (flat.hasOwnProperty(col.data)) {
            flat.sample[col.initialData] = flat[col.data];
          }
          col.libraryPack(lib.sample, flat.sample, errorHandler);
        };

        if (col.update) {
          col.libraryUpdate = col.update;
          col.update = function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            if (!flat.sample) {
              flat.sample = {};
            }
            return col.libraryUpdate(lib.sample, flat.sample, flatProperty, value, setReadOnly, setOptions, setData);
          };
        }
      });

      return libColumns.splice(0, 4).concat(samColumns).concat(libColumns);
    },

    getFixedColumns: function(config) {
      return config.libraryAliasMaybeRequired ? 2 : 0;
    },

    getCustomActions: function(table) {
      return HotTarget.library.getCustomActions(table);
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = window.location.origin + '/miso/library/bulk/edit?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }, {
        name: 'Make aliquots',
        action: function(items) {
          HotUtils.warnIfConsentRevoked(items, function() {
            var fields = [];
            HotUtils.showDialogForBoxCreation('Make Aliquots', 'Create', fields, Urls.ui.libraryAliquots.bulkPropagate, function(result) {
              return {
                ids: items.map(Utils.array.getId).join(',')
              };
            }, function(result) {
              return items.length;
            });
          });
        }
      }, HotUtils.printAction('library'), ].concat(HotUtils.makeQcActions("Library"));
    }

  };
})();
