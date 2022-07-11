create schema versus-app

create table person (
	id serial primary key,
	username varchar(30) unique not null,
	passwrd varchar(30) not null,
	first_name varchar(30) not null,
	last_name varchar(30) not null
)

insert into person values (default, 'asdf', 'asdf', 'asdf', 'asdf')