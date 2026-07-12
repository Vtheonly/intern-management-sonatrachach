
CREATE TABLE department (
                            department_id VARCHAR2(255) PRIMARY KEY,
                            location VARCHAR2(255),
                            department_name VARCHAR2(255),
                            department_description VARCHAR2(255),
                            fax VARCHAR2(255)
);

-- Create the PROFILE table BEFORE tables that reference it
CREATE TABLE profile (
                         profile_id VARCHAR2(255) PRIMARY KEY,
                         profile_name VARCHAR2(255),
                         profile_description VARCHAR2(255)
);

CREATE TABLE responsible (
                             id VARCHAR2(255) PRIMARY KEY,
                             name VARCHAR2(255),
                             department_id VARCHAR2(255),
                             FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE theme (
                       theme_id VARCHAR2(255) PRIMARY KEY,
                       description VARCHAR2(255),
                       theme_name VARCHAR2(255),
                       department_id VARCHAR2(255),
                       FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- Now create WORKER, referencing the existing PROFILE table
CREATE TABLE worker (
                        id VARCHAR2(255) PRIMARY KEY,
                        full_name VARCHAR2(255),
                        department_id VARCHAR2(255),
                        username VARCHAR2(255),
                        email_address VARCHAR2(255),
                        phone_number VARCHAR2(255),
                        fax_number VARCHAR2(255),
                        supervisor_id VARCHAR2(255),
                        password_hash VARCHAR2(255),
                        salt VARCHAR2(255),
                        password_creation_date DATE,
                        password_expiry_date DATE,
                        profile_id VARCHAR2(255),
                        FOREIGN KEY (department_id) REFERENCES department(department_id),
                        FOREIGN KEY (supervisor_id) REFERENCES worker(id),
                        FOREIGN KEY (profile_id) REFERENCES profile(profile_id) -- This now references an existing table definition
);

CREATE TABLE intern (
                        group_id VARCHAR2(255),
                        id VARCHAR2(255),
                        name VARCHAR2(255),
                        age NUMBER, -- Changed INT to NUMBER
                        email VARCHAR2(255),
                        responsible_id VARCHAR2(255),
                        theme_id VARCHAR2(255),
                        department_id VARCHAR2(255),
                        university VARCHAR2(255),
                        phone_number VARCHAR2(255),
                        start_date DATE,
                        end_date DATE,
                        inserted_by VARCHAR2(255),
                        PRIMARY KEY (group_id, id),
                        FOREIGN KEY (responsible_id) REFERENCES responsible(id),
                        FOREIGN KEY (theme_id) REFERENCES theme(theme_id),
                        FOREIGN KEY (department_id) REFERENCES department(department_id),
                        FOREIGN KEY (inserted_by) REFERENCES worker(id)
);

CREATE TABLE department_theme (
                                  department_id VARCHAR2(255),
                                  theme_id VARCHAR2(255),
                                  description VARCHAR2(255),
                                  PRIMARY KEY (department_id, theme_id),
                                  FOREIGN KEY (department_id) REFERENCES department(department_id),
                                  FOREIGN KEY (theme_id) REFERENCES theme(theme_id)
);

CREATE TABLE responsible_theme (
                                   responsible_id VARCHAR2(255),
                                   theme_id VARCHAR2(255),
                                   description VARCHAR2(255),
                                   PRIMARY KEY (responsible_id, theme_id),
                                   FOREIGN KEY (responsible_id) REFERENCES responsible(id),
                                   FOREIGN KEY (theme_id) REFERENCES theme(theme_id)
);

CREATE TABLE theme_intern (
                              theme_id VARCHAR2(255),
                              intern_group_id VARCHAR2(255),
                              intern_id VARCHAR2(255),
                              description VARCHAR2(255),
                              PRIMARY KEY (theme_id, intern_group_id, intern_id),
                              FOREIGN KEY (theme_id) REFERENCES theme(theme_id),
                              FOREIGN KEY (intern_group_id, intern_id) REFERENCES intern(group_id, id)
);

CREATE TABLE function_and_menu (
                                   function_and_menu_id VARCHAR2(255) PRIMARY KEY,
                                   function_and_menu_name VARCHAR2(255),
                                   function_and_menu_description VARCHAR2(255)
);

-- Now create function_and_menu_profile, referencing the existing PROFILE table
CREATE TABLE function_and_menu_profile (
                                           function_and_menu_id VARCHAR2(255),
                                           profile_id VARCHAR2(255),
                                           description VARCHAR2(255),
                                           PRIMARY KEY (function_and_menu_id, profile_id),
                                           FOREIGN KEY (function_and_menu_id) REFERENCES function_and_menu(function_and_menu_id),
                                           FOREIGN KEY (profile_id) REFERENCES profile(profile_id) -- This now references an existing table definition
);