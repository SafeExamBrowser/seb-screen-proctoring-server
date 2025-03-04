-- -----------------------------------------------------
--     SEBSP-201 Add column index for timestamp in table screenshot_data
-- -----------------------------------------------------

CREATE INDEX `idx_screenshot_data_timestamp` ON `screenshot_data` (`timestamp` ASC);