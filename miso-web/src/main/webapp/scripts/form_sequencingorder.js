if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.sequencingorder = (function ($) {
  return {
    getSaveUrl: function (order) {
      return order.id
        ? Urls.rest.sequencingOrders.update(order.id)
        : Urls.rest.sequencingOrders.create;
    },
    getSaveMethod: function (order) {
      return order.id ? "PUT" : "POST";
    },
    getEditUrl: function (order) {
      throw new Error("Sequencing orders do not have an edit page");
    },
    getSections: function (config, object) {
      return [
        {
          title: "Sequencing Order Information",
          fields: [
            {
              title: "Purpose",
              data: "purposeId",
              type: "read-only",
              getDisplayValue: function (order) {
                return order.purposeAlias;
              },
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Sequencing Parameters",
              data: "parameters",
              omit: true,
              type: "read-only",
              getDisplayValue: function (order) {
                return order.parameters.name + " (" + order.parameters.instrumentModelAlias + ")";
              },
            },
            {
              title: "Container Model",
              data: "containerModelId",
              omit: true,
              type: "read-only",
              getDisplayValue: function (order) {
                if (order.containerModelId) {
                  return Utils.array.findUniqueOrThrow(
                    Utils.array.idPredicate(order.containerModelId),
                    Constants.containerModels
                  ).alias;
                } else {
                  return "Unspecified";
                }
              },
            },
            {
              title: "Partitions Required",
              data: "partitions",
              type: "read-only",
            },
          ],
        },
      ];
    },
  };
})(jQuery);
