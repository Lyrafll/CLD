# Database migration

In this task you will migrate the Drupal database to the new RDS database instance.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 - Securing current Drupal data

### [Get Bitnami MariaDb user's password](https://docs.bitnami.com/aws/faq/get-started/find-credentials/)

```bash
[INPUT]
cat /home/bitnami/bitnami_credentials
[OUTPUT]
Welcome to the Bitnami package for Drupal

******************************************************************************
The default username and password is 'user' and 'Rgg7gCXM@.4A'.
******************************************************************************

You can also use this password to access the databases and any other component the stack includes.

Please refer to https://docs.bitnami.com/ for more details.
```

### Get Database Name of Drupal

```bash
[INPUT]
//add string connection

mariadb -u root -p'[PASSWORD]' -e "SHOW databases;"

[OUTPUT]

+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| test               |
+--------------------+
```

### [Dump Drupal DataBases](https://mariadb.com/kb/en/mariadb-dump/)

```bash
[INPUT]

mariadb-dump -u root -p'[PASSWORD]' --databases bitnami_drupal > drupal_db.sql

[OUTPUT]
none, but drupal_db.sql created
```

### Create the new Data base on RDS

```sql
[INPUT]
CREATE DATABASE bitnami_drupal;
```
note : Can't create database 'bitnami_drupal'; database exists

### [Import dump in RDS db-instance](https://mariadb.com/kb/en/restoring-data-from-dump-files/)

Note : you can do this from the Drupal Instance. Do not forget to set the "-h" parameter.

```sql
[INPUT]
mysql -h dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p'[PASSWORD]' < drupal_db.sql

[OUTPUT]
```

### [Get the current Drupal connection string parameters](https://www.drupal.org/docs/8/api/database-api/database-configuration)

```bash
[INPUT]
//help : same settings.php as before

cat /bitnami/drupal/sites/default/settings.php | tail -n14

[OUTPUT]
//at the end of the file you will find connection string parameters

$databases['default']['default'] = array (
  'database' => 'bitnami_drupal',
  'username' => 'bn_drupal',
  'password' => '[PASSWORD]',
  'prefix' => '',
  'host' => '127.0.0.1',
  'port' => '3306',
  'isolation_level' => 'READ COMMITTED',
  'driver' => 'mysql',
  'namespace' => 'Drupal\\mysql\\Driver\\Database\\mysql',
  'autoload' => 'core/modules/mysql/src/Driver/Database/mysql/',
);
```

### Replace the current host with the RDS FQDN

```
//settings.php

$databases['default']['default'] = array (
   [...] 
  'host' => 'dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com',
   [...] 
);
```

### [Create the Drupal Users on RDS Data base](https://mariadb.com/kb/en/create-user/)

Note : only calls from both private subnets must be approved.
* [By Password](https://mariadb.com/kb/en/create-user/#identified-by-password)
* [Account Name](https://mariadb.com/kb/en/create-user/#account-names)
* [Network Mask](https://cric.grenoble.cnrs.fr/Administrateurs/Outils/CalculMasque/)

```sql
[INPUT]
mysql -h dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p'[PASSWORD]' -e "GRANT ALL PRIVILEGES ON bitnami_drupal.* TO 'bn_drupal'@'10.0.11.0/255.255.255.240' IDENTIFIED BY '[PASSWORD]';"
```

```sql
//validation
[INPUT]
mysql -h dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p'[PASSWORD]' -e "SHOW GRANTS for 'bn_drupal'@'10.0.11.0/255.255.255.240';"
[OUTPUT]

+----------------------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.11.0/255.255.255.240                                                                                   |
+----------------------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO `bn_drupal`@`10.0.11.0/255.255.255.240` IDENTIFIED BY PASSWORD '[PASSWORD]' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO `bn_drupal`@`10.0.11.0/255.255.255.240`                                            |
+----------------------------------------------------------------------------------------------------------------------------------+
```

### Validate access (on the drupal instance)

```sql
[INPUT]
mysql -h dbi-devopsteam11.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p'[PASSWORD]' bitnami_drupal -e "SHOW DATABASES;"

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
2 rows in set (0.001 sec)
```

* Repeat the procedure to enable the instance on subnet 2 to also talk to your RDS instance.