# Migrating to MySQL 8.0

MySQL 5.7 is nearing end of life, so we have moved MISO to MySQL 8.0. To migrate to the latest
version of MISO, you must switch to MySQL 8.0. Separate instructions are provided below for
migrating bare metal and Docker Compose setups.

It is recommended to upgrade MISO to the latest 1.x version first using the regular upgrade
procedure, then use these instructions to upgrade to 2.0.0, then use the regular instructions to
move to a later 2.x version if available.

## Bare Metal

1. Stop Tomcat to prevent MISO access during maintenance
1. Make a backup in your usual way, to use for roll-back if necessary
1. Make a backup of tables only, to use for the migration (views/triggers/functions/procedures will
  be re-created by Flyway). Replace `lims` with your database name if different
  
    ```
    DATABASE=lims

    mysql -u root -p --skip-column-names -b -e \
      "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '${DATABASE}' AND TABLE_TYPE = 'BASE TABLE';" \
      | xargs mysqldump -u root -p --single-transaction --skip-triggers "${DATABASE}" > misodb_5_7.sql
    ```

1. remove MySQL 5.7 and install MySQL 8
   * set the root password. Going forward, root must be used to run Flyway and restore backups
1. prepare MySQL 8 database. Replace `lims` with your database name if different.
   * `CREATE DATABASE lims;`
   * Create any necessary users. e.g.

     ```
     CREATE USER 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';
     GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';
     ```

1. restore tables-only backup to mysql 8.0 using root user

   ```
   mysql -D ${DATABASE} -uroot -p < /misodb_5_7.sql
   ```

1. Update the database URL in your `ROOT.xml`. Replace `localhost` and `lims` with the correct host
  and database name. Note the recommended connection options have changed

    ```
    url="jdbc:mysql://localhost:3306/lims?autoReconnect=true&amp;characterEncoding=UTF-8&amp;allowPublicKeyRetrieval=true&amp;sslMode=DISABLED"
    ```

1. Follow the usual [update procedure](https://miso-lims.readthedocs.io/projects/docs/en/latest/admin/baremetal-installation-guide/#installing-and-upgrading), but with one additional step:
  after copying the sqlstore jar, but before running Flyway migrate, run Flyway repair

    ```
    ./flyway -user=root -password=$MYSQL_ROOT_PASSWORD -url=$MISO_DB_URL -outOfOrder=true -locations=classpath:db/migration,classpath:uk.ac.bbsrc.tgac.miso.db.migration migrate -placeholders.filesDir=${MISO_FILES_DIR}
    ```
  
    Note the database URL should be updated (replacing `localhost` and `lims` with the correct host
    and database name):

    ```
    jdbc:mysql://localhost:3306/lims?autoReconnect=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&sslMode=DISABLED&useSSL=false
    ```

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

### Instructions

1. Make a backup in your usual way, to use for roll-back if necessary
1. Make a backup of tables only, to use for the migration (views/triggers/functions/procedures will
  be re-created by Flyway). Replace `lims` with your database name if different. Replace
  `miso-lims_db_1` with the correct container ID or name if different

    ```
    DATABASE=lims
    read -rs ROOT_PASSWORD
    # enter your root password
    docker exec -it miso-lims_db_1 /bin/bash -c "mysql -u root -p${ROOT_PASSWORD} --skip-column-names -b -e \
      \"SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '${DATABASE}' AND TABLE_TYPE = 'BASE TABLE';\" 2> /dev/null \
      | xargs mysqldump -u root -p${ROOT_PASSWORD} --single-transaction --skip-triggers ${DATABASE} 2> /dev/null" > misodb_5_7.sql
    ```

1. Update your docker-compose file to use the new MISO version and MySQL 8.0
    * add secret: `root_password`
    * **db** service:
      * change image to `mysql:8.0`
      * set environment variable
        * `MYSQL_ROOT_PASSWORD_FILE: /run/secrets/root_password`
        * If you previously had `MYSQL_RANDOM_ROOT_PASSWORD`, remove it
      * add secret: `root_password`
      * change the volume targeting `/var/lib/mysql` to a new source
    * **flyway** service:
      * update image to `ghcr.io/miso-lims/miso-lims-migration:2.0.0` (or update version in
        environment variable if applicable)
      * remove secret: `lims_password`
      * add secret: `root_password`
    * **webapp** service:
      * update image to `ghcr.io/miso-lims/miso-lims-webapp:2.0.0` (or update version in environment
        variable if applicable)

1. Spin up the MySQL container only, with new mounted volume

    ```
    docker-compose up db
    ```

1. Restore tables-only backup to the new container using root user. Replace `miso-lims_db_1` with
  the correct container ID or name if different

    ```
    docker exec -i miso-lims_db_1 sh -c "exec mysql -u root -p${ROOT_PASSWORD} ${DATABASE}" < misodb_5_7.sql
    ```

1. Use Flyway container to run `repair` command
    * edit docker-compose file - change flyway service `command` to `repair`
    * Run `docker-compose up flyway` and wait for it to complete
    * edit docker-compose file - change flyway service `command` back to `migrate`
1. Shut down MySQL container

    ```
    docker-compose down
    ```

1. Bring up full docker-compose

    ```
    docker-compose up
    ```

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
