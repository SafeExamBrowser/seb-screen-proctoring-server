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
  UNIQUE INDEX `authentication_id_UNIQUE` (`authentication_id` ASC))
;


-- -----------------------------------------------------
-- Table `oauth_refresh_token`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oauth_refresh_token` ;

CREATE TABLE IF NOT EXISTS `oauth_refresh_token` (
  `token_id` VARCHAR(255) NULL DEFAULT NULL,
  `token` BLOB NULL DEFAULT NULL,
  `authentication` BLOB NULL DEFAULT NULL)
;

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
  PRIMARY KEY (`id`))
;

-- -----------------------------------------------------
-- Table `group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `group` ;

CREATE TABLE IF NOT EXISTS `group` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(45) NOT NULL,
  `name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `screenshot_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot_data` ;

CREATE TABLE IF NOT EXISTS `screenshot_data` (
  `id` BIGINT UNSIGNED NOT NULL,
  `screenshot_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `session_id` VARCHAR(45) NOT NULL,
  `groupId` BIGINT UNSIGNED NOT NULL,
  `timestamp` BIGINT UNSIGNED NOT NULL,
  `image_url` VARCHAR(255) NULL,
  `image_format` VARCHAR(45) NULL,
  `meta_data` VARCHAR(8000) NULL,
  PRIMARY KEY (`id`),
  INDEX `group_ref_idx` (`groupId` ASC),
  CONSTRAINT `group_ref`
    FOREIGN KEY (`groupId`)
    REFERENCES `group` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `screenshot`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot` ;

CREATE TABLE IF NOT EXISTS `screenshot` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `image` MEDIUMBLOB NULL,
  PRIMARY KEY (`id`))
;