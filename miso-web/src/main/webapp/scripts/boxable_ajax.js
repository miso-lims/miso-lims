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

var Boxable = Boxable || {
  /**
   * Enable or disable distribution date & recipient, depending on if distributed is checked
   */
  distributionChanged: function() {
    var isDistributed = document.getElementById('distributed').checked;
    var distributionDate = document.getElementById('distributionDatePicker');
    var recipient = document.getElementById('distributionRecipient');
    var location = document.getElementById('locationBarcode');
    function makeRequiredStar() {
      var required = document.createElement('SPAN');
      required.classList.add('requiredForDistribution');
      required.innerHTML = " *";
      return required;
    }
    if (isDistributed) {
      distributionDate.removeAttribute('readonly');
      distributionDate.classList.remove('disabled');
      Utils.ui.addDatePicker('distributionDatePicker');
      recipient.removeAttribute('readonly');
      recipient.classList.remove('disabled');
      distributionDate.labels[0].appendChild(makeRequiredStar());
      recipient.labels[0].appendChild(makeRequiredStar());
      if (document.getElementById('boxLocation') != null) {
        document.getElementById('boxLocation').innerHTML = "";
      }
    } else {
      distributionDate.setAttribute('value', "");
      distributionDate.setAttribute('readonly', 'readonly');
      distributionDate.classList.add('disabled');
      jQuery('#distributionDatePicker').datepicker('destroy');
      jQuery('#distributionDatePicker').removeClass('hasDatepicker');
      recipient.setAttribute('value', "");
      recipient.setAttribute('readonly', 'readonly');
      recipient.classList.add('disabled');
      jQuery('.requiredForDistribution').remove();
      if (location && location.value.indexOf('SENT TO:') == 0) {
        location.setAttribute('value', "");
      }
    }
  },
};