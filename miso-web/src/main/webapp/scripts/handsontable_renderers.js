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
/**
 * Created by saltc on 24/12/2015.
 */
function defaultTextRenderer(instance, td, row, col, prop, value, cellProperties) {
    if (value) {
        Handsontable.renderers.TextRenderer.apply(this, arguments);
    } else {
        Handsontable.Dom.empty(td);
        jQuery(td).css('color', '#888888');
        jQuery(td).css('font-style', 'italic');
        var disp = '';
        switch (col) {
            case 0:
                disp = "Enter a name..."
                break;
            case 1:
                disp = "Enter a description..."
                break;
        }
        jQuery(td).text(disp);
    }
}

function defaultDropdownRenderer(instance, td, row, col, prop, value, cellProperties) {
    // jQuery(td).addClass('htAutocompleteArrow');

    if (value) {
        Handsontable.renderers.AutocompleteRenderer.apply(this, arguments);
    } else {
        // Handsontable.Dom.empty(td);
        jQuery(td).css('color', '#888888');
        jQuery(td).css('font-style', 'italic');
        var disp = '';
        disp = "-Select-";
        value = disp;
        Handsontable.renderers.AutocompleteRenderer.apply(this, arguments);

    }
}

function defaultCheckboxRenderer(instance, td, row, col, prop, value, cellProperties) {
    if (!value) {
        value = false;
    }
    Handsontable.renderers.CheckboxRenderer.apply(this, arguments);
}
