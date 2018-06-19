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
 
var KitDescriptor = KitDescriptor || {
  validateKitDescriptor: function () {
    Validate.cleanFields('#kitdescriptor-form');
    jQuery('#kitdescriptor-form').parsley().destroy();
    
    // Name input field validation
    jQuery('#name').attr('class', 'form-control');
    jQuery('#name').attr('data-parsley-maxlength', '255');
    jQuery('#name').attr('data-parsley-required', 'true');
    
    // Version input field validation
    jQuery('#version').attr('class', 'form-control');
    jQuery('#version').attr('data-parsley-maxlength', '3');
    jQuery('#version').attr('data-parsley-type', 'number');
    
    // Manufacturer input field validation
    jQuery('#manufacturer').attr('class', 'form-control');
    jQuery('#manufacturer').attr('data-parsley-maxlength', '100');
    jQuery('#manufacturer').attr('data-parsley-required', 'true');
    
    // Part number input field validation
    jQuery('#partNumber').attr('class', 'form-control');
    jQuery('#partNumber').attr('data-parsley-maxlength', '50');
    jQuery('#partNumber').attr('data-parsley-required', 'true');
    
    // Stock level input field validation
    jQuery('#stockLevel').attr('class', 'form-control');
    jQuery('#stockLevel').attr('data-parsley-maxlength', '10');
    jQuery('#stockLevel').attr('data-parsley-type', 'number');
    jQuery('#stockLevel').attr('data-parsley-required', 'true');
    
    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');

    jQuery('#kitdescriptor-form').parsley();
    jQuery('#kitdescriptor-form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#kitdescriptor-form');
  }
};