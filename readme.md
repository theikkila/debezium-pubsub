

# Run MS SQL Server (Developer-version)
Important part is enable MSSQL-agent which maintains all CDC-processes.
```
docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=kissa@2Kossu' -e 'MSSQL_AGENT_ENABLED=true' -p 1433:1433 -d mcr.microsoft.com/mssql/server:2017-latest
```

## Create DB and tables

```sql
CREATE DATABASE vuoronumerodb;

USE vuoronumerodb;
CREATE SCHEMA [default];


CREATE TABLE [default].turn_number
(
    id         INT IDENTITY,
    office        VARCHAR(255) NOT NULL,
    issued_at DATETIME NOT NULL
                DEFAULT CURRENT_TIMESTAMP,
    done_at DATETIME,
    PRIMARY KEY(id)
);

```

## Enable CDC

Read https://debezium.io/documentation/reference/0.9/connectors/sqlserver.html#how-the-connector-works

```sql
GO
EXEC sys.sp_cdc_enable_db
GO


GO
EXEC sys.sp_cdc_enable_table
@source_schema = N'default',
@source_name   = N'turn_number',
@role_name     = NULL,
@filegroup_name = NULL,
@supports_net_changes = 1
GO
```

## Configure debezium app

Edit `src/main/scala/pubsubzium/PubSubZium.scala`

## Run it

```sh
sbt run
```


## Add new row into the tracked table:

```sql
USE vuoronumerodb;
INSERT INTO [default].turn_number (office) VALUES ('toolon-toimipiste');
```


# Tasks

1. Modify `handleEvent` to push JSON-objects into Cloud Pub/Sub
2. Make it configurable

### 3. Extra (don't implement yet)
Currently the app uses `org.apache.kafka.connect.storage.FileOffsetBackingStore` for offset storage.
This is file based storage so it's not stateless.

How big deal it is to change this to use some other cloud backed method such as database? 
