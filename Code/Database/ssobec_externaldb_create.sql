-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2015-04-29 13:16:01.98




-- tables
-- Table lighting_energy_performance
CREATE TABLE lighting_energy_performance (
    date date    NOT NULL ,
    zone_description_region_id int    NOT NULL ,
    lighting_total_kw double    NOT NULL ,
    lighting_waste_kw double    NOT NULL ,
    CONSTRAINT lighting_energy_performance_pk PRIMARY KEY (date,zone_description_region_id)
);

-- Table plugload_energy_performance
CREATE TABLE plugload_energy_performance (
    date date    NOT NULL ,
    zone_description_region_id int    NOT NULL ,
    appliance_total_kw double    NOT NULL ,
    appliance_type varchar(255)    NOT NULL ,
    CONSTRAINT plugload_energy_performance_pk PRIMARY KEY (date,zone_description_region_id,appliance_type)
);

-- Table region_authority
CREATE TABLE region_authority (
    user_user_id int    NOT NULL ,
    zone_description_region_id int    NOT NULL ,
    CONSTRAINT region_authority_pk PRIMARY KEY (user_user_id,zone_description_region_id)
);

-- Table testing_ac_prediction
CREATE TABLE testing_ac_prediction (
    test_date date    NOT NULL ,
    outside_temperature double    NOT NULL ,
    ac_energy_usage double    NOT NULL ,
    CONSTRAINT testing_ac_prediction_pk PRIMARY KEY (test_date)
);

-- Table user
CREATE TABLE user (
    user_id int    NOT NULL  AUTO_INCREMENT,
    first_name varchar(50)    NOT NULL ,
    last_name varchar(50)    NOT NULL ,
    password varchar(50)    NOT NULL ,
    login_email varchar(100)    NOT NULL ,
    UNIQUE INDEX user_ak_id (user_id),
    CONSTRAINT user_pk PRIMARY KEY (user_id)
);

-- Table zone_description
CREATE TABLE zone_description (
    region_id int    NOT NULL  AUTO_INCREMENT,
    region_name varchar(255)    NOT NULL ,
    windows varchar(3)    NOT NULL ,
    CONSTRAINT zone_description_pk PRIMARY KEY (region_id)
);

-- Table zone_lighting
CREATE TABLE zone_lighting (
    zone_description_region_id int    NOT NULL ,
    time_stamp timestamp    NOT NULL ,
    status varchar(3)    NOT NULL ,
    energy_usage_kwh int    NOT NULL ,
    CONSTRAINT zone_lighting_pk PRIMARY KEY (zone_description_region_id,time_stamp)
);

-- Table zone_occupancy
CREATE TABLE zone_occupancy (
    zone_description_region_id int    NOT NULL ,
    time_stamp timestamp    NOT NULL ,
    occupancy int    NOT NULL ,
    UNIQUE INDEX zone_occupancy_ak_1 (zone_description_region_id,time_stamp),
    CONSTRAINT zone_occupancy_pk PRIMARY KEY (zone_description_region_id,time_stamp)
);

-- Table zone_plugload
CREATE TABLE zone_plugload (
    zone_description_region_id int    NOT NULL ,
    time_stamp timestamp    NOT NULL ,
    appliance_name varchar(255)    NOT NULL ,
    appliance_type varchar(255)    NOT NULL ,
    status varchar(9)    NOT NULL ,
    energy_usage_kwh int    NOT NULL ,
    CONSTRAINT zone_plugload_pk PRIMARY KEY (zone_description_region_id,time_stamp,appliance_name)
);

-- Table zone_temperature
CREATE TABLE zone_temperature (
    zone_description_region_id int    NOT NULL ,
    time_stamp timestamp    NOT NULL ,
    temperature int    NOT NULL ,
    CONSTRAINT zone_temperature_pk PRIMARY KEY (zone_description_region_id,time_stamp)
);





-- foreign keys
-- Reference:  plugload_energy_performance_zone_description (table: plugload_energy_performance)


ALTER TABLE plugload_energy_performance ADD CONSTRAINT plugload_energy_performance_zone_description FOREIGN KEY plugload_energy_performance_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  region_authority_user (table: region_authority)


ALTER TABLE region_authority ADD CONSTRAINT region_authority_user FOREIGN KEY region_authority_user (user_user_id)
    REFERENCES user (user_id);
-- Reference:  region_authority_zone_description (table: region_authority)


ALTER TABLE region_authority ADD CONSTRAINT region_authority_zone_description FOREIGN KEY region_authority_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  zone_energy_performance_zone_description (table: lighting_energy_performance)


ALTER TABLE lighting_energy_performance ADD CONSTRAINT zone_energy_performance_zone_description FOREIGN KEY zone_energy_performance_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  zone_lighting_zone_description (table: zone_lighting)


ALTER TABLE zone_lighting ADD CONSTRAINT zone_lighting_zone_description FOREIGN KEY zone_lighting_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  zone_occupancy_zone_description (table: zone_occupancy)


ALTER TABLE zone_occupancy ADD CONSTRAINT zone_occupancy_zone_description FOREIGN KEY zone_occupancy_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  zone_plugload_zone_description (table: zone_plugload)


ALTER TABLE zone_plugload ADD CONSTRAINT zone_plugload_zone_description FOREIGN KEY zone_plugload_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);
-- Reference:  zone_temperature_zone_description (table: zone_temperature)


ALTER TABLE zone_temperature ADD CONSTRAINT zone_temperature_zone_description FOREIGN KEY zone_temperature_zone_description (zone_description_region_id)
    REFERENCES zone_description (region_id);



-- End of file.

