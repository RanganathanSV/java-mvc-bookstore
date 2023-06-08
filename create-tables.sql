create database bookstore;

create table bookstore.user
(
id int(50) primary key auto_increment, 
first_name varchar(220),  
last_name varchar(220), 
phone varchar(120), 
email varchar(120), 
password varchar(220),
date_created timestamp default now()
);

create table bookstore.admin
(
	id int(50) primary key auto_increment, 
	user_id int(45),  
	is_admin boolean default(false),
	FOREIGN KEY (user_id) REFERENCES bookstore.user(id)
);

create table bookstore.book
(
	id int(50) primary key auto_increment, 
	title varchar(220), 
	author varchar(120), 
	genre varchar(120), 
	description text, 
	date_created timestamp default now(), 
	copy int(10), 
	price decimal(2)
);

create table bookstore.employee
(
	id int(50) primary key auto_increment, 
	name varchar(120), 
	salary decimal(50), 
	department varchar(120), 
	reg_date timestamp default now()
);

create table bookstore.feedback 
(
	id int(50) primary key auto_increment, 
	name varchar(120), 
	phone varchar(120), 
	email varchar(120), 
	feedback text, 
	date_created timestamp default now()
);

create table bookstore.purchase_detail
(
	id int(50) primary key auto_increment,  
	name varchar(120), 
	phone varchar(120), 
	books text, 
	quantity int(50), 
	total_price decimal(50), 
	date_purchased timestamp default now()
);