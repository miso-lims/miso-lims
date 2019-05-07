if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.dilution = (function($) {

  return {
    getSaveUrl: function(dilution) {
      if (dilution.id) {
        return '/miso/rest/librarydilution/' + dilution.id;
      } else {
        throw new Error('Page not intended for new dilution creation');
      }
    },
    getSaveMethod: function(dilution) {
      return 'PUT';
    },
    getEditUrl: function(dilution) {
      return '/miso/dilution/' + dilution.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Dilution Information',
        fields: [{
          title: 'Dilution ID',
          data: 'id',
          type: 'read-only'
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only'
        }, {
          title: 'Parent Library',
          data: 'libraryId',
          type: 'read-only',
          getDisplayValue: function(dilution) {
            return dilution.library.alias;
          },
          getLink: function(dilution) {
            return '/miso/library/' + dilution.library.id;
          }
        }, {
          title: 'Matrix Barcode',
          data: 'identificationBarcode',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Discarded',
          data: 'discarded',
          type: 'checkbox',
          onChange: function(newValue, updateField) {
            updateField('volume', {
              disabled: newValue
            });
          }
        }, {
          title: 'Volume',
          data: 'volume',
          type: 'decimal'
        }, FormUtils.makeUnitsField('volume'), {
          title: 'Concentration',
          data: 'concentration',
          type: 'decimal'
        }, FormUtils.makeUnitsField('concentration'), FormUtils.makeBoxLocationField(), {
          title: 'Creation Date',
          data: 'creationDate',
          required: 'true',
          type: 'date'
        }, {
          title: 'Targeted Sequencing',
          data: 'targetedSequencingId',
          type: 'dropdown',
          getSource: function() {
            return Constants.targetedSequencings.filter(function(targetedSequencing) {
              return targetedSequencing.kitDescriptorIds.indexOf(object.library.kitDescriptorId) > -1;
            });
          },
          sortSource: Utils.sorting.standardSort('alias'),
          getItemLabel: function(item) {
            return item.alias;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'ng of Library Used',
          data: 'ngUsed',
          type: 'decimal'
        }, {
          title: 'Volume of Library Used',
          data: 'volumeUsed',
          type: 'decimal'
        }].concat(FormUtils.makeDistributionFields())
      }];
    }
  };

})(jQuery);
