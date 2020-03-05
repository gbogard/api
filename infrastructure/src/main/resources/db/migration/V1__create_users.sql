create table if not exists users (
    id varchar(40) primary key,
    user_name varchar(40) unique,
    email_address varchar(255) unique,
    created_at timestamp,
    last_logged_in timestamp,
    password varchar(255)
);