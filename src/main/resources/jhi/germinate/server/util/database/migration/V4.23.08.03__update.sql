INSERT INTO `imagetypes` (`description`, `reference_table`) VALUES ('Data story images', 'storysteps');

DROP TABLE IF EXISTS `stories`;
CREATE TABLE `stories` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `description` text NULL,
    `image_id` int(11) NULL,
    `requirements` json NULL,
    `publication_id` int(11) NULL,
    `featured` tinyint(1) NOT NULL DEFAULT 0,
    `visibility` tinyint(1) NOT NULL DEFAULT 1,
    `user_id` int(11) NOT NULL,
    `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`publication_id`) REFERENCES `publications` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (`image_id`) REFERENCES `images` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    PRIMARY KEY (`id`)
);

CREATE TABLE `storysteps`  (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `story_id` int(11) NOT NULL,
    `story_index` int(11) NOT NULL,
    `page_config` json NOT NULL,
    `name` varchar(255) NOT NULL,
    `description` text NULL,
    `image_id` int(11) NULL,
    `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`story_id`) REFERENCES `stories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`image_id`) REFERENCES `images` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);