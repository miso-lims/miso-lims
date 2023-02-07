if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.user = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean
   *   isSelf: boolean
   *   usersEditable: boolean
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("users_and_groups", "users");
    },
    getSaveUrl: function (user) {
      return user.id ? Urls.rest.users.update(user.id) : Urls.rest.users.create;
    },
    getSaveMethod: function (user) {
      return user.id ? "PUT" : "POST";
    },
    getEditUrl: function (user, config) {
      return config.isAdmin ? Urls.ui.users.edit(user.id) : Urls.ui.users.editSelf(user.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "User Information",
          fields: config.usersEditable
            ? getEditableFields(config, object)
            : getReadOnlyFields(config, object),
        },
      ];
    },
  };

  function getEditableFields(config, object) {
    return [
      {
        title: "User ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (user) {
          return user.id || "Unsaved";
        },
      },
      {
        title: "Full Name",
        data: "fullName",
        type: "text",
        required: true,
        maxLength: 255,
      },
      {
        title: "Login Name",
        data: "loginName",
        type: "text",
        required: true,
        maxLength: 255,
        disabled: !config.isAdmin,
      },
      {
        title: "Password",
        data: "password",
        type: "password",
        required: true,
        maxLength: 100,
        include: !object.id,
      },
      {
        title: "Confirm Password",
        data: "passwordConfirm",
        omit: true,
        type: "password",
        required: true,
        maxLength: 100,
        include: !object.id,
        match: "password",
      },
      {
        title: "Email Address",
        data: "email",
        type: "text",
        required: true,
        maxLength: 255,
        regex: "email",
      },
      {
        title: "Admin?",
        data: "admin",
        type: "checkbox",
        disabled: !config.isAdmin || config.isSelf,
      },
      {
        title: "Internal?",
        description: "Only internal users can log into MISO",
        data: "internal",
        type: "checkbox",
        disabled: !config.isAdmin,
      },
      {
        title: "Active?",
        data: "active",
        type: "checkbox",
        disabled: !config.isAdmin || config.isSelf,
      },
    ];
  }

  function getReadOnlyFields(config, object) {
    $("#save").remove();
    return [
      {
        title: "User ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (user) {
          return user.id || "Unsaved";
        },
      },
      {
        title: "Full Name",
        data: "fullName",
        type: "read-only",
      },
      {
        title: "Login Name",
        data: "loginName",
        type: "read-only",
      },
      {
        title: "Email Address",
        data: "email",
        type: "read-only",
      },
      {
        title: "Admin?",
        data: "admin",
        type: "checkbox",
        disabled: true,
      },
      {
        title: "Internal?",
        data: "internal",
        type: "checkbox",
        disabled: true,
      },
      {
        title: "Active?",
        data: "active",
        type: "checkbox",
        disabled: true,
      },
    ];
  }
})(jQuery);

FormTarget.passwordreset = (function ($) {
  /*
   * Expected config {
   *   userId: int
   *   isAdmin: boolean
   *   isSelf: boolean
   * }
   */

  return {
    getSaveUrl: function (object, config) {
      return Urls.rest.users.resetPassword(config.userId);
    },
    getSaveMethod: function () {
      return "POST";
    },
    getEditUrl: function (user, config) {
      return config.isAdmin ? Urls.ui.users.edit(user.id) : Urls.ui.users.editSelf(user.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Reset Password",
          fields: [
            {
              title: "Old Password",
              data: "oldPassword",
              type: "password",
              required: true,
              maxLength: 100,
              include: !config.isAdmin || config.isSelf,
            },
            {
              title: "New Password",
              data: "newPassword",
              type: "password",
              required: true,
              maxLength: 100,
            },
            {
              title: "Confirm Password",
              data: "passwordConfirm",
              omit: true,
              type: "password",
              required: true,
              maxLength: 100,
              match: "newPassword",
            },
          ],
        },
      ];
    },
  };
})(jQuery);
