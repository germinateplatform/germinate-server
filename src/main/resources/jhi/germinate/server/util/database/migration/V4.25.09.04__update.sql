CREATE TABLE `taxonomyproviders`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `name` varchar(255) NOT NULL,
     `homepage_url` text NULL,
     `taxonomy_placeholder_url` text NULL,
     `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
);

CREATE TABLE `taxonomyproviderslinks`  (
     `taxonomy_id` int NOT NULL,
     `taxonomyprovider_id` int NOT NULL,
     `external_id` text NOT NULL,
     PRIMARY KEY (`taxonomy_id`, `taxonomyprovider_id`),
     FOREIGN KEY(`taxonomy_id`) REFERENCES `taxonomies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
     FOREIGN KEY (`taxonomyprovider_id`) REFERENCES `taxonomyproviders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO `taxonomyproviders` (`name`, `homepage_url`, `taxonomy_placeholder_url`) VALUES ('NCBI', 'https://www.ncbi.nlm.nih.gov/Taxonomy', 'https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id={ID}');
