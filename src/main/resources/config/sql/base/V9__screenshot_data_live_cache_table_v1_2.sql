-- -----------------------------------------------------
-- Table `screenshot_data_live_cache` SEBSP-210
-- -----------------------------------------------------
DROP TABLE IF EXISTS `screenshot_data_live_cache` ;

CREATE TABLE IF NOT EXISTS `screenshot_data_live_cache` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `session_uuid` VARCHAR(45) NOT NULL,
  `id_latest_ssd` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_session_uuid` (`session_uuid` ASC));