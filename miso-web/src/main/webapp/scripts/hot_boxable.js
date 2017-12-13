/**
 * Boxable-specific Handsontable code. Provides columns that are intended for use with Boxable types such as Samples a Libraries
 */
HotTarget.boxable = (function() {

  var boxSearchCache = [];
  var boxesByAlias = [];

  var boxAliasValidator = function(value, callback) {
    return callback(Utils.validation.isEmpty(value) || value in boxesByAlias);
  };

  var cacheBox = function(box) {
    var occupied = box.items.map(function(item) {
      return item.coordinates;
    });
    var free = [];
    for (var row = 0; row < box.rows; row++) {
      for (var col = 0; col < box.cols; col++) {
        var pos = String.fromCharCode(65 + row) + (col < 9 ? '0' : '') + (col + 1);
        if (occupied.indexOf(pos) === -1) {
          free.push(pos);
        }
      }
    }
    box.emptyPositions = free;
    boxesByAlias[box.alias] = box;
  };

  return {
    makeBoxLocationColumns: function() {
      return [{
        header: 'Box Search',
        data: 'boxSearch',
        allowEmpty: true,
        include: true,
        unpack: function(obj, flat, setCellMeta) {
          flat.boxSearch = '';
        },
        pack: function(obj, flat, errorHandler) {
          // search field only
        }
      }, {
        header: 'Box Alias',
        data: 'boxAlias',
        type: 'dropdown',
        validator: boxAliasValidator,
        include: true,
        trimDropdown: false,
        source: [''],
        unpack: function(obj, flat, setCellMeta) {
          if (obj.box) {
            flat.boxAlias = obj.box.alias;
            cacheBox(obj.box);
          }
        },
        pack: function(obj, flat, errorHandler) {
          obj.box = obj.box || {};
          if (flat.boxAlias) {
            obj.box.alias = flat.boxAlias;
            obj.box.id = boxesByAlias[flat.boxAlias].id;
          } else {
            obj.box.alias = null;
            obj.box.id = null;
          }
        },
        depends: 'boxSearch',
        update: function(obj, flat, value, setReadOnly, setOptions, setData) {
          var applyChanges = function(source) {
            setOptions({
              source: source
            });
            setData(source.length > 1 ? 'SELECT' : source[0]);
          };

          if (!value) {
            applyChanges(['']);
            return;
          }
          if (boxSearchCache[value]) {
            applyChanges(boxSearchCache[value].map(function(item) {
              return item.alias;
            }));
            return;
          }

          setData('(...searching...)');
          var deferred = jQuery.Deferred();
          jQuery.ajax({
            url: '/miso/rest/boxes/search?' + jQuery.param({
              q: value
            }),
            contentType: "application/json; charset=utf8",
            dataType: "json"
          }).success(function(data) {
            boxSearchCache[value] = data;
            jQuery.each(data, function(index, item) {
              if (!boxesByAlias[item.alias]) {
                cacheBox(item);
              }
            });
            if (!data.length) {
              applyChanges(['']);
            } else {
              applyChanges(data.map(function(item) {
                return item.alias;
              }));
            }
          }).fail(function(response, textStatus, serverStatus) {
            applyChanges(['']);
            HotUtils.showServerErrors(response, serverStatus);
          }).always(function() {
            deferred.resolve();
          });
          return deferred.promise();
        }
      }, {
        header: 'Position',
        data: 'boxPosition',
        type: 'dropdown',
        include: true,
        trimDropdown: false,
        source: [''],
        unpack: function(obj, flat, setCellMeta) {
          flat.boxPosition = obj.boxPosition || null;
          setCellMeta('validator', (obj.box && obj.box.alias) ? /^[A-Z][0-9]{2}$/ : HotUtils.validator.requiredEmpty);
        },
        pack: function(obj, flat, errorHandler) {
          obj.boxPosition = flat.boxPosition;
          if (flat.boxPosition && !flat.boxAlias) {
            errorHandler('No box specified for box position');
          } else if (flat.boxAlias && !flat.boxPosition) {
            errorHandler('Box position missing');
          }
        },
        depends: 'boxAlias',
        update: function(obj, flat, value, setReadOnly, setOptions, setData) {
          setReadOnly(!value);
          setOptions({
            validator: value ? /^[A-Z][0-9]{2}$/ : HotUtils.validator.requiredEmpty
          });
          var box = !value ? null : boxesByAlias[value];
          if (!box) {
            setData('');
            setOptions({
              source: ['']
            });
          } else {
            setOptions({
              source: box.emptyPositions
            });
          }
        }
      }];
    }
  }

})();
