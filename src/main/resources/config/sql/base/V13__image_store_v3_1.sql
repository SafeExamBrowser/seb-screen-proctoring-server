
-- -----------------------------------------------------
-- Alter Table `session` SEBSERV-1024
-- -----------------------------------------------------

ALTER TABLE `session`
ADD COLUMN IF NOT EXISTS `store_encryption_key` VARCHAR(255) NULL
;