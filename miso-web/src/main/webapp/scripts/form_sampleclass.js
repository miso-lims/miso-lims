FormTarget = FormTarget || {};
FormTarget.sampleclass = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean
   *   pageMode: string 'create' or 'edit'
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'sample-classes-and-categories');
    },
    getSaveUrl: function(sampleClass) {
      return sampleClass.id ? Urls.rest.sampleClasses.update(sampleClass.id) : Urls.rest.sampleClasses.create;
    },
    getSaveMethod: function(sampleClass) {
      return sampleClass.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(sampleClass) {
      return Urls.ui.sampleClasses.edit(sampleClass.id);
    },
    getSections: function(config, object) {
      return [{
        title: 'Sample Class Information',
        fields: config.isAdmin ? getEditableFields(config, object) : getReadOnlyFields(config, object)
      }];
    },
    confirmSave: function(object, saveCallback) {
      object.parentRelationships = SampleClass.getParents();
      object.childRelationships = SampleClass.getChildren();
      saveCallback();
    }
  }

  function getReadOnlyFields(config, object) {
    $('#save').remove();
    return [{
      title: 'Sample Class ID',
      data: 'id',
      type: 'read-only',
      getDisplayValue: function(sampleClass) {
        return sampleClass.id || 'Unsaved';
      }
    }, {
      title: 'Alias',
      data: 'alias',
      type: 'read-only'
    }, {
      title: 'Category',
      data: 'sampleCategory',
      type: 'read-only'
    }, {
      title: 'Subcategory',
      data: 'sampleSubcategory',
      getDisplayValue: function(sampleClass) {
        return sampleClass.sampleSubcategory || 'n/a';
      },
      type: 'read-only'
    }, {
      title: 'Suffix',
      data: 'suffix',
      getDisplayValue: function(sampleClass) {
        return sampleClass.suffix || 'n/a';
      },
      type: 'read-only'
    }, {
      title: 'V2 Naming Code',
      data: 'v2NamingCode',
      getDisplayValue: function(sampleClass) {
        return sampleClass.v2NamingCode || 'n/a';
      },
      type: 'read-only'
    }, {
      title: 'Receipt Allowed?',
      data: 'directCreationAllowed',
      type: 'checkbox',
      disabled: true
    }, {
      title: 'DNase Treatable?',
      data: 'dnaseTreatable',
      type: 'checkbox',
      disabled: true
    }, {
      title: 'Archived',
      data: 'archived',
      type: 'checkbox',
      disabled: true
    }];
  }

  function getEditableFields(config, object) {
    return [
        {
          title: 'Sample Class ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(sampleClass) {
            return sampleClass.id || 'Unsaved';
          }
        },
        {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 255
        },
        {
          title: 'Category',
          data: 'sampleCategory',
          type: 'dropdown',
          required: true,
          source: Constants.sampleCategories,
          onChange: function(newValue, form) {
            form.updateField('sampleSubcategory', {
              source: newValue ? Constants.sampleSubcategories[newValue] : []
            });
            if ((SampleClass.getParents() && SampleClass.getParents().length)
                || (SampleClass.getChildren() && SampleClass.getChildren().length)) {
              Utils.showOkDialog('Warning', ['Changing sample category may invalidate existing relationships']);
            }
          }
        }, {
          title: 'Subcategory',
          data: 'sampleSubcategory',
          type: 'dropdown',
          source: object.sampleCategory ? Constants.sampleSubcategories[object.sampleCategory] : [],
          nullLabel: 'n/a'
        }, {
          title: 'Suffix',
          data: 'suffix',
          type: 'text',
          maxLength: 5
        }, {
          title: 'V2 Naming Code',
          data: 'v2NamingCode',
          type: 'text',
          maxLength: 2
        }, {
          title: 'Direct Creation Allowed?',
          data: 'directCreationAllowed',
          type: 'checkbox'
        }, {
          title: 'DNase Treatable?',
          data: 'dnaseTreatable',
          type: 'checkbox'
        }, {
          title: 'Archived',
          data: 'archived',
          type: 'checkbox'
        }];
  }

})(jQuery);
