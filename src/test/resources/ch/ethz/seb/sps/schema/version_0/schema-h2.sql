
-- -----------------------------------------------------
-- Table `oauth_access_token`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oauth_access_token` ;

CREATE TABLE IF NOT EXISTS `oauth_access_token` (
  `token_id` VARCHAR(255) NULL DEFAULT NULL,
  `token` BLOB NULL DEFAULT NULL,
  `authentication_id` VARCHAR(255) NULL DEFAULT NULL,
  `user_name` VARCHAR(255) NULL DEFAULT NULL,
  `client_id` VARCHAR(255) NULL DEFAULT NULL,
  `authentication` BLOB NULL DEFAULT NULL,
  `refresh_token` VARCHAR(255) NULL DEFAULT NULL,
  UNIQUE INDEX `authentication_id_UNIQUE` (`authentication_id` ASC));


-- -----------------------------------------------------
-- Table `oauth_refresh_token`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oauth_refresh_token` ;

CREATE TABLE IF NOT EXISTS `oauth_refresh_token` (
  `token_id` VARCHAR(255) NULL DEFAULT NULL,
  `token` BLOB NULL DEFAULT NULL,
  `authentication` BLOB NULL DEFAULT NULL);


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user` ;

CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `surname` VARCHAR(255) NULL DEFAULT NULL,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `language` VARCHAR(45) NOT NULL,
  `timeZone` VARCHAR(45) NOT NULL,
  `roles` VARCHAR(255) NOT NULL,
  `creation_time` BIGINT NOT NULL,
  `last_update_time` BIGINT NOT NULL,
  `termination_time` BIGINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_user` (`id` ASC));


-- -----------------------------------------------------
-- Table `screenshot_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot_data` ;

CREATE TABLE IF NOT EXISTS `screenshot_data` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `session_uuid` VARCHAR(45) NOT NULL,
  `timestamp` BIGINT NOT NULL,
  `image_format` INT(1) NULL,
  `meta_data` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  INDEX `screenshot_data_session_uuid_ix` (`session_uuid` ASC),
  UNIQUE INDEX `id_screenshot_data` (`id` ASC));


-- -----------------------------------------------------
-- Table `screenshot`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot` ;

CREATE TABLE IF NOT EXISTS `screenshot` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `image` MEDIUMBLOB NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_screenshot` (`id` ASC));



-- -----------------------------------------------------
-- Table `exam`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `exam` ;

CREATE TABLE IF NOT EXISTS `exam` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(45) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(4000) NULL,
  `url` VARCHAR(255) NULL,
  `type` VARCHAR(45) NULL,
  `owner` VARCHAR(255) NOT NULL,
  `creation_time` BIGINT NOT NULL,
  `last_update_time` BIGINT NOT NULL,
  `termination_time` BIGINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `exam_UNIQUE` (`id` ASC));



-- -----------------------------------------------------
-- Table `seb_group`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `seb_group` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(45) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(4000) NULL,
  `owner` VARCHAR(255) NULL,
  `creation_time` BIGINT NOT NULL,
  `last_update_time` BIGINT NOT NULL,
  `termination_time` BIGINT NULL,
  `exam_id` BIGINT UNSIGNED NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_group` (`id` ASC),
  INDEX `group_uuid_ix` (`uuid` ASC),
  CONSTRAINT `exam_ref`
    FOREIGN KEY (`exam_id`)
    REFERENCES `exam` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `session`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `session` ;

CREATE TABLE IF NOT EXISTS `session` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT UNSIGNED NOT NULL,
  `uuid` VARCHAR(45) NOT NULL,
  `image_format` INT(1) NOT NULL,
  `client_name` VARCHAR(255) NULL,
  `client_ip` VARCHAR(45) NULL,
  `client_machine_name` VARCHAR(255) NULL,
  `client_os_name` VARCHAR(255) NULL,
  `client_version` VARCHAR(255) NULL,
  `creation_time` BIGINT NOT NULL,
  `last_update_time` BIGINT NOT NULL,
  `termination_time` BIGINT NULL,
  PRIMARY KEY (`id`),
  INDEX `group_ref_idx` (`group_id` ASC),
  UNIQUE INDEX `id_session` (`id` ASC),
  INDEX `session_uuid_ix` (`uuid` ASC),
  CONSTRAINT `group_ref`
    FOREIGN KEY (`group_id`)
    REFERENCES `seb_group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `client_access`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `client_access` ;

CREATE TABLE IF NOT EXISTS `client_access` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(45) NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(4000) NULL,
  `client_name` VARCHAR(255) NOT NULL,
  `client_secret` VARCHAR(255) NOT NULL,
  `owner` VARCHAR(255) NULL,
  `creation_time` BIGINT NOT NULL,
  `last_update_time` BIGINT NOT NULL,
  `termination_time` BIGINT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));


-- -----------------------------------------------------
-- Table `entity_privilege`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `entity_privilege` ;

CREATE TABLE IF NOT EXISTS `entity_privilege` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `entity_type` VARCHAR(45) NOT NULL,
  `entity_id` BIGINT NOT NULL,
  `user_uuid` VARCHAR(255) NOT NULL,
  `privileges` VARCHAR(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_privilege` (`id` ASC));


-- -----------------------------------------------------
-- Table `additional_attribute`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `additional_attribute` ;

CREATE TABLE IF NOT EXISTS `additional_attribute` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `entity_type` VARCHAR(45) NOT NULL,
  `entity_id` BIGINT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `value` VARCHAR(255) NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `webservice_server_info`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `webservice_server_info` ;

CREATE TABLE IF NOT EXISTS `webservice_server_info` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(255) NOT NULL,
  `server_address` VARCHAR(45) NOT NULL,
  `master` INT(1) UNSIGNED NOT NULL,
  `creation_time` BIGINT NOT NULL,
  `last_update_time` BIGINT NOT NULL,
  `termination_time` BIGINT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `audit_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `audit_log` ;

CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_uuid` VARCHAR(255) NOT NULL,
  `timestamp` BIGINT NOT NULL,
  `activity_type` VARCHAR(45) NOT NULL,
  `entity_type` VARCHAR(45) NOT NULL,
  `entity_id` BIGINT NOT NULL,
  `message` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`));


