INSERT IGNORE INTO `user` VALUES
    (1, 'super-admin', 'super-admin', 'surname', 'super-admin', '$2a$08$c2GKYEYoUVXH1Yb8GXVXVu66ltPvbZgLMcVSXRH.LgZNF/YeaYB8m', 'super-admin@nomail.nomail', 'en', 'UTC', 'ADMIN', 0, 0, null, 1)
;

INSERT IGNORE INTO `client_access` VALUES
    (1, 'test', 'test', 'test-description','test', '$2a$04$9JXeMn4nIHtHUSujpgTiqe11Y./qM/LdiMeaSDbTTWBI/zkHWAfEi', 'super-admin', 0, 0, null)
;

INSERT IGNORE INTO `exam` VALUES
    (1, 'a33d1f74-d5f2-47a3-8993-dc4d813bd4e4', 'test exam 01', '', '', '', 'super-admin', '06628f1f-8b2e-4bfa-8dd6-79842e7c0249,66c9c9d5-953a-44e2-a4d1-2348f7a7c489', 1709890124393, 1709890134393, NULL, 1709890124393, 1709890134393, 1),
    (2, 'a33d1f74-d5f2-47a3-8993-dc4d813bd4e5', 'test exam 02', '', '', '', 'super-admin', 'super-admin,66c9c9d5-953a-44e2-a4d1-2348f7a7c489', 1709890124394, 1709890134394, NULL, 1709890124394, 1709890134394, 1),
    (3, 'a33d1f74-d5f2-47a3-8993-dc4d813bd4e6', 'test exam 03', '', '', '', 'super-admin', '', 1709890124395, 1709890134395, 1709890134394, 1709890124395, 1709890134395, 1),
    (4, 'a33d1f74-d5f2-47a3-8993-dc4d813bd4e7', 'test exam 04', '', '', '', 'super-admin', '', 1709890124396, 1709890134396, 1709890134394, 1709890124396, 1709890134396, 1)
;

INSERT IGNORE INTO `seb_group` VALUES
    (1, '3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c', 'test_group', 'test-description', 'super-admin', 0, 0, null, 1),
    (2, '1cfb88c0-34a5-4ffd-a11c-6d9790b3f24c', 'exam_group', 'exam_group description', 'super-admin', 1721743475058, 1721743475060, null, 1)
;

INSERT IGNORE INTO `session` VALUES
    (1, 1, '9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 0, 'Scottie Degroot', '111.111.111.111', 'localhost', 'windows', '0.1-httpBot', 1721743476058, 1721743656171, null, NULL);

INSERT IGNORE INTO `session` VALUES
    (2, 1, '1cfb99c0-34a5-4ffd-a11c-4d9790b3f24c', 0, 'Scottie Degroot', '111.111.111.111', 'localhost', 'windows', '0.1-httpBot', 1721743476058, 1721743656171, null, NULL);

INSERT IGNORE INTO `session` VALUES
    (6, 2, 'cbb6ace2-4840-41c1-8b45-0752e1fb1ecd', 0, 'Arnulfo Durfee', '99.120.90.178', 'localhost', 'windows', '0.1-httpBot', 1721743467980, 1721743647997, 1721743647997, NULL);
