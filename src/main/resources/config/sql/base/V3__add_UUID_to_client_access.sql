-- -----------------------------------------------------
-- Alter Table `batch_action`
-- -----------------------------------------------------

ALTER TABLE `client_access`
ADD COLUMN IF NOT EXISTS `uuid` VARCHAR(45) NULL AFTER `id`
;