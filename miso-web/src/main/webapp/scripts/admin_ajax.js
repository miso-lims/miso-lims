/**
 * Catches and logs errors.
 */
window.addEventListener("error", function (e) {
  var error = e.error;
  console.log(error);
});

var Admin = Admin || {
  clearCache: function () {
    Utils.showConfirmDialog("Hibernate Cache", "Clear", ["Clear Hibernate cache?"], function () {
      Utils.ajaxWithDialog(
        "Clearing Cache",
        "POST",
        "/miso/rest/admin/cache/clear",
        null,
        function (success) {
          Utils.showOkDialog("Cache", [success ? "Cache cleared." : "Failed to clear cache."]);
        }
      );
    });
  },
  regenBarcodes: function () {
    Utils.showConfirmDialog("Barcodes", "Generate", ["Generate missing barcodes?"], function () {
      Utils.ajaxWithDialog(
        "Generating Barcodes",
        "POST",
        "/miso/rest/admin/barcode/regen",
        null,
        function (results) {
          Utils.showOkDialog(
            "Cache",
            results
              .filter(function (result) {
                return result.count > 0;
              })
              .map(function (result) {
                return (
                  "Regenerated " +
                  result.updated +
                  " barcodes of " +
                  result.blank +
                  " " +
                  result.target +
                  ". " +
                  result.total +
                  " " +
                  result.target +
                  " processed."
                );
              })
          );
        }
      );
    });
  },
  refreshConstants: function () {
    Utils.showConfirmDialog("Constants", "Refresh", ["Refresh constants?"], function () {
      Utils.ajaxWithDialog(
        "Refreshing Constants",
        "POST",
        "/miso/rest/admin/constants/refresh",
        null,
        function (success) {
          Utils.showOkDialog("Constants", ["Constants refreshed."]);
        }
      );
    });
  },
};
