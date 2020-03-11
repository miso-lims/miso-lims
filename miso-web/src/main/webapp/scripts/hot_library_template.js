HotTarget.libraryTemplate = (function() {

  var getDesign = function(name) {
    if (!name) {
      return null;
    }
    return Utils.array.findFirstOrNull(Utils.array.namePredicate(name.replace(/\s-.*/, "")), Constants.libraryDesigns);
  };

  var getDesignDisplayName = function(design) {
    var sampleClass = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(design.sampleClassId),
        Constants.sampleClasses), 'alias');
    design.displayName = design.name + ' - ' + (sampleClass || 'Unknown');
    return design;
  }

  const none = '(Unspecified)';

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('libraries', 'library-templates');
    },
    getCreateUrl: function() {
      return Urls.rest.libraryTemplates.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.libraryTemplates.update(id);
    },
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(librarytemplate, errorHandler) {
    },
    createColumns: function(config, create, data) {
      var platformTypes = Constants.platformTypes.filter(function(pt) {
        return pt.active || data.reduce(function(acc, libTemp) {
          return acc || pt.name == libTemp.platformType;
        }, false);
      }).map(function(pt) {
        return pt.key;
      });
      platformTypes.unshift(none);
      return [
          HotUtils.makeColumnForText('Alias', true, 'alias', {
            unpackAfterSave: true,
            validator: HotUtils.validator.requiredText
          }),
          HotUtils.makeColumnForConstantsList('Design', Constants.isDetailedSample, 'libraryDesignAlias', 'designId', 'id', 'displayName',
              Constants.libraryDesigns.map(getDesignDisplayName), false, {}, null, none),
          HotUtils.makeColumnForConstantsList('Code', Constants.isDetailedSample, 'libraryDesignCode', 'designCodeId', 'id', 'code',
              Constants.libraryDesignCodes, false, {
                depends: 'libraryDesignAlias',
                update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  HotUtils.updateFromTemplateOrDesign(design, null, 'designCodeId', Constants.libraryDesignCodes, 'code', setReadOnly,
                      setData);
                }
              }, null, none),
          {
            header: 'Platform',
            data: 'platformType',
            type: 'dropdown',
            trimDropdown: false,
            source: platformTypes,
            validator: HotUtils.validator.requiredAutocomplete,
            include: true,
            unpack: function(libTemp, flat, setCellMeta) {
              flat.platformType = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.namePredicate(libTemp.platformType),
                  Constants.platformTypes), 'key')
                  || none;
            },
            pack: function(libTemp, flat, errorHandler) {
              libTemp.platformType = flat.platformType == none ? null : HotUtils.getPlatformType(flat.platformType);
            },
          },
          {
            header: 'Type',
            data: 'libraryTypeDescription',
            type: 'dropdown',
            trimDropdown: false,
            source: [none],
            validator: HotUtils.validator.requiredAutocomplete,
            include: true,
            depends: 'platformType',
            unpack: function(libTemp, flat, setCellMeta) {
              flat.libraryTypeDescription = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .idPredicate(libTemp.libraryTypeId), Constants.libraryTypes), 'description')
                  || none;
            },
            'pack': function(libTemp, flat, errorHander) {
              libTemp.libraryTypeId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.description == flat.libraryTypeDescription;
              }, Constants.libraryTypes), 'id');
            },
            update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var pt = HotUtils.getPlatformType(flat.platformType);
              var types = Constants.libraryTypes.filter(function(lt) {
                return lt.platform == pt && (!lt.archived || libTemp.libraryTypeId == lt.id);
              }).map(function(lt) {
                return lt.description;
              }).sort();
              types.unshift(none);
              setOptions({
                'source': types
              });
            }

          },
          HotUtils.makeColumnForConstantsList('Selection', true, 'librarySelectionTypeAlias', 'selectionId', 'id', 'name',
              Constants.librarySelections, false, {
                depends: 'libraryDesignAlias',
                update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  HotUtils.updateFromTemplateOrDesign(design, null, 'selectionId', Constants.librarySelections, 'name', setReadOnly,
                      setData);
                }
              }, null, none),
          HotUtils.makeColumnForConstantsList('Strategy', true, 'libraryStrategyTypeAlias', 'strategyId', 'id', 'name',
              Constants.libraryStrategies, false, {
                depends: 'libraryDesignAlias',
                update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  HotUtils
                      .updateFromTemplateOrDesign(design, null, 'strategyId', Constants.libraryStrategies, 'name', setReadOnly, setData);
                }
              }, null, none),
          {
            header: 'Kit',
            data: 'kitDescriptorName',
            type: 'dropdown',
            trimDropdown: false,
            validator: HotUtils.validator.requiredAutocomplete,
            source: [none],
            include: true,
            unpack: function(libTemp, flat, setCellMeta) {
              flat.kitDescriptorName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .idPredicate(libTemp.kitDescriptorId), Constants.kitDescriptors), 'name')
                  || none;
            },
            pack: function(libTemp, flat, errorHandler) {
              libTemp.kitDescriptorId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(kit) {
                return kit.platformType == flat.platformType && kit.kitType == 'Library' && kit.name == flat.kitDescriptorName;
              }, Constants.kitDescriptors), 'id');
            },
            depends: 'platformType',
            update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var kits = Constants.kitDescriptors.filter(function(kit) {
                return kit.platformType == flat.platformType && kit.kitType == 'Library';
              }).map(Utils.array.getName).sort()
              kits.unshift(none);
              setOptions({
                'source': kits
              });
            }
          },
          {
            header: 'Index Kit',
            data: 'indexFamilyName',
            type: 'dropdown',
            trimDropdown: false,
            validator: HotUtils.validator.requiredAutocomplete,
            source: [none],
            include: true,
            unpack: function(libTemp, flat, setCellMeta) {
              flat.indexFamilyName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(indexFamily) {
                return !Utils.validation.isEmpty(libTemp.indexFamilyId) && indexFamily.id == libTemp.indexFamilyId;
              }, Constants.indexFamilies), 'name') || none;
            },
            pack: function(libTemp, flat, errorHandler) {
              libTemp.indexFamilyId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(indexFamily) {
                return indexFamily.name == flat.indexFamilyName;
              }, Constants.indexFamilies), 'id');
            },
            depends: 'platformType',
            update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var pt = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
                return platformType.key == flat.platformType;
              }, Constants.platformTypes), 'name');
              if (!pt) {
                setOptions({
                  'source': [none]
                });
              } else {
                setOptions({
                  'source': [none].concat(Constants.indexFamilies.filter(
                      function(family) {
                        return family.platformType == pt
                            && (!family.archived || Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(indexFamily) {
                              return indexFamily.id == libTemp.indexFamilyId;
                            }, Constants.indexFamilies), 'name') === family.name);
                      }).map(function(family) {
                    return family.name;
                  }).sort())
                });
              }
            }
          }, HotUtils.makeColumnForDecimal('Default Volume', true, 'defaultVolume', 14, 10, false, false), {
            header: 'Vol. Units',
            data: 'volumeUnits',
            type: 'dropdown',
            trimDropdown: false,
            source: [none].concat(Constants.volumeUnits.map(function(unit) {
              return unit.units;
            })),
            include: true,
            allowHtml: true,
            validator: HotUtils.validator.requiredAutocomplete,
            unpack: function(obj, flat, setCellMeta) {
              var units = Constants.volumeUnits.find(function(unit) {
                return unit.name == obj.volumeUnits;
              });
              flat['volumeUnits'] = !!units ? units.units : none;
            },
            pack: function(obj, flat, errorHandler) {
              var units = Constants.volumeUnits.find(function(unit) {
                return unit.units == flat['volumeUnits'];
              });
              obj['volumeUnits'] = !!units ? units.name : null;
            }
          }];
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = Urls.ui.libraryTemplates.bulkEdit + '?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }, ];
    },
  };
})();
