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


ALTER TABLE `seb_group`
ADD COLUMN IF NOT EXISTS `exam_id` BIGINT UNSIGNED;

ALTER TABLE `seb_group`
ADD UNIQUE INDEX `exam_id_UNIQUE` (`exam_id` ASC),
ADD CONSTRAINT `exam_ref`
FOREIGN KEY (`exam_id`) REFERENCES `exam` (`id`);