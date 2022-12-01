CREATE TABLE `userfeedback`
(
    `id`            int(11)                                                              NOT NULL AUTO_INCREMENT COMMENT 'Auto-incremented primary key.',
    `content`       text                                                                 NOT NULL COMMENT 'Feedback content.',
    `image`         mediumblob                                                           NULL COMMENT 'Optional interface screenshot.',
    `page_url`      text                                                                 NOT NULL COMMENT 'The URL of the page the user was looking at.',
    `user_id`       int(11)                                                              NULL COMMENT 'Optional user id if user was logged in.',
    `contact_email` varchar(255)                                                         NOT NULL COMMENT 'Contact email address.',
    `feedback_type` enum ('question', 'data_error', 'general', 'bug', 'feature_request') NOT NULL DEFAULT 'general' COMMENT 'The type of feedback.',
    `severity`      enum ('low','medium','high')                                         NOT NULL DEFAULT 'medium' COMMENT 'The estimated severity of the issue.',
    `is_new`        tinyint(1)                                                           NOT NULL DEFAULT 1 COMMENT 'Indicates whether this is new feedback or has been seen before.',
    `created_on`    datetime                                                             NULL     DEFAULT CURRENT_TIMESTAMP COMMENT 'When this database record has been created.',
    `updated_on`    timestamp                                                            NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When this database record has last been updated.',
    PRIMARY KEY (`id`),
    INDEX (`is_new`) USING BTREE
);