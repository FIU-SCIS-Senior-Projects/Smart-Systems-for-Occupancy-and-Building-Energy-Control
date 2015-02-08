-- Created by Vertabelo (http://vertabelo.com)
-- Script type: create
-- Scope: [tables, references, sequences, views, procedures]
-- Generated at Sat Feb 07 03:29:00 UTC 2015




-- tables
-- Table current_zone_data
CREATE TABLE current_zone_data (
    zone_description_region_id int    NOT NULL ,
    occupancy int    NULL ,
    temperature int    NULL ,
    plug_load int    NULL ,
    lighting int    NULL ,
    time_stamp timestamp    NOT NULL  ON UPDATE CURRENT_TIMESTAMP ,
    CONSTRAINT current_zone_data_pk PRIMARY KEY (zone_description_region_id)
);

-- Table region_authority
CREATE TABLE region_authority (
    user_user_id int    NOT NULL ,
    zone_description_region_id int    NOT NULL ,
    CONSTRAINT region_authority_pk PRIMARY KEY (user_user_id,zone_description_region_id)
);

-- Table user
CREATE TABLE user (
    user_id int    NOT NULL  AUTO_INCREMENT,
    name varchar(50)    NOT NULL ,
    password varchar(50)    NOT NULL ,
    login_email varchar(100)    NOT NULL ,
    UNIQUE INDEX user_ak_id (user_id),
    CONSTRAINT user_pk PRIMARY KEY (user_id)
);

-- Table zone_description
CREATE TABLE zone_description (
    region_id int    NOT NULL  AUTO_INCREMENT,
    region_name varchar(255)    NOT NULL ,
    CONSTRAINT zone_description_pk PRIMARY KEY (region_id)
);





-- foreign keys
-- Reference:  region_authority_user (table: region_authority)


ALTER TABLE region_authority ADD CONSTRAINT region_authority_user FOREIGN KEY region_authority_user (user_user_id)
    REFERENCES user (user_id);
-- Reference:  region_authority_zone_description (table: region_authority)


ALTER TABLE region_authority ADD CONSTRAINT region_authority_zone_description FOREIGN KEY region_authority_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  zone_data_zone_description (table: current_zone_data)


ALTER TABLE current_zone_data ADD CONSTRAINT zone_data_zone_description FOREIGN KEY zone_data_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);



-- End of file.

