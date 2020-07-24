# Building and Deploying

## Installing MISO

We recommend
[using Docker Compose to install and run MISO](../compose-installation-guide).

If you cannot use Docker, there is a
[baremetal installation guide](../baremetal-installation-guide) as well.

## Monitoring

The main MISO application and Run Scanner can be monitored using [Prometheus](http://prometheus.io/).
Available metrics can be obtained at `http://<miso-URL>/metrics` (note: no `/miso` before `/metrics`).

## Next steps

After MISO is installed, you will want to configure it with data specific to your institute. The
following sections of the [User Manual](../../user_manual/) should be immediately helpful:

* [Type Data](../../user_manual/type_data/)
* [Instruments](../../user_manual/instruments/)
* [Freezers and Rooms](../../user_manual/freezers_and_rooms/)
* [Barcode Label Printers](../../user_manual/barcode_label_printers/)

See also [Related Software](../../user_manual/related_software) that you may wish to use with MISO.
