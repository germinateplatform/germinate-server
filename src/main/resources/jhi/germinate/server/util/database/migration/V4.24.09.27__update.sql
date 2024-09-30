INSERT INTO `imagetypes` (`description`, `reference_table`)
VALUES ('Project image', 'projects');

ALTER TABLE `projects`
    ADD COLUMN `image_id` int(11) NULL AFTER `external_url`,
    ADD FOREIGN KEY (`image_id`) REFERENCES `images` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
