create table if not exists container (
    code varchar(32) primary key,
    type varchar(32) not null
);
create table if not exists ship (
    name varchar(32) primary key,
    company varchar(32) not null
);
create table if not exists item (
    name varchar(32) primary key,
    type varchar(32) not null,
    price int not null,
    container_code varchar(32),
    ship_name varchar(32),
    log_time timestamp not null
);
create table if not exists import_information (
  item varchar(32) primary key,
  city varchar(32) not null,
  time date,
  tax numeric(20, 7) not null
);
create table if not exists export_information (
  item varchar(32) primary key,
  city varchar(32) not null,
  time date,
  tax numeric(20, 7) not null
);
create table if not exists courier (
  phone_number varchar(32) primary key,
  name varchar(32) not null,
  gender char(1) not null,
  birth_year int not null,
  company varchar(32) not null
);
create table if not exists delivery_information (
    item varchar(32) primary key,
    city varchar(32) not null,
    finish_time date,
    courier_phone_number varchar(32) references courier(phone_number)
);
create table if not exists retrieval_information (
    item varchar(32) primary key,
    city varchar(32) not null,
    start_time date not null,
    courier_phone_number varchar(32) not null references courier(phone_number)
);
ALTER TABLE item ADD CONSTRAINT fk_item_cc FOREIGN KEY (container_code) REFERENCES container(code);
ALTER TABLE item ADD CONSTRAINT fk_item_sn FOREIGN KEY (ship_name) REFERENCES  ship(name);
ALTER TABLE import_information ADD CONSTRAINT fk_ii FOREIGN KEY (item) REFERENCES item(name);
ALTER TABLE export_information ADD CONSTRAINT fk_ei FOREIGN KEY (item) REFERENCES item(name);
ALTER TABLE retrieval_information ADD CONSTRAINT fk_ri_in FOREIGN KEY (item) REFERENCES item(name);
ALTER TABLE retrieval_information ADD CONSTRAINT fk_ri_cpn FOREIGN KEY (courier_phone_number) REFERENCES courier(phone_number);
ALTER TABLE delivery_information ADD CONSTRAINT fk_di_in FOREIGN KEY (item) REFERENCES item(name);
ALTER TABLE delivery_information ADD CONSTRAINT fk_di_cpn FOREIGN KEY (courier_phone_number) REFERENCES courier(phone_number);