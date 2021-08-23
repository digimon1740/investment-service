DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id         bigint NOT NULL AUTO_INCREMENT,
    username   varchar(50),
    password   varchar(300),
    created_at timestamp default NOW(),
    primary key (id)
);

DROP TABLE IF EXISTS user_balances;
CREATE TABLE user_balances
(
    id      bigint NOT NULL AUTO_INCREMENT,
    user_id bigint not null,
    balance int default 0,
    primary key (id)
);

DROP TABLE IF EXISTS user_investments;
CREATE TABLE user_investments
(
    id               bigint NOT NULL AUTO_INCREMENT,
    user_id          bigint not null,
    product_id       bigint not null,
    investing_amount int default 0,
    created_at       timestamp,
    primary key (id)
);

DROP TABLE IF EXISTS products;
CREATE TABLE products
(
    id                       bigint      NOT NULL AUTO_INCREMENT,
    title                    varchar(50) not null,
    type                     varchar(10) not null,
    total_investing_amount   bigint default 0,
    current_investing_amount bigint default 0,
    version                  bigint default 1,
    started_at               timestamp,
    finished_at              timestamp,
    created_at               timestamp,
    updated_at               timestamp,
    primary key (id)
);
