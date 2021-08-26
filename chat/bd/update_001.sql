create table users (
                        id serial primary key not null,
                        login varchar(2000),
                        password varchar(2000),
                        role_id int
);

insert into users (login, password, role_id) values ('azat', '123', 1);