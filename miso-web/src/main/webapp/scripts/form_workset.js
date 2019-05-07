if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.workset = (function($) {

  return {
    getSaveUrl: function(workset) {
      return workset.id ? ('/miso/rest/worksets/' + workset.id) : '/miso/rest/worksets';
    },
    getSaveMethod: function(workset) {
      return workset.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(workset) {
      return '/miso/workset/' + workset.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Workset Information',
        fields: [{
          title: 'Workset ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(workset) {
            return workset.id || 'Unsaved';
          }
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 100
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Created By',
          data: 'creator',
          type: 'read-only',
          include: !!object.creator
        }]
      }];
    }
  }

})(jQuery);