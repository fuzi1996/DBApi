PRAGMA foreign_keys = false;

DROP TABLE IF EXISTS "api_alarm";
CREATE TABLE "api_alarm" (
  "api_id" text NOT NULL,
  "alarm_plugin" text,
  "alarm_plugin_param" text
);

DROP TABLE IF EXISTS "api_auth";
CREATE TABLE "api_auth" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "app_id" varchar(64) DEFAULT NULL,
  "group_id" varchar(255) DEFAULT NULL
);

DROP TABLE IF EXISTS "api_config";
CREATE TABLE "api_config" (
  "id" TEXT NOT NULL,
  "path" TEXT,
  "name" TEXT,
  "note" TEXT,
  "params" TEXT,
  "status" integer,
  "datasource_id" text,
  "previlege" integer,
  "group_id" text,
  "cache_plugin" TEXT,
  "cache_plugin_params" TEXT,
  "create_time" text,
  "update_time" text,
  "content_type" TEXT,
  "open_trans" integer,
  "json_param" TEXT,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_api_config_path" UNIQUE ("path" ASC)
);

DROP TABLE IF EXISTS "api_group";
CREATE TABLE "api_group"
(
    "id"   text NOT NULL,
    "name" TEXT NOT NULL,
    PRIMARY KEY ("id"),
    CONSTRAINT "uk_api_group_name" UNIQUE ("name" ASC)
);

DROP TABLE IF EXISTS "api_sql";
CREATE TABLE "api_sql"
(
    "id"                      integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "api_id"                  text,
    "sql_text"                TEXT,
    "transform_plugin"        TEXT,
    "transform_plugin_params" TEXT
);

DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info`
(
    `id`          TEXT NOT NULL PRIMARY KEY,
    `name`        TEXT,
    `note`        TEXT,
    `secret`      TEXT,
    `expire_desc` TEXT,
    `expire_time` TEXT
);

DROP TABLE IF EXISTS "datasource";
CREATE TABLE "datasource"
(
    "id"          text NOT NULL,
    "name"        TEXT,
    "note"        TEXT,
    "type"        TEXT,
    "url"         TEXT,
    "username"    TEXT,
    "password"    TEXT,
    "driver"      text NOT NULL,
    "table_sql"   text,
    "create_time" text,
    "update_time" text,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "firewall";
CREATE TABLE "firewall" (
  "status" TEXT,
  "mode" TEXT
);

INSERT INTO "firewall" VALUES ('off', 'black');

DROP TABLE IF EXISTS "ip_rules";
CREATE TABLE "ip_rules" (
  "type" TEXT,
  "ip" TEXT
);

INSERT INTO "ip_rules" VALUES ('black', NULL);
INSERT INTO "ip_rules" VALUES ('white', NULL);

DROP TABLE IF EXISTS "sqlite_sequence";
CREATE TABLE "sqlite_sequence" (
  "name",
  "seq"
);

INSERT INTO "sqlite_sequence" VALUES ('user', 1);
INSERT INTO "sqlite_sequence" VALUES ('token', 0);
INSERT INTO "sqlite_sequence" VALUES ('api_sql', 0);
INSERT INTO "sqlite_sequence" VALUES ('api_auth', 0);

DROP TABLE IF EXISTS "token";
CREATE TABLE "token" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "token" TEXT,
  "note" TEXT,
  "expire" integer,
  "create_time" integer
);

DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "username" TEXT,
  "password" TEXT,
  UNIQUE ("username" ASC) ON CONFLICT ABORT
);

INSERT INTO "user" VALUES (1, 'admin', 'admin');

CREATE UNIQUE INDEX "api_config_id_uindex"
ON "api_config" (
  "id" ASC
);

CREATE UNIQUE INDEX "api_group_id_uindex"
ON "api_group" (
  "id" ASC
);

CREATE UNIQUE INDEX "datasource_id_uindex"
ON "datasource" (
  "id" ASC
);

UPDATE "sqlite_sequence" SET seq = 1 WHERE name = 'user';

PRAGMA foreign_keys = true;
