-- -----------------------------------------------------
-- Alter Table `session`
-- -----------------------------------------------------

ALTER TABLE `session`
ADD COLUMN IF NOT EXISTS `first_screenshot_time` BIGINT NULL
;