SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `germinatebase`
    ADD COLUMN `display_name` varchar(255) NOT NULL COMMENT 'The name to be displayed on user interfaces and to be exported to external tools like Flapjack and Helium.' AFTER `name`,
    ADD INDEX `germinatebase_display_name`(`display_name`) USING BTREE;

UPDATE `germinatebase` SET `display_name` = `name`;

SET FOREIGN_KEY_CHECKS=1;