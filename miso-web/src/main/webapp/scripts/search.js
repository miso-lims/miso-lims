/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

var Search = Search || {
  dashboardSearch: function(inp, throbber) {
    var t = jQuery(inp);
    var id = t.attr('id');
    jQuery('#' + id + 'result').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax('dashboard', id, {
      'str': t.val(),
      'url': ajaxurl
    }, {
      "doOnSuccess": function(json) {
        if (throbber) {
          jQuery('#' + id + 'result').html("");
        }

        if (!Utils.validation.isNullCheck(json.html)) {
          jQuery('#' + id + 'result').html(json.html);
        } else {
          jQuery('#' + id + 'result').html("No matches");
        }
      }
    });
    return true;
  },

  loadAll: function() {
    var self = this;
    self.dashboardSearch(jQuery('#searchProject'), true);
    self.dashboardSearch(jQuery('#searchRun'), true);
  },
};
