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
  `creation_date` DATETIME NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `surname` VARCHAR(255) NULL DEFAULT NULL,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `language` VARCHAR(45) NOT NULL,
  `timeZone` VARCHAR(45) NOT NULL,
  `active` INT(1) NOT NULL,
  `roles` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `screenshot_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot_data` ;

CREATE TABLE IF NOT EXISTS `screenshot_data` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `session_uuid` VARCHAR(45) NOT NULL,
  `timestamp` BIGINT UNSIGNED NOT NULL,
  `image_url` VARCHAR(255) NULL,
  `image_format` VARCHAR(45) NULL,
  `meta_data` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  INDEX `session_uuid_ix` (`session_uuid` ASC));


-- -----------------------------------------------------
-- Table `screenshot`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot` ;

CREATE TABLE IF NOT EXISTS `screenshot` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `image` MEDIUMBLOB NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `group` ;

CREATE TABLE IF NOT EXISTS `group` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(45) NOT NULL,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `session`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `session` ;

CREATE TABLE IF NOT EXISTS `session` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT UNSIGNED NOT NULL,
  `uuid` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `group_ref_idx` (`group_id` ASC),
  CONSTRAINT `group_ref`
    FOREIGN KEY (`group_id`)
    REFERENCES `group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `client_access`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `client_access` ;

CREATE TABLE IF NOT EXISTS `client_access` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `client_name` VARCHAR(255) NOT NULL,
  `client_secret` VARCHAR(255) NOT NULL,
  `creation_date` DATETIME NOT NULL,
  `active` INT(1) NOT NULL,
  PRIMARY KEY (`id`));