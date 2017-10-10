/**
 * Library-receipt-specific Handsontable code
 */

HotTarget.libraryReceipt = (function() {
  return {
    createUrl: '/miso/rest/library',
    updateUrl: '/miso/rest/library/',
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: HotTarget.library.fixUp,
    createColumns: function(config, create, data) {
      var samColumns = HotTarget.sample.createColumns(config, create, data);
      var libColumns = HotTarget.library.createColumns(config, create, data);

      samColumns.forEach(function(col, colIndex) {
        col.data = 'sample.' + col.data;
        if (col.depends) {
          col.depends = 'sample.' + col.depends;
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
          col.libraryPack(lib.sample, flat.sample, errorHandler);
        };
        if (col.update) {
          col.libraryUpdate = col.update;
          col.update = function(lib, flat, value, setReadOnly, setOptions, setData) {
            if (!flat.sample) {
              flat.sample = {};
            }
            col.libraryUpdate(lib.sample, flat.sample, value, setReadOnly, setOptions, setData);
          };
        }
      });

      return libColumns.splice(0, 4).concat(samColumns).concat(libColumns);
    },

    fixedColumns: 0,

    bulkActions: [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/library/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }, {
      name: 'Make dilutions',
      action: function(items) {
        window.location = window.location.origin + '/miso/library/dilutions/bulk/propagate?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }, HotUtils.printAction('library'), ].concat(HotUtils.makeQcActions("Library")),

  };
})();
