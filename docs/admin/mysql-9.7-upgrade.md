# Migrating to MySQL 9.7

MySQL 8.0 has reached end of life, so we have moved MISO to MySQL 9.7. To migrate to the latest
version of MISO, you must switch to MySQL 9.7. Separate instructions are provided below for
migrating bare metal and Docker Compose setups.

It is recommended to upgrade MISO to the latest 3.x version first using the regular upgrade
procedure, then use these instructions to upgrade to 4.0.0, then use the regular instructions to
move to a later 4.x version if available.

## Bare Metal

**Note**: The steps that use the MySQL root user may alternately be done using a different user with
similar privileges (`GRANT ALL PRIVILEGES ON *.* TO 'username'@'host';` works. Only a subset of
these is required, but we have not investigated specifics)

1. Stop Tomcat to prevent MISO access during maintenance
1. Make a backup in your usual way, to use for roll-back if necessary
1. Make a backup of tables only, to use for the migration (views/triggers/functions/procedures will
  be re-created by Flyway). Replace `lims` with your database name if different

        DATABASE=lims

        mysql -u root -p --skip-column-names -b -e \
          "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '${DATABASE}' AND TABLE_TYPE = 'BASE TABLE';" \
          | xargs mysqldump -u root -p --single-transaction --skip-triggers "${DATABASE}" > misodb_8_0.sql

1. remove MySQL 8.0 and install MySQL 9.7.
    * set the MySQL root password.
    * configure the MySQL server time zone (UTC is recommended). You may want to populate the MySQL
      time zone tables. See the [MySQL docs for this](https://dev.mysql.com/doc/refman/9.7/en/time-zone-support.html).
1. prepare MySQL 9.7 database. Replace `lims` with your database name if different.
    * `CREATE DATABASE lims;`
    * Create any necessary users. e.g.

            CREATE USER 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';
            GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';

1. restore tables-only backup to mysql 9.7 using MySQL root user.

        mysql -D ${DATABASE} -uroot -p < misodb_8_0.sql

1. Follow the usual [update procedure](https://miso-lims.readthedocs.io/en/latest/admin/baremetal-installation-guide/#installing-and-upgrading)
until you get to migrating the database. Note that the steps here have been updated and we now
recommend running Flyway via our Docker image. Follow these instructions, but before running Flyway
migrate, run a Flyway repair:

        docker run --rm --env-file "${ENV_FILE}" -v "${PASSWORD_FILE}:/run/secrets/flyway_password:ro" miso-lims-flyway:${MISO_VERSION} repair

    If you receive an error regarding the time zone, ensure that you have configured the MySQL time
    zone as instructed above.

1. Run Flyway migrate and continue with the usual update procedure.

## Docker Compose

### Notes for all below instructions:

* You should add your usual parameters to all `docker-compose` commands (--env-file, -f, etc.)
* Docker services named `db`, `flyway`, and `webapp` are assumed. Substitute your own names if
  different
* You can use `-d` on `docker-compose up` commands to run in the background and use a single
  terminal for all of this, but it's often useful to keep an eye on the containers' output, so using
  a second terminal is ideal
* You will need to know your MySQL root password. See below for instructions to reset it if
  necessary
* The steps that use the MySQL root user may alternately be done using a different user with similar
  privileges (`GRANT ALL PRIVILEGES ON *.* TO 'username'@'host';` works. Only a subset of these is
  required, but we have not investigated specifics)

### Instructions

1. Make a backup in your usual way, to use for roll-back if necessary
1. Make a backup of tables only, to use for the migration (views/triggers/functions/procedures will
  be re-created by Flyway). Replace `lims` with your database name if different. Replace
  `miso-lims_db_1` with the correct container ID or name if different

        DATABASE=lims
        read -rs ROOT_PASSWORD
        # enter your root password
        docker exec -it miso-lims_db_1 /bin/bash -c "mysql -u root -p${ROOT_PASSWORD} --skip-column-names -b -e \
          \"SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '${DATABASE}' AND TABLE_TYPE = 'BASE TABLE';\" 2> /dev/null \
          | xargs mysqldump -u root -p${ROOT_PASSWORD} --single-transaction --skip-triggers ${DATABASE} 2> /dev/null" > misodb_8_0.sql

1. Shut down the docker-compose environment

        docker-compose down

1. Update your docker-compose file to use the new MISO version and MySQL 9.7
    * **db** service:
        * change image to `mysql:9.7`
        * change the volume targeting `/var/lib/mysql` to a new source
    * **flyway** service:
        * update image to `ghcr.io/miso-lims/miso-lims-migration:4.0.0` (or update version in
          environment variable if applicable)
        * add environment variable `FLYWAY_USER=root` (or other user)
        * add environment variable `FLYWAY_PASSWORD_FILE=/run/secrets/root_password`
    * **webapp** service:
        * update image to `ghcr.io/miso-lims/miso-lims-webapp:4.0.0` (or update version in
        environment variable if applicable)

1. Spin up the MySQL container only, with new mounted volume

        docker-compose up db

1. Restore tables-only backup to the new container using root user. Replace `miso-lims_db_1` with
  the correct container ID or name if different

        docker exec -i miso-lims_db_1 sh -c "exec mysql -u root -p${ROOT_PASSWORD} ${DATABASE}" < misodb_8_0.sql

1. Use Flyway container to run `repair` command
    * edit docker-compose file - change flyway service `command` to `repair`
    * Run `docker-compose up flyway` and wait for it to complete
    * edit docker-compose file - change flyway service `command` back to `migrate`
1. Shut down MySQL container

        docker-compose down

1. Bring up full docker-compose

        docker-compose up

1. Watch the logs to ensure the Flyway migration completes successfully. It will likely take a few minutes, and you should see a message like "miso-lims_flyway_1 exited with code 0" when it's done

### Docker Compose - Reset Root Password

Only do this if you don't know your root password.

```
# stop all docker containers
docker-compose down
# run bash on db container (doesn't start mysqld):
docker-compose run db bash

# create password reset script. Replace CHANGE_ME with your desired password
echo "USE mysql;
UPDATE user SET authentication_string=PASSWORD('CHANGE_ME') WHERE User='root';
FLUSH PRIVILEGES;" > /tmp/reset-root.sql

mysqld_safe --init-file=/tmp/reset-root.sql &
# wait a few seconds, then try logging in to make sure it worked
mysql -u root -p
# enter password you entered in place of CHANGE_ME above
exit
exit
docker-compose down
```
