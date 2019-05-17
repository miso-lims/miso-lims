if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.user = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean
   *   isSelf: boolean
   * }
   */

  return {
    getSaveUrl: function(user) {
      return user.id ? ('/miso/rest/users/' + user.id) : '/miso/rest/users';
    },
    getSaveMethod: function(user) {
      return user.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(user) {
      return '/miso/admin/user/' + user.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'User Information',
        fields: [{
          title: 'User ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(user) {
            return user.id || 'Unsaved';
          }
        }, {
          title: 'Full Name',
          data: 'fullName',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Login Name',
          data: 'loginName',
          type: 'text',
          required: true,
          maxLength: 255,
          disabled: !config.isAdmin
        }, {
          title: 'Password',
          data: 'password',
          type: 'password',
          required: true,
          maxLength: 100,
          include: !object.id
        }, {
          title: 'Confirm Password',
          data: 'passwordConfirm',
          omit: true,
          type: 'password',
          required: true,
          maxLength: 100,
          include: !object.id,
          match: 'password'
        }, {
          title: 'Email Address',
          data: 'email',
          type: 'text',
          required: true,
          maxLength: 255,
          regex: 'email'
        }, {
          title: 'Admin?',
          data: 'admin',
          type: 'checkbox',
          disabled: !config.isAdmin || config.isSelf
        }, {
          title: 'Internal?',
          data: 'internal',
          type: 'checkbox',
          disabled: !config.isAdmin
        }, {
          title: 'External?',
          data: 'external',
          type: 'checkbox',
          disabled: !config.isAdmin
        }, {
          title: 'Active?',
          data: 'active',
          type: 'checkbox',
          disabled: !config.isAdmin || config.isSelf
        }]
      }];
    }
  }

})(jQuery);

FormTarget.passwordreset = (function($) {

  /*
   * Expected config {
   *   userId: int
   *   isAdmin: boolean
   *   isSelf: boolean
   * }
   */

  return {
    getSaveUrl: function(object, config) {
      return '/miso/rest/users/' + config.userId + '/password';
    },
    getSaveMethod: function() {
      return 'POST';
    },
    getEditUrl: function(user, config) {
      return '/miso' + (config.isAdmin ? '/admin' : '') + '/user/' + user.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Reset Password',
        fields: [{
          title: 'Old Password',
          data: 'oldPassword',
          type: 'password',
          required: true,
          maxLength: 100,
          include: !config.isAdmin || config.isSelf
        }, {
          title: 'New Password',
          data: 'newPassword',
          type: 'password',
          required: true,
          maxLength: 100
        }, {
          title: 'Confirm Password',
          data: 'passwordConfirm',
          omit: true,
          type: 'password',
          required: true,
          maxLength: 100,
          match: 'newPassword'
        }]
      }];
    }
  }

})(jQuery);
