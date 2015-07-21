# MISO Notification Server
Run miso-notification server as start-up service using systemd

### Setup

Edit service configuration file (_miso-notification.service_):
    1. `WorkingDirectory`, replace `<path-to-miso-lims>` with the path to **miso-lims** directory
    2. `Environment`, replace `<path-to-miso-lims>` with the path to **miso-lims** directory

Edit java properties file (_notification.properties_):
    1. Uncomment necessary `<service>.dataPaths` line and add  comma-separated paths to data folders to scan
    2. Replace "localhost:8080" with URI to MISO web server

### Install Service

    1. Copy **miso-notification.service** file to `/etc/systemd/system` folder (_requires super-user privileges_)
    2. Open Terminal and enter the following commands (_requires super-user privileges_):
```bash
sudo systemctl daemon-reload
sudo systemctl enable miso-notification.service
sudo systemctl start miso-notification.service
```

The service should start up. You can inspect **stdout** in `<path-to-miso-lims>/notification-server/service/log/notification/notification.log` file, and **stderr** by `sudo journalctl -f -u miso-notification`
