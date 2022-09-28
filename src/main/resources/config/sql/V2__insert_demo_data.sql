INSERT IGNORE INTO `user` VALUES 
    (1, 'super-admin', '2019-01-01', 'super-admin', '', 'super-admin', '$2a$08$c2GKYEYoUVXH1Yb8GXVXVu66ltPvbZgLMcVSXRH.LgZNF/YeaYB8m', 'super-admin@nomail.nomail', 'en', 'UTC', 1, 'ADMIN')
;

INSERT IGNORE INTO `client_access` VALUES
    (1, 'test', '$2a$04$9JXeMn4nIHtHUSujpgTiqe11Y./qM/LdiMeaSDbTTWBI/zkHWAfEi', '2019-01-01', 1)
;

INSERT IGNORE INTO `seb_group` VALUES
    (1, 'test_uuid', 'test_group', 0, null)
;

INSERT IGNORE INTO `session` VALUES
    (1, 1, 'session_uuid', 'test_session', 0, null)
;
