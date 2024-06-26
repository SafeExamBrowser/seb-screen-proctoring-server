ALTER TABLE `SEBScreenProctoring`.`additional_attribute`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `value` `value` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL;

ALTER TABLE `SEBScreenProctoring`.`audit_log`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `user_uuid` `user_uuid` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `activity_type` `activity_type` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `entity_type` `entity_type` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `message` `message` VARCHAR(4000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ;

ALTER TABLE `SEBScreenProctoring`.`client_access`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `uuid` `uuid` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `name` `name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `description` `description` VARCHAR(4000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `client_name` `client_name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `client_secret` `client_secret` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `owner` `owner` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ;

ALTER TABLE `SEBScreenProctoring`.`entity_privilege`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `entity_type` `entity_type` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `user_uuid` `user_uuid` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `privileges` `privileges` VARCHAR(16) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ;

ALTER TABLE `SEBScreenProctoring`.`exam`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `uuid` `uuid` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `name` `name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `description` `description` VARCHAR(4000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `url` `url` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `type` `type` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `owner` `owner` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ;

ALTER TABLE `SEBScreenProctoring`.`screenshot_data`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `session_uuid` `session_uuid` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `meta_data` `meta_data` VARCHAR(4000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ;

ALTER TABLE `SEBScreenProctoring`.`seb_group`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `uuid` `uuid` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `name` `name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `description` `description` VARCHAR(4000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `owner` `owner` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ;

ALTER TABLE `SEBScreenProctoring`.`session`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `uuid` `uuid` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `client_name` `client_name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `client_ip` `client_ip` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `client_machine_name` `client_machine_name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `client_os_name` `client_os_name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `client_version` `client_version` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ;

ALTER TABLE `SEBScreenProctoring`.`user`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `uuid` `uuid` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `name` `name` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `surname` `surname` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `username` `username` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `password` `password` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `email` `email` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `language` `language` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `timeZone` `timeZone` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `roles` `roles` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ;

ALTER TABLE `SEBScreenProctoring`.`webservice_server_info`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_general_ci,
CHANGE COLUMN `uuid` `uuid` VARCHAR(255) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ,
CHANGE COLUMN `server_address` `server_address` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL ;