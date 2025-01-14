ALTER TABLE `images`
    ADD COLUMN `is_reference` tinyint(1) NOT NULL DEFAULT false AFTER `exif`;