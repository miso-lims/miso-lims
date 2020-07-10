FormTarget = FormTarget || {};
FormTarget.project = (function($) {

  /*
   * Expected config {
   *   statusOptions: array
   *   naming: {
   *     primary: {
   *       shortNameRequired: boolean
   *       shortNameMofifiable: boolean
   *     },
   *     secondary: { // optional
   *       shortNameRequired: boolean
   *       shortNameMofifiable: boolean
   *     }
   *   }
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('projects');
    },
    getSaveUrl: function(project) {
      return project.id ? Urls.rest.projects.update(project.id) : Urls.rest.projects.create;
    },
    getSaveMethod: function(project) {
      return project.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(project) {
      return Urls.ui.projects.edit(project.id);
    },
    getSections: function(config, object) {
      return [{
        title: "Project Information",
        fields: [{
          title: 'Project ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(project) {
            return project.id || 'Unsaved';
          }
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only',
          getDisplayValue: function(project) {
            return project.name || 'Unsaved';
          }
        }, {
          title: 'Creation Date',
          data: 'creationDate',
          type: 'read-only'
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 100
        }, {
          title: 'Use Secondary Naming Scheme',
          data: 'secondaryNaming',
          type: 'checkbox',
          include: !!config.naming.secondary && !object.id,
          onChange: function(newValue, form) {
            var scheme = config.naming[newValue ? 'secondary' : 'primary'];
            form.updateField('shortName', {
              required: scheme.shortNameRequired,
              disabled: !scheme.shortNameModifiable
            });
          }
        }, {
          title: 'Use Secondary Naming Scheme',
          data: 'secondaryNaming',
          type: 'read-only',
          include: !!config.naming.secondary && object.id,
          getDisplayValue: function(project) {
            return project.secondaryNaming ? 'Yes (Secondary)' : 'No (Primary)';
          }
        }, {
          title: 'Short Name',
          data: 'shortName',
          type: 'text',
          maxLength: 255,
          required: config.naming.primary.shortNameRequired,
          disabled: !config.naming.primary.shortNameModifiable
        }, {
          title: 'REB Number',
          description: 'Research ethics board approval number',
          data: 'rebNumber',
          type: 'text',
          maxLength: 50
        }, {
          title: 'REB Expiry',
          description: 'Expiry date of research ethics board approval',
          data: 'rebExpiry',
          type: 'date'
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Status',
          data: 'status',
          type: 'dropdown',
          required: true,
          source: config.statusOptions
        }, {
          title: 'Reference Genome',
          data: 'referenceGenomeId',
          type: 'dropdown',
          required: 'true',
          source: Constants.referenceGenomes,
          sortSource: Utils.sorting.standardSortWithException('alias', 'Unknown', true),
          getItemLabel: function(item) {
            return item.alias;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Default Targeted Sequencing',
          data: 'defaultTargetedSequencingId',
          type: 'dropdown',
          source: Constants.targetedSequencings,
          sortSource: Utils.sorting.standardSort('alias'),
          getItemLabel: function(item) {
            return item.alias;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Clinical',
          data: 'clinical',
          type: 'checkbox'
        }]
      }];
    }
  };

})(jQuery);
