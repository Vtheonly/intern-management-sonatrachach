-- =====================================================================
-- SQL DDL (Data Definition Language) for Intern Management Application
-- Database: Oracle
-- Version: Merged
-- =====================================================================
-- This script is the result of merging the primary schema with a
-- secondary version. It preserves the structure of the primary tables
-- while integrating new tables and concepts from the secondary script.
-- Foreign keys and sample data have been adjusted for consistency.
-- =====================================================================


-- Drop tables if they exist to start fresh (in reverse order of creation)
BEGIN
    -- Drop new tables from secondary script
    EXECUTE IMMEDIATE 'DROP TABLE function_and_menu_profile';
    EXECUTE IMMEDIATE 'DROP TABLE function_and_menu';
    EXECUTE IMMEDIATE 'DROP TABLE theme_intern';
    EXECUTE IMMEDIATE 'DROP TABLE responsible_theme';
    EXECUTE IMMEDIATE 'DROP TABLE department_theme';
    EXECUTE IMMEDIATE 'DROP TABLE responsible';
    EXECUTE IMMEDIATE 'DROP TABLE profile';
    -- Drop original tables
    EXECUTE IMMEDIATE 'DROP TABLE intern';
    EXECUTE IMMEDIATE 'DROP TABLE theme';
    EXECUTE IMMEDIATE 'DROP TABLE worker_user';
    EXECUTE IMMEDIATE 'DROP TABLE department';
    EXECUTE IMMEDIATE 'DROP TABLE role';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- ===============================================================
-- PRIMARY TABLES (Structure from original script)
-- ===============================================================

-- 1. Table: role
-- Stores the different user roles in the system (e.g., Admin, Chief).
CREATE TABLE role (
                      role_id     NUMBER(10)          PRIMARY KEY,
                      role_name   VARCHAR2(50 CHAR)   NOT NULL UNIQUE
);

COMMENT ON TABLE role IS 'Stores user roles like Admin, Chief, User, etc. Retained from primary script.';


-- 2. Table: department
-- Stores information about company departments.
CREATE TABLE department (
                            department_id           NUMBER(10)          PRIMARY KEY,
                            department_name         VARCHAR2(100 CHAR)  NOT NULL UNIQUE,
                            department_description  VARCHAR2(255 CHAR),
                            location                VARCHAR2(100 CHAR),
                            fax                     VARCHAR2(20 CHAR)
);

COMMENT ON TABLE department IS 'Stores company department information. Structure retained from primary script.';


-- 3. Table: worker_user
-- Stores system user accounts. Renamed from "worker" to "worker_user" as per instruction.
-- Columns from secondary script like "profile_id" were ignored to maintain original structure.
CREATE TABLE worker_user (
                             user_id                 NUMBER(10)          PRIMARY KEY,
                             full_name               VARCHAR2(100 CHAR)  NOT NULL,
                             username                VARCHAR2(50 CHAR)   NOT NULL UNIQUE,
                             email_address           VARCHAR2(100 CHAR)  UNIQUE,
                             phone_number            VARCHAR2(20 CHAR),
                             fax_number              VARCHAR2(20 CHAR),
                             password_hash           VARCHAR2(255 CHAR)  NOT NULL,
                             salt                    VARCHAR2(255 CHAR),
                             password_creation_date  DATE                DEFAULT SYSDATE,
                             password_expiry_date    DATE,
                             role_id                 NUMBER(10)          NOT NULL,
                             department_id           NUMBER(10),
                             supervisor_id           NUMBER(10),
                             CONSTRAINT fk_worker_role FOREIGN KEY (role_id) REFERENCES role(role_id),
                             CONSTRAINT fk_worker_department FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE SET NULL,
                             CONSTRAINT fk_worker_supervisor FOREIGN KEY (supervisor_id) REFERENCES worker_user(user_id) ON DELETE SET NULL
);

COMMENT ON TABLE worker_user IS 'Stores accounts for all system users. Merged, keeping primary structure.';


-- 4. Table: theme
-- Stores internship themes or projects. Retains original column structure.
CREATE TABLE theme (
                       theme_id        NUMBER(10)          PRIMARY KEY,
                       theme_name      VARCHAR2(150 CHAR)  NOT NULL UNIQUE,
                       description     VARCHAR2(500 CHAR),
                       responsible     VARCHAR2(100 CHAR), -- NOTE: Original column retained as per rule.
                       department_id   NUMBER(10)          NOT NULL,
                       CONSTRAINT fk_theme_department FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE
);

COMMENT ON TABLE theme IS 'Stores details about internship projects/themes. Merged, keeping primary structure.';


-- 5. Table: intern
-- Stores information about the interns. New columns from secondary script were ignored.
CREATE TABLE intern (
                        intern_id       NUMBER(10)          PRIMARY KEY,
                        name            VARCHAR2(100 CHAR)  NOT NULL,
                        age             NUMBER(3),
                        email           VARCHAR2(100 CHAR)  UNIQUE,
                        university      VARCHAR2(150 CHAR),
                        phone_number    VARCHAR2(20 CHAR),
                        start_date      DATE,
                        is_accepted     VARCHAR2(10 CHAR)   DEFAULT 'Pending' NOT NULL,
                        theme_id        NUMBER(10),
                        CONSTRAINT fk_intern_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id) ON DELETE SET NULL,
                        CONSTRAINT chk_intern_status CHECK (is_accepted IN ('Accepted', 'Rejected', 'Pending'))
);

COMMENT ON TABLE intern IS 'Stores all data related to interns. Merged, keeping primary structure.';


-- ===============================================================
-- NEW TABLES (Added from secondary script, with adjustments)
-- ===============================================================

-- 6. Table: profile
-- New table from secondary script. Represents a concept similar to 'role' but used for menu access.
CREATE TABLE profile (
                         profile_id          VARCHAR2(255)       PRIMARY KEY,
                         profile_name        VARCHAR2(255 CHAR),
                         profile_description VARCHAR2(255 CHAR)
);
COMMENT ON TABLE profile IS 'New table from secondary script. Defines profiles for function/menu access.';


-- 7. Table: responsible
-- New table from secondary script to properly track responsible persons.
CREATE TABLE responsible (
                             id              VARCHAR2(255)       PRIMARY KEY,
                             name            VARCHAR2(255 CHAR),
                             department_id   NUMBER(10), -- Adjusted datatype to match department table
                             CONSTRAINT fk_responsible_dept FOREIGN KEY (department_id) REFERENCES department(department_id)
);
COMMENT ON TABLE responsible IS 'New table from secondary script to manage responsible parties for themes.';


-- 8. Table: function_and_menu
-- New table from secondary script.
CREATE TABLE function_and_menu (
                                   function_and_menu_id          VARCHAR2(255)       PRIMARY KEY,
                                   function_and_menu_name        VARCHAR2(255 CHAR),
                                   function_and_menu_description VARCHAR2(255 CHAR)
);
COMMENT ON TABLE function_and_menu IS 'New table from secondary script. Lists system functions and menus.';


-- 9. Table: function_and_menu_profile
-- New junction table from secondary script.
CREATE TABLE function_and_menu_profile (
                                           function_and_menu_id VARCHAR2(255),
                                           profile_id           VARCHAR2(255),
                                           description          VARCHAR2(255 CHAR),
                                           PRIMARY KEY (function_and_menu_id, profile_id),
                                           CONSTRAINT fk_famp_fam FOREIGN KEY (function_and_menu_id) REFERENCES function_and_menu(function_and_menu_id),
                                           CONSTRAINT fk_famp_profile FOREIGN KEY (profile_id) REFERENCES profile(profile_id)
);
COMMENT ON TABLE function_and_menu_profile IS 'New table from secondary script. Links profiles to functions.';


-- 10. Junction Tables (with adjusted FKs and structure)

CREATE TABLE department_theme (
                                  department_id NUMBER(10),
                                  theme_id      NUMBER(10),
                                  description   VARCHAR2(255 CHAR),
                                  PRIMARY KEY (department_id, theme_id),
                                  CONSTRAINT fk_dt_department FOREIGN KEY (department_id) REFERENCES department(department_id),
                                  CONSTRAINT fk_dt_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id)
);
COMMENT ON TABLE department_theme IS 'New junction table. Links departments to themes.';


CREATE TABLE responsible_theme (
                                   responsible_id VARCHAR2(255),
                                   theme_id       NUMBER(10),
                                   description    VARCHAR2(255 CHAR),
                                   PRIMARY KEY (responsible_id, theme_id),
                                   CONSTRAINT fk_rt_responsible FOREIGN KEY (responsible_id) REFERENCES responsible(id),
                                   CONSTRAINT fk_rt_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id)
);
COMMENT ON TABLE responsible_theme IS 'New junction table. Links responsible people to themes.';


CREATE TABLE theme_intern (
                              theme_id    NUMBER(10),
                              intern_id   NUMBER(10),
                              description VARCHAR2(255 CHAR),
                              PRIMARY KEY (theme_id, intern_id),
                              CONSTRAINT fk_ti_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id),
                              CONSTRAINT fk_ti_intern FOREIGN KEY (intern_id) REFERENCES intern(intern_id)
);
COMMENT ON TABLE theme_intern IS 'New junction table. Links themes to interns. Structure was adapted to match primary keys.';


-- ===============================================================
-- SAMPLE DATA INSERTION (Primary data preserved, new data added)
-- ===============================================================

-- Insert Roles (from primary script)
INSERT INTO role (role_id, role_name) VALUES (1, 'User');
INSERT INTO role (role_id, role_name) VALUES (4, 'Admin');
INSERT INTO role (role_id, role_name) VALUES (5, 'Chief');

-- Insert Departments (from primary script)
INSERT INTO department (department_id, department_name, location, fax) VALUES (10, 'Human Resources', 'Building A, Floor 1', '111-222-3333');
INSERT INTO department (department_id, department_name, location, fax) VALUES (20, 'Software Development', 'Building B, Floor 3', '444-555-6666');
INSERT INTO department (department_id, department_name, location, fax) VALUES (30, 'Marketing', 'Building A, Floor 2', '777-888-9999');

-- Insert Worker Users (from primary script)
INSERT INTO worker_user (user_id, full_name, username, email_address, password_hash, role_id, department_id, supervisor_id)
VALUES (101, 'John Smith', 'jsmith_chief', 'john.smith@example.com', 'rootroot', 5, 20, NULL);
INSERT INTO worker_user (user_id, full_name, username, email_address, password_hash, role_id, department_id, supervisor_id)
VALUES (102, 'Jane Doe', 'jdoe_admin', 'jane.doe@example.com', 'rootroot', 4, 10, NULL);
INSERT INTO worker_user (user_id, full_name, username, email_address, password_hash, role_id, department_id, supervisor_id)
VALUES (103, 'Peter Jones', 'pjones', 'peter.jones@example.com', 'rootroot', 1, 20, 101);

-- Insert Themes (from primary script)
INSERT INTO theme (theme_id, theme_name, description, responsible, department_id)
VALUES (1, 'Mobile App Development', 'Develop a new cross-platform mobile application.', 'John Smith', 20);
INSERT INTO theme (theme_id, theme_name, description, responsible, department_id)
VALUES (2, 'Social Media Campaign', 'Plan and execute a new marketing campaign.', 'Alice Williams', 30);
INSERT INTO theme (theme_id, theme_name, description, responsible, department_id)
VALUES (3, 'Recruitment Process Automation', 'Automate parts of the HR recruitment workflow.', 'Jane Doe', 10);

-- Insert Interns (from primary script)
INSERT INTO intern (intern_id, name, age, email, university, phone_number, start_date, is_accepted, theme_id)
VALUES (1001, 'Michael Brown', 21, 'michael.b@university.edu', 'State University', '123-456-7890', TO_DATE('2024-06-01', 'YYYY-MM-DD'), 'Pending', 1);
INSERT INTO intern (intern_id, name, age, email, university, phone_number, start_date, is_accepted, theme_id)
VALUES (1002, 'Emily White', 22, 'emily.w@university.edu', 'Tech Institute', '098-765-4321', TO_DATE('2024-06-01', 'YYYY-MM-DD'), 'Accepted', 2);
INSERT INTO intern (intern_id, name, age, email, university, phone_number, start_date, is_accepted, theme_id)
VALUES (1003, 'Chris Green', 20, 'chris.g@university.edu', 'City College', '555-123-4567', TO_DATE('2024-07-15', 'YYYY-MM-DD'), 'Rejected', 1);

-- Insert Profiles (from secondary script)
INSERT INTO profile (profile_id, profile_name, profile_description) VALUES ('P001', 'Administrator', 'System Administrator Role');
INSERT INTO profile (profile_id, profile_name, profile_description) VALUES ('P002', 'Manager', 'Department Manager Role');
INSERT INTO profile (profile_id, profile_name, profile_description) VALUES ('P003', 'Worker', 'General Worker Role');

-- Insert Responsibles (new consistent data)
INSERT INTO responsible (id, name, department_id) VALUES ('R001', 'John Smith', 20);
INSERT INTO responsible (id, name, department_id) VALUES ('R002', 'Alice Williams', 30);
INSERT INTO responsible (id, name, department_id) VALUES ('R003', 'Jane Doe', 10);

-- Insert Functions and Menus (from secondary script)
INSERT INTO function_and_menu (function_and_menu_id, function_and_menu_name, function_and_menu_description) VALUES ('M001', 'Main Menu', 'Main application menu');
INSERT INTO function_and_menu (function_and_menu_id, function_and_menu_name, function_and_menu_description) VALUES ('M002', 'Create Worker Menu', 'Menu for creating new workers');
INSERT INTO function_and_menu (function_and_menu_id, function_and_menu_name, function_and_menu_description) VALUES ('M003', 'Statistics Menu', 'Menu for viewing statistics');
INSERT INTO function_and_menu (function_and_menu_id, function_and_menu_name, function_and_menu_description) VALUES ('M004', 'Insert Interns Menu', 'Menu for inserting new interns');

-- Insert Function/Menu Profile Mappings (from secondary script)
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M001', 'P001', 'Administrator has access to the main menu.');
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M002', 'P001', 'Administrator can create worker profiles.');
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M003', 'P001', 'Administrator can view system statistics.');
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M004', 'P001', 'Administrator can insert interns.');
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M001', 'P002', 'Manager has access to the main menu.');
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M004', 'P002', 'Manager can insert interns.');
INSERT INTO function_and_menu_profile (function_and_menu_id, profile_id, description) VALUES ('M001', 'P003', 'Worker has access to the main menu.');

-- Insert into new Junction Tables (new consistent data)
INSERT INTO department_theme (department_id, theme_id, description) VALUES (20, 1, 'Software Development works on the Mobile App.');
INSERT INTO department_theme (department_id, theme_id, description) VALUES (30, 2, 'Marketing works on the Social Media Campaign.');
INSERT INTO department_theme (department_id, theme_id, description) VALUES (10, 3, 'Human Resources works on Recruitment Automation.');

INSERT INTO responsible_theme (responsible_id, theme_id, description) VALUES ('R001', 1, 'John Smith is responsible for the Mobile App.');
INSERT INTO responsible_theme (responsible_id, theme_id, description) VALUES ('R002', 2, 'Alice Williams is responsible for the Social Media Campaign.');

INSERT INTO theme_intern (theme_id, intern_id, description) VALUES (1, 1001, 'Michael Brown is assigned to the mobile app project.');
INSERT INTO theme_intern (theme_id, intern_id, description) VALUES (2, 1002, 'Emily White is assigned to the marketing campaign.');


COMMIT;

-- ===============================================================
-- End of Merged Script
-- ===============================================================