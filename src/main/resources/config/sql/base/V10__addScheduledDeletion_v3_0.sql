-- -----------------------------------------------------
-- Table `scheduled_delete`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `scheduled_delete` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `state` VARCHAR(45) NOT NULL,
  `delete_due_time` BIGINT NOT NULL,
  `schedule_time` BIGINT NOT NULL,
  `start_time` BIGINT NULL,
  `end_time` BIGINT NULL,
  `owner` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `scheduled_delete_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `scheduled_delete_info` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `scheduled_delete_id` BIGINT UNSIGNED NOT NULL,
  `state` VARCHAR(45) NOT NULL,
  `exam_uuid` VARCHAR(45) NULL,
  `deletion_info` VARCHAR(4000) NULL,
  `error_info` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  INDEX `scheduled_delete_ref_idx` (`scheduled_delete_id` ASC),
  CONSTRAINT `scheduled_delete_ref`
    FOREIGN KEY (`scheduled_delete_id`)
    REFERENCES `scheduled_delete` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);