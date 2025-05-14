-- -----------------------------------------------------
--  According to https://planetscale.com/blog/what-are-the-disadvantages-of-database-indexes
--  To many indexes can lead to slow write. It seems that some SPS tables has duplicated id indexes (PrimaryKey is already an index)
--  This drops unnecessary index for the screenshot_data table since this is the most frequent and grows fast in SPS

-- NOTE: if this should cause latency on searches add it gain
-- -----------------------------------------------------

ALTER TABLE `screenshot_data` DROP INDEX `id_screenshot_data`;
ALTER TABLE `screenshot` DROP INDEX `id_screenshot`;