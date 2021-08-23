insert into users (id, username, password)
values (1, 'digimon', 'digimon');
insert into users (id, username, password)
values (2, 'digimon2', 'digimon2');


insert into user_balances(user_id, balance)
values (1, 1000000);
insert into user_balances(user_id, balance)
values (2, 1500000);


insert into products(id, title, type, total_investing_amount, started_at, finished_at, created_at, updated_at)
values (1, '북한부동산', 'PROPERTY', 100000, '2021-05-01 00:00:00', '2021-06-01 00:00:00', '2021-03-01 00:00:00', '2021-03-01 00:00:00');

insert into products(id, title, type, total_investing_amount, started_at, finished_at, created_at, updated_at)
values (2, '미국부동산', 'PROPERTY', 200000, '2021-06-01 00:00:00', '2021-07-01 00:00:00', '2021-03-01 00:00:00', '2021-03-01 00:00:00');

insert into products(id, title, type, total_investing_amount, started_at, finished_at, created_at, updated_at)
values (3, '한국인덱스펀드', 'CREDIT', 500000 , '2021-05-01 00:00:00', '2021-10-01 00:00:00', '2021-04-01 00:00:00', '2021-04-01 00:00:00');

insert into products(id, title, type, total_investing_amount, started_at, finished_at, created_at, updated_at)
values (4, '서울부동산', 'PROPERTY', 300000, '2021-08-01 00:00:00', '2021-11-01 00:00:00', '2021-07-01 00:00:00', '2021-07-01 00:00:00');