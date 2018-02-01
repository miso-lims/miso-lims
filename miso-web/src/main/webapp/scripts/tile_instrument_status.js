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

TileTarget.instrument_status = {
  name: "Instrument Status",
  transform: function(data) {
    var isRunning = data.run && data.run.status === 'Running';
    return Tile.make([
        Tile.title(data.instrument.name, isRunning ? Tile.statusBusy() : Tile.statusOk()),
        Tile.lines(data.run ? [
            data.run.name + ' (' + data.run.alias + ')',
            (isRunning ? ("Busy since " + data.run.startDate) : ("Idle since " + data.run.endDate))
                + (data.run.progress ? (" " + data.run.progress) : "")] : ["Idle"], false)], function() {
      Utils.showWizardDialog(data.instrument.name, [{
        "name": "View Instrument (" + data.instrument.name + ")",
        "handler": function() {
          window.location = window.location.origin + '/miso/instrument/' + data.instrument.id;
        }
      }, data.run ? {
        "name": "View " + (data.run.status === 'Running' ? '' : "Last") + " Run (" + data.run.alias + ")",
        "handler": function() {
          window.location = window.location.origin + '/miso/run/' + data.run.id;
        }
      } : null].filter(function(x) {
        return !!x;
      }).concat(data.pools.map(function(pool) {
        return {
          "name": "View " + pool.name + " (" + pool.alias + ")",
          "handler": function() {
            window.location = window.location.origin + '/miso/pool/' + pool.id;
          }
        };
      })));

    });
  }
};
