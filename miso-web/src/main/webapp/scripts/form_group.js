if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.group = (function($) {

  return {
    getSaveUrl: function(group) {
      return group.id ? ('/miso/rest/groups/' + group.id) : '/miso/rest/groups';
    },
    getSaveMethod: function(group) {
      return group.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(group) {
      return '/miso/admin/group/' + group.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Group Information',
        fields: [{
          title: 'Group ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(group) {
            return group.id || 'Unsaved';
          }
        }, {
          title: 'Name',
          data: 'name',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }]
      }];
    }
  }

})(jQuery);
