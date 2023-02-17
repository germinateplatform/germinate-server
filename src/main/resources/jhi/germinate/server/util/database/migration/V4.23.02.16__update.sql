ALTER TABLE `news`
    ADD COLUMN `image_fit` enum('contain','cover') NOT NULL DEFAULT 'cover' COMMENT 'Determines the css property of the news item image.' AFTER `image`;