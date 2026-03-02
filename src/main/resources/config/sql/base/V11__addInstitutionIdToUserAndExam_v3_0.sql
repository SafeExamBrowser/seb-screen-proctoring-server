-- -----------------------------------------------------
-- Table `user` add column institution id
-- -----------------------------------------------------

ALTER TABLE `user`
ADD COLUMN IF NOT EXISTS `institution_id` BIGINT NULL
;

-- -----------------------------------------------------
-- Table `exam` rename column deletion_time to  institution_id
-- -----------------------------------------------------

ALTER TABLE `exam`
CHANGE `deletion_time` `institution_id` BIGINT NULL
;