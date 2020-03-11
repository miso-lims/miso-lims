FormTarget = FormTarget || {};
FormTarget.indexfamily = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean
   *   pageMode: string ['create', 'edit']
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'indices');
    },
    getSaveUrl: function(family) {
      return family.id ? Urls.rest.indexFamilies.update(family.id) : Urls.rest.indexFamilies.create;
    },
    getSaveMethod: function(family) {
      return family.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(family) {
      return Urls.ui.indexFamilies.edit(family.id);
    },
    getSections: function(config, object) {
      return [{
        title: 'Index Family Information',
        fields: config.isAdmin ? getEditableFields(config, object) : getReadOnlyFields(config, object)
      }];
    }
  }

  function getEditableFields(config, object) {
    return [{
      title: 'Index Family ID',
      data: 'id',
      type: 'read-only',
      getDisplayValue: function(family) {
        return family.id || 'Unsaved';
      }
    }, {
      title: 'Name',
      data: 'name',
      type: 'text',
      required: true,
      maxLength: 255
    }, {
      title: 'Platform',
      data: 'platformType',
      type: 'dropdown',
      include: config.pageMode === 'create',
      required: true,
      source: Constants.platformTypes,
      getItemLabel: function(item) {
        return item.key;
      },
      getItemValue: Utils.array.getName
    }, {
      title: 'Platform',
      data: 'platformType',
      include: config.pageMode !== 'create',
      type: 'read-only',
      getDisplayValue: function(family) {
        return Utils.array.findUniqueOrThrow(Utils.array.namePredicate(family.platformType), Constants.platformTypes).key;
      }
    }, {
      title: 'Multi-Sequence Indices',
      data: 'fakeSequence',
      type: 'checkbox',
      disabled: config.pageMode !== 'create'
    }, {
      title: 'Unique Dual Indices',
      data: 'uniqueDualIndex',
      type: 'checkbox'
    }, {
      title: 'Archived',
      data: 'archived',
      type: 'checkbox'
    }];
  }

  function getReadOnlyFields(config, object) {
    $('#save').remove();
    return [{
      title: 'Index Family ID',
      data: 'id',
      type: 'read-only',
      getDisplayValue: function(family) {
        return family.id || 'Unsaved';
      }
    }, {
      title: 'Name',
      data: 'name',
      type: 'read-only'
    }, {
      title: 'Platform',
      data: 'platformType',
      type: 'read-only',
      getDisplayValue: function(family) {
        return Utils.array.findUniqueOrThrow(Utils.array.namePredicate(family.platformType), Constants.platformTypes).key;
      }
    }, {
      title: 'Multi-Sequence Indices',
      data: 'fakeSequence',
      type: 'checkbox',
      disabled: true
    }, {
      title: 'Unique Dual Indices',
      data: 'uniqueDualIndex',
      type: 'checkbox',
      disabled: true
    }, {
      title: 'Archived',
      data: 'archived',
      type: 'checkbox',
      disabled: true
    }];
  }

})(jQuery);
