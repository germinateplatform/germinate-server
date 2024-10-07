CREATE TABLE `datawarnings`  (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `description` text NULL,
     `category` enum('generic', 'quality', 'source', 'deprecated', 'missing', 'inaccuracy') NOT NULL DEFAULT 'generic',
     `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
);

CREATE TABLE `germplasmdatawarnings`  (
     `germinatebase_id` int(11) NOT NULL,
     `datawarning_id` int(11) NOT NULL,
     `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`germinatebase_id`, `datawarning_id`),
     FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (`datawarning_id`) REFERENCES `datawarnings` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
