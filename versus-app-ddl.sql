create schema versus-app

create table person (
	id serial primary key,
	username varchar(30) unique not null,
	passwrd varchar(30) not null,
	first_name varchar(30) not null,
	last_name varchar(30) not null
)

create table artist (
	id serial primary key,
	stage_name varchar(30) not null
)

create table album (
	id serial primary key,
	title varchar(30) not null,
	artist_id integer references artist(id)
)

create table inventory (
	person_id integer references person(id),
	album_id integer references album(id),
	primary key(person_id, album_id)
)
