-- -----------------------------------------------------
-- Alter Table `exam` SEBSP-165
-- -----------------------------------------------------

ALTER TABLE `exam`
ADD COLUMN IF NOT EXISTS `supporter` VARCHAR(4000) NULL AFTER `owner`,
ADD COLUMN IF NOT EXISTS `deletion_time` BIGINT NULL
;

-- -----------------------------------------------------
-- Alter Table `session` SEBSP-172
-- -----------------------------------------------------

ALTER TABLE `session`
ADD COLUMN IF NOT EXISTS `encryption_key` VARCHAR(255) NULL
;
