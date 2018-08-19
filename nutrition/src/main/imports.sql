CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE users (
  user_id            BIGINT       NOT NULL,
  username           VARCHAR(45)  NOT NULL,
  first_name         VARCHAR(45),
  last_name          VARCHAR(45),
  encoded_password   VARCHAR(255) NOT NULL,
  email              VARCHAR(255) NOT NULL,
  picture            VARCHAR(255),
  enabled            BOOLEAN      NOT NULL DEFAULT FALSE,
  email_confirmation BOOLEAN      NOT NULL DEFAULT FALSE,
  attempts           INTEGER      NOT NULL DEFAULT 0,
  expected_number_calories INTEGER NOT NULL DEFAULT 2000,
  PRIMARY KEY (user_id),
  UNIQUE (username),
  UNIQUE (email)
);

alter table users
  add column encoded_password varchar(255) not null default 'password';
alter table users
  add column expected_number_calories  INTEGER      NOT NULL DEFAULT 2000;

update users u
set encoded_password = u.password_encoded

alter table users
  drop column password_encoded

CREATE TABLE verification_tokens(
  token_id BIGINT      NOT NULL,
  user_id     BIGINT NOT null REFERENCES users,
  token         VARCHAR(250) NOT NULL,
  expiration timestamp NOT NULL,
  PRIMARY KEY (token_id),
  UNIQUE ( token, user_id)
);

CREATE TABLE user_roles (
  user_role_id BIGINT      NOT NULL,
  user_id     BIGINT NOT NULL REFERENCES users,
  role         VARCHAR(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE ( ROLE, user_id)
);

CREATE TABLE nutrition_entries (
  nutrition_entry_id BIGINT       NOT NULL,
  user_id            BIGINT       NOT NULL REFERENCES users,
  time               timestamp    NOT NULL,
  date               timestamp    NOT NULL,
  text               VARCHAR(255),
  meal_name          VARCHAR(255) NOT NULL,
  total_calories     BOOLEAN      NOT NULL DEFAULT FALSE,
  calories           INTEGER      NOT NULL
);
