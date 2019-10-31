# Building and Deploying

## Installing MISO

We recommend
[using Docker Compose to install and run MISO](../compose-installation-guide).

If you cannot use Docker, there is a
[baremetal installation guide](../baremetal-installation-guide) as well.

## Monitoring

The main MISO application and Run Scanner can be monitored using [Prometheus](http://prometheus.io/).
Available metrics can be obtained at at `http://<miso-URL>/metrics` (note: no `/miso` before `/metrics`).

## Next steps

After MISO is installed, refer to the [Admin Manual](../admin-guide) for tips on maintaining and running MISO.
