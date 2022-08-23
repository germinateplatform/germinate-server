ALTER TABLE `images`
ADD COLUMN `exif` json NULL AFTER `path`;