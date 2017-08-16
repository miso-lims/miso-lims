HotTarget.pool = {
  createUrl : '/miso/rest/pool',
  updateUrl : '/miso/rest/pool/',
  requestConfiguration : function(config, callback) {
    callback(config)
  },
  fixUp : function(lib, errorHandler) {
  },
  createColumns : function(config, create, data) {
    return [
        {
          header : 'Pool Name',
          data : 'name',
          readOnly : true,
          include : true,
          unpackAfterSave : true,
          unpack : function(pool, flat, setCellMeta) {
            flat.name = pool.name;
          },
          pack : function(pool, flat, errorHandler) {
            pool.name = flat.name;
          }
        },
        {
          header : 'Pool Alias',
          data : 'alias',
          include : true,
          validator : HotUtils.validator.requiredText,
          unpackAfterSave : true,
          unpack : function(pool, flat, setCellMeta) {
            flat.alias = pool.alias || null;
          },
          pack : function(pool, flat, errorHandler) {
            pool.alias = flat.alias;
          }
        },
        {
          header : 'Matrix Barcode',
          data : 'identificationBarcode',
          validator : HotUtils.validator.optionalTextNoSpecialChars,
          include : !Constants.automaticBarcodes,
          unpack : function(pool, flat, setCellMeta) {
            flat.identificationBarcode = pool.identificationBarcode || null;
          },
          pack : function(pool, flat, errorHandler) {
            pool.identificationBarcode = flat.identificationBarcode;
          }
        },
        {
          header : 'Creation Date',
          data : 'creationDate',
          type : 'date',
          dateFormat : 'YYYY-MM-DD',
          datePickerConfig : {
            firstDay : 0,
            numberOfMonths : 1
          },
          allowEmpty : false,
          validator : HotUtils.validator.requiredText,
          include : true,
          unpack : function(pool, flat, setCellMeta) {
            flat.creationDate = pool.creationDate || null;
          },
          pack : function(pool, flat, errorHandler) {
            pool.creationDate = flat.creationDate;
          }
        },
        HotUtils.makeColumnForFloat(
            'Concentration (' + Constants.poolConcentrationUnits + ')', true,
            'concentration', true),
        HotUtils.makeColumnForFloat('Volume (&#181;l)', true, 'volume', false),
        HotUtils.makeColumnForBoolean('QC Passed?', true, 'qcPassed', false),
        HotUtils.makeColumnForBoolean('Ready to Run?', true, 'readyToRun', true) ];
  },
  
  bulkActions : [
      {
        name : 'Edit',
        action : function(items) {
          window.location = window.location.origin + '/miso/pool/bulk/edit?' + jQuery
              .param({
                ids : items.map(Utils.array.getId).join(',')
              });
        }
      }, ],

};
