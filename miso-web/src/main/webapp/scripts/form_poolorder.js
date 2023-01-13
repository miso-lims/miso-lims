if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.poolorder = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("pool_orders");
    },
    getSaveUrl: function (order) {
      return order.id ? Urls.rest.poolOrders.update(order.id) : Urls.rest.poolOrders.create;
    },
    getSaveMethod: function (order) {
      return order.id ? "PUT" : "POST";
    },
    getEditUrl: function (order) {
      return Urls.ui.poolOrders.edit(order.id);
    },
    getSections: function (config, object) {
      var platform = getPlatform(object);
      var instrumentModel = null;
      if (object.parametersId) {
        var params = Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(object.parametersId),
          Constants.sequencingParameters
        );
        instrumentModel = Utils.array.findUniqueOrThrow(
          Utils.array.idPredicate(params.instrumentModelId),
          Constants.instrumentModels
        );
      }

      return [
        {
          title: "Order Information",
          fields: [
            {
              title: "Pool Order ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (order) {
                return order.id || "Unsaved";
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 100,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Purpose",
              data: "purposeId",
              type: "dropdown",
              required: true,
              source: Constants.runPurposes,
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("alias"),
            },
            {
              title: "Specify sequencing requirements?",
              data: "sequencingRequirements",
              type: "checkbox",
              omit: true,
              initial: !!object.partitions,
              onChange: function (newValue, form) {
                var opts = {
                  disabled: !newValue,
                  required: newValue,
                };
                if (!newValue) {
                  opts.value = null;
                }
                ["platform", "instrumentModel", "parametersId", "partitions"].forEach(function (
                  field
                ) {
                  form.updateField(field, opts);
                });
                form.updateField("containerModelId", {
                  disabled: !newValue,
                  required: newValue && (!object.id || !!object.containerModelId),
                });
              },
            },
            {
              title: "Platform",
              data: "platform",
              omit: true,
              type: "dropdown",
              source: Constants.platformTypes.filter(function (pt) {
                return pt.active;
              }),
              getItemLabel: function (item) {
                return item.key;
              },
              getItemValue: Utils.array.getName,
              nullLabel: "Unspecified",
              include: !platform,
              onChange: function (newValue, form) {
                var platform = !newValue
                  ? null
                  : Utils.array.findUniqueOrThrow(
                      Utils.array.namePredicate(newValue),
                      Constants.platformTypes
                    );
                updateInstrumentModels(platform, form.updateField);
              },
            },
            {
              title: "Platform",
              data: "platform",
              omit: true,
              type: "read-only",
              initial: platform ? platform.key : null,
              include: !!platform,
            },
            {
              title: "Instrument Model",
              data: "instrumentModel",
              omit: true,
              type: "dropdown",
              initial: instrumentModel ? instrumentModel.id : null,
              source: !platform
                ? []
                : Constants.instrumentModels.filter(function (model) {
                    return (
                      model.platformType === platform.name && model.instrumentType === "SEQUENCER"
                    );
                  }),
              sortSource: Utils.sorting.standardSort("alias"),
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              nullLabel: "Unspecified",
              onChange: function (newValue, form) {
                if (newValue) {
                  // Update container models
                  var selected = Utils.array.findUniqueOrThrow(
                    Utils.array.idPredicate(newValue),
                    Constants.instrumentModels
                  );
                  var containerModelOptions = {
                    source: selected.containerModels,
                  };
                  if (
                    object.containerModelId &&
                    selected.containerModels.find(Utils.array.idPredicate(object.containerModelId))
                  ) {
                    containerModelOptions.value = object.containerModelId;
                  }
                  form.updateField("containerModelId", containerModelOptions);

                  // Update sequencing parameters
                  var paramSource = Constants.sequencingParameters.filter(function (params) {
                    return params.instrumentModelId == newValue;
                  });
                  var paramOptions = {
                    source: paramSource,
                  };
                  if (
                    object.parametersId &&
                    paramSource.find(Utils.array.idPredicate(object.parametersId))
                  ) {
                    paramOptions.value = object.parametersId;
                  }
                  form.updateField("parametersId", paramOptions);
                } else {
                  form.updateField("parametersId", {
                    source: [],
                  });
                  form.updateField("containerModelId", {
                    source: [],
                  });
                }
              },
            },
            {
              title: "Container Model",
              data: "containerModelId",
              type: "dropdown",
              source: [],
              sortSource: Utils.sorting.standardSort("alias"),
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              nullLabel: object.containerModelId ? undefined : "Unspecified",
            },
            {
              title: "Sequencing Parameters",
              data: "parametersId",
              type: "dropdown",
              source: [],
              sortSource: Utils.sorting.standardSort("name"),
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.getId,
              nullLabel: "Unspecified",
            },
            {
              title: "Partitions Required",
              data: "partitions",
              type: "int",
              min: 1,
            },
            {
              title: "Draft",
              data: "draft",
              type: "checkbox",
            },
            {
              title: "Status",
              data: "status",
              type: "read-only",
              include: !!object.id,
            },
            {
              title: "Pool",
              data: "poolId",
              type: "read-only",
              include: !!object.id,
              getDisplayValue: function (order) {
                return order.poolId ? order.poolAlias : "not linked";
              },
              getLink: function (object) {
                return object.poolId ? Urls.ui.pools.edit(object.poolId) : null;
              },
            },
            {
              title: "Sequencing Order",
              data: "sequencingOrderId",
              type: "read-only",
              include: !!object.id,
              getDisplayValue: function (order) {
                if (!order.partitions) {
                  return "n/a";
                }
                return order.sequencingOrderId ? "linked" : "not linked";
              },
            },
            {
              title: "Fulfillment",
              type: "special",
              include: !!object.id && !object.draft,
              makeControls: makeFulfilControls(object),
            },
          ],
        },
      ];
    },
    confirmSave: function (object) {
      object.orderAliquots = PoolOrder.getAliquots();
    },
  };

  function getPlatform(order) {
    var platformName = null;
    if (order.parametersId) {
      var params = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(order.parametersId),
        Constants.sequencingParameters
      );
      var instrumentModel = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(params.instrumentModelId),
        Constants.instrumentModels
      );
      platformName = instrumentModel.platformType;
    } else if (order.libraries && order.libraries.length) {
      platformName = order.libraries[0].library.platformType;
    }
    return platformName == null
      ? null
      : Utils.array.findUniqueOrThrow(
          Utils.array.namePredicate(platformName),
          Constants.platformTypes
        );
  }

  function updateInstrumentModels(platform, updateField, selectedModel) {
    var options = {
      source: !platform
        ? []
        : Constants.instrumentModels.filter(function (model) {
            return model.platformType === platform.name && model.instrumentType === "SEQUENCER";
          }),
    };
    if (selectedModel && source.some(Utils.array.idPredicate(selectedModel.id))) {
      options.value = selectedModel.id;
    }
    updateField("instrumentModel", options);
  }

  function makeFulfilControls(object) {
    return function (form) {
      var controls = [];
      if (object.poolId) {
        if (object.sequencingOrderId) {
          controls.push(makeButton("Unlink Sequencing Order", unlinkSequencingOrder, form));
        } else {
          controls.push(makeButton("Unlink Pool", unlinkPool, form));
          if (object.status !== "Fulfilled")
            controls.push(
              makeButton("Create Sequencing Order", createSequencingOrder, form),
              makeButton("Link Sequencing Order", linkSequencingOrder, form)
            );
        }
      } else {
        controls.push(
          makeButton("Create Pool", createPool, form),
          makeButton("Link Pool", linkPool, form)
        );
      }
      return controls;
    };
  }

  function makeButton(text, onclick, form) {
    return $("<button>")
      .addClass("ui-state-default")
      .attr("type", "button")
      .text(text)
      .click(ifSaved(form, onclick))
      .after(" ");
  }

  function ifSaved(form, callback) {
    return function () {
      if (form.isChanged()) {
        Utils.showOkDialog("Error", ["Please save your changes first"]);
      } else {
        callback(form);
      }
    };
  }

  function createPool(form) {
    var orderAliquots = PoolOrder.getAliquots();
    if (!orderAliquots || !orderAliquots.length) {
      Utils.showOkDialog("Error", ["Please add aliquots before creating a pool"]);
      return;
    }
    Utils.showConfirmDialog(
      "Create Pool",
      "Yes",
      ["Pool will contain the following aliquots and proportions."]
        .concat(
          orderAliquots.map(function (orderAliquot) {
            return "*" + orderAliquot.aliquot.alias + " (" + orderAliquot.proportion + ")";
          })
        )
        .concat(["Is this correct?"]),
      function () {
        var platformTypeKey = orderAliquots[0].aliquot.libraryPlatformType;
        var pool = {
          platformType: Utils.array.findUniqueOrThrow(function (pt) {
            return pt.key === platformTypeKey;
          }, Constants.platformTypes).name,
          pooledElements: orderAliquots.map(function (orderAliquot) {
            var aliquot = orderAliquot.aliquot;
            aliquot.proportion = orderAliquot.proportion;
            return aliquot;
          }),
        };
        FormUtils.createFormDialog("Create Pool", pool, "pool", {}, function (pool) {
          setPool(form, pool);
        });
      }
    );
  }

  function linkPool(form) {
    Utils.showDialog(
      "Fulfil Pool Order",
      "Search",
      [
        {
          label: "Pool name, alias, or barcode",
          required: true,
          type: "text",
          property: "query",
        },
      ],
      function (results) {
        Utils.ajaxWithDialog(
          "Searching...",
          "GET",
          Urls.rest.pools.search +
            "?" +
            Utils.page.param({
              q: results.query,
            }),
          null,
          function (data) {
            if (!data || !data.length) {
              Utils.showOkDialog("Results", ["No pools found"]);
            } else if (data.length === 1) {
              setPool(form, data[0]);
            } else {
              Utils.showWizardDialog(
                "Choose Pool",
                data.map(function (pool) {
                  return {
                    name: pool.name + " (" + pool.alias + ")",
                    handler: function () {
                      setPool(form, pool);
                    },
                  };
                })
              );
            }
          }
        );
      }
    );
  }

  function setPool(form, pool) {
    var notFound = [];
    var wrongProportions = [];
    PoolOrder.getAliquots().forEach(function (orderAli) {
      var poolAli = Utils.array.findFirstOrNull(function (pooledElement) {
        return (
          pooledElement.id === orderAli.aliquot.id ||
          (pooledElement.parentAliquotIds &&
            pooledElement.parentAliquotIds.indexOf(orderAli.aliquot.id) !== -1)
        );
      }, pool.pooledElements);
      if (!poolAli) {
        notFound.push(orderAli);
      } else if (poolAli.proportion !== poolAli.proportion) {
        wrongProportions.push(poolAli);
      }
    });
    if (notFound.length) {
      Utils.showOkDialog(
        "Error",
        ["Selected pool does not contain the following library aliquots"].concat(
          notFound.map(function (notFoundAli) {
            return "* " + notFoundAli.aliquot.name + " (" + notFoundAli.aliquot.alias + ")";
          })
        )
      );
    } else if (wrongProportions.length) {
      Utils.showConfirmDialog(
        "Confirm Proportions",
        "Confirm",
        [
          "Selected pool contains all of the required library aliquots, " +
            "but the following are at different proportions"
              .concat(
                wrongProportions.map(function (wrongAli) {
                  return "* " + wrongAli.name + " (" + wrongAli.alias + "): " + wrongAli.proportion;
                })
              )
              .concat(["Are you sure you wish to use this pool?"]),
        ],
        doSetPool(form, pool)
      );
    } else {
      doSetPool(form, pool);
    }
  }

  function doSetPool(form, pool) {
    if (pool && (pool.duplicateIndices || pool.nearDuplicateIndices)) {
      Utils.showOkDialog("Error", ["Selected pool contains duplicate or near duplicate indices."]);
    } else {
      form.updateField("poolId", {
        value: pool ? pool.id : null,
        label: pool ? pool.alias : "not linked",
        link: pool ? Urls.ui.pools.edit(pool.id) : null,
      });
      form.save();
    }
  }

  function unlinkPool(form) {
    Utils.showConfirmDialog(
      "Unlink Pool",
      "Yes",
      ["Are you sure you wish to unlink the pool from this order? The order will be unfulfilled."],
      function () {
        doSetPool(form, null);
      }
    );
  }

  function linkSequencingOrder(form) {
    Utils.ajaxWithDialog(
      "Finding Sequencing Orders",
      "GET",
      Urls.rest.sequencingOrders.search +
        "?" +
        Utils.page.param({
          poolId: form.get("poolId"),
          purposeId: form.get("purposeId"),
          containerModelId: form.get("containerModelId"),
          parametersId: form.get("parametersId"),
          partitions: form.get("partitions"),
        }),
      null,
      function (sequencingOrders) {
        switch (sequencingOrders.length) {
          case 0:
            Utils.showOkDialog("Link Sequencing Order", ["No matching sequencing orders found"]);
            break;
          case 1:
            Utils.showConfirmDialog(
              "Link Sequencing Order",
              "Link",
              ["1 matching sequencing order found. Link to pool order?"],
              function () {
                setSequencingOrder(form, sequencingOrders[0]);
              }
            );
            break;
          default:
            Utils.showWizardDialog(
              "Link Sequencing Order",
              sequencingOrders.map(function (sequencingOrder) {
                return {
                  name:
                    "Sequencing order " +
                    sequencingOrder.id +
                    (sequencingOrder.description ? " - " + sequencingOrder.description : ""),
                  handler: function () {
                    setSequencingOrder(form, sequencingOrder);
                  },
                };
              })
            );
        }
      }
    );
  }

  function createSequencingOrder(form) {
    var purpose = Utils.array.findUniqueOrThrow(
      Utils.array.idPredicate(form.get("purposeId")),
      Constants.runPurposes
    );
    var sequencingOrder = {
      purposeId: purpose.id,
      purposeAlias: purpose.alias,
      containerModelId: form.get("containerModelId"),
      parameters: Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(form.get("parametersId")),
        Constants.sequencingParameters
      ),
      partitions: form.get("partitions"),
      pool: {
        id: form.get("poolId"),
      },
    };
    FormUtils.createFormDialog(
      "Create Sequencing Order",
      sequencingOrder,
      "sequencingorder",
      {},
      function (result) {
        setSequencingOrder(form, result);
      }
    );
  }

  function unlinkSequencingOrder(form) {
    Utils.showConfirmDialog(
      "Unlink Sequencing Order",
      "Yes",
      [
        "Are you sure you wish to unlink the sequencing order from this pool order? The order will be unfulfilled.",
      ],
      function () {
        setSequencingOrder(form, null);
      }
    );
  }

  function setSequencingOrder(form, sequencingOrder) {
    form.updateField("sequencingOrderId", {
      value: sequencingOrder ? sequencingOrder.id : null,
      label: sequencingOrder ? "linked" : "not linked",
    });
    form.save();
  }
})(jQuery);
