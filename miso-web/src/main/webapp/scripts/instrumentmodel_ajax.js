var InstrumentModel = (function ($) {
  var positionsContainerSelector = "#listInstrumentPositionsContainer";
  var positionsListId = "listInstrumentPositions";
  var positionsListSelector = "#" + positionsListId;

  var containersContainerSelector = "#listContainerModelsContainer";
  var containersListId = "listContainerModels";
  var containersListSelector = "#" + containersListId;

  var positionsListInitialized = false;
  var containersListInitialized = false;
  var form = null;
  var isAdmin = false;

  var temporaryIdCounter = 0;

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setAdmin: function (admin) {
      isAdmin = admin;
    },

    getInstrumentPositions: function () {
      if (!positionsListInitialized) {
        return null;
      }
      return $(positionsListSelector)
        .dataTable()
        .fnGetData()
        .map(function (pos) {
          // remove temporary IDs
          if (pos.id < 0) {
            pos.id = null;
          }
          return pos;
        });
    },

    setInstrumentPositions: function (positions) {
      if (positionsListInitialized) {
        form.markOtherChanges();
        $(positionsListSelector).dataTable().fnDestroy();
        $(positionsContainerSelector).empty();
        ListState[positionsListId] = null;
      }
      // IDs needed for list checkboxes to work correctly
      positions.forEach(function (position) {
        if (!position.id) {
          position.id = generateTemporaryId();
        }
      });
      $(positionsContainerSelector).append(
        $("<table>").attr("id", positionsListId).addClass("display no-border ui-widget-content")
      );
      ListUtils.createStaticTable(
        positionsListId,
        ListTarget.instrumentposition,
        {
          isAdmin: isAdmin,
        },
        positions
      );
      positionsListInitialized = true;
    },

    addInstrumentPosition: function (alias) {
      var positions = InstrumentModel.getInstrumentPositions();
      positions.push({
        alias: alias,
      });
      InstrumentModel.setInstrumentPositions(positions);
    },

    removeInstrumentPositions: function (aliases) {
      var positions = InstrumentModel.getInstrumentPositions().filter(function (pos) {
        return aliases.indexOf(pos.alias) === -1;
      });
      InstrumentModel.setInstrumentPositions(positions);
    },

    getContainerModels: function () {
      if (!containersListInitialized) {
        return null;
      }
      return $(containersListSelector).dataTable().fnGetData();
    },

    setContainerModels: function (models) {
      if (containersListInitialized) {
        form.markOtherChanges();
        $(containersListSelector).dataTable().fnDestroy();
        $(containersContainerSelector).empty();
        ListState[containersListId] = null;
      }
      $(containersContainerSelector).append(
        $("<table>").attr("id", containersListId).addClass("display no-border ui-widget-content")
      );
      ListUtils.createStaticTable(
        containersListId,
        ListTarget.containermodel,
        {
          isAdmin: isAdmin,
          isInstrumentModelPage: true,
        },
        models
      );
      containersListInitialized = true;
    },

    addContainerModel: function (model) {
      var models = InstrumentModel.getContainerModels();
      models.push(model);
      InstrumentModel.setContainerModels(models);
    },

    removeContainerModels: function (ids) {
      var models = InstrumentModel.getContainerModels().filter(function (model) {
        return ids.indexOf(model.id) === -1;
      });
      InstrumentModel.setContainerModels(models);
    },
  };

  function generateTemporaryId() {
    temporaryIdCounter--;
    return temporaryIdCounter;
  }
})(jQuery);
