-- 기존 테이블 삭제 (역순으로)
DROP TABLE IF EXISTS gesture_actions;
DROP TABLE IF EXISTS active_log;
DROP TABLE IF EXISTS iot;
DROP TABLE IF EXISTS gesture;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS camera;
DROP TABLE IF EXISTS picture;
DROP TABLE IF EXISTS setting;
DROP TABLE IF EXISTS target;
DROP TABLE IF EXISTS user;

-- 테이블 생성
CREATE TABLE user (
                      user_id INTEGER NOT NULL,
                      user_nickname VARCHAR(255),
                      password VARCHAR(255),
                      email VARCHAR(255),
                      PRIMARY KEY (user_id)
);

CREATE TABLE target (
                        target_id INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        target_type VARCHAR(10) CHECK (target_type IN ('person', 'pet')),
                        PRIMARY KEY (target_id),
                        FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE setting (
                         setting_id INTEGER NOT NULL,
                         user_id INTEGER NOT NULL,
                         notification_enabled VARCHAR(10) CHECK (notification_enabled IN ('enabled', 'disabled')),
                         PRIMARY KEY (setting_id),
                         FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE iot (
                     device_id INTEGER NOT NULL,
                     user_id INTEGER NOT NULL,
                     ai_status VARCHAR(10) CHECK (ai_status IN ('active', 'inactive', 'learning')),
                     temperatue DOUBLE,
                     humidity DOUBLE,
                     device_status VARCHAR(10) CHECK (device_status IN ('online', 'offline')),
                     version INTEGER,
                     PRIMARY KEY (device_id),
                     FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE active_log (
                            log_id INTEGER NOT NULL,
                            camera_id INTEGER NOT NULL,
                            log_text VARCHAR(255),
                            thumbimage_path VARCHAR(255),
                            created_at VARCHAR(255),
                            PRIMARY KEY (log_id)
);

CREATE TABLE picture (
                         image_id INTEGER NOT NULL,
                         device_id INTEGER NOT NULL,
                         log_id INTEGER NOT NULL,
                         image_path VARCHAR(255),
                         capture_time TIMESTAMP,
                         stroage_order VARCHAR(255),
                         PRIMARY KEY (image_id),
                         FOREIGN KEY (device_id) REFERENCES iot(device_id),
                         FOREIGN KEY (log_id) REFERENCES active_log(log_id)
);

CREATE TABLE gesture_actions (
                                 action_id INTEGER NOT NULL,
                                 black_screen VARCHAR(10) CHECK (black_screen IN ('enabled', 'disabled')),
                                 capture VARCHAR(10) CHECK (capture IN ('enabled', 'disabled')),
                                 send_alert VARCHAR(10) CHECK (send_alert IN ('enabled', 'disabled')),
                                 notifications VARCHAR(10) CHECK (notifications IN ('enabled', 'disabled')),
                                 massage VARCHAR(255),
                                 PRIMARY KEY (action_id)
);

CREATE TABLE camera (
                        camera_id INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        device_id INTEGER NOT NULL,
                        target_id INTEGER NOT NULL,
                        camera__name VARCHAR(255),
                        is_enabled VARCHAR(3) CHECK (is_enabled IN ('yes', 'no')),
                        motion_detection_enabled BOOLEAN,
                        danger_zone VARCHAR(255),
                        PRIMARY KEY (camera_id),
                        FOREIGN KEY (user_id) REFERENCES user(user_id),
                        FOREIGN KEY (device_id) REFERENCES iot(device_id),
                        FOREIGN KEY (target_id) REFERENCES target(target_id)
);

CREATE TABLE notification (
                              notification_id INTEGER NOT NULL,
                              user_id INTEGER NOT NULL,
                              log_id INTEGER NOT NULL,
                              notification_type VARCHAR(10) CHECK (notification_type IN ('alert', 'info')),
                              title VARCHAR(255),
                              created_at VARCHAR(255),
                              PRIMARY KEY (notification_id),
                              FOREIGN KEY (user_id) REFERENCES user(user_id),
                              FOREIGN KEY (log_id) REFERENCES active_log(log_id)
);

CREATE TABLE gesture (
                         gesture_id INTEGER NOT NULL,
                         user_id INTEGER NOT NULL,
                         camera_id INTEGER NOT NULL,
                         gesture_name VARCHAR(255) NOT NULL,
                         gesture_type VARCHAR(50),
                         gesture_description CLOB,
                         geture_image_path VARCHAR(255),
                         is_enabled VARCHAR(3) CHECK (is_enabled IN ('yes', 'no')),
                         action_id INTEGER NOT NULL,
                         PRIMARY KEY (gesture_id),
                         FOREIGN KEY (user_id) REFERENCES user(user_id),
                         FOREIGN KEY (camera_id) REFERENCES camera(camera_id),
                         FOREIGN KEY (action_id) REFERENCES gesture_actions(action_id)
);

-- 외래 키 제약 조건 추가 (camera와 active_log 간)
ALTER TABLE active_log ADD FOREIGN KEY (camera_id) REFERENCES camera(camera_id);