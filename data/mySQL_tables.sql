DROP DATABASE IF EXISTS projectb;
CREATE DATABASE projectb;
USE projectb;

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(256) UNIQUE,
    email VARCHAR(256) UNIQUE,
    premium BOOLEAN,
    paid Date,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE userotp (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT,
    first VARCHAR(256),
    second VARCHAR(256),
    third VARCHAR(256),
    CONSTRAINT pk_userotp PRIMARY KEY (id),
    CONSTRAINT fk_user_id_otp FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE    
        ON UPDATE RESTRICT   
);

CREATE TABLE user_data (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT,
    password VARCHAR(256),
    created_at DATE,
    last_login DATE,
    login_streak INT,
    payment_id VARCHAR(512),
    CONSTRAINT pk_user_data PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE   
    ON UPDATE CASCADE 
);

CREATE TABLE achievements (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT,
    achievement VARCHAR(256),
    description VARCHAR(256),
    date_earned DATE,
    CONSTRAINT pk_achievements PRIMARY KEY (id),
    CONSTRAINT fk_user_id_achievements FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE   
	ON UPDATE CASCADE 
);