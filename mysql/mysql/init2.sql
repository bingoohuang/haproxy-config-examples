USE mysql;
CREATE USER 'haproxy'@'%';
FLUSH PRIVILEGES;

create database bjca;
use bjca;
create table t1(name varchar(10));
insert into t1(name) values('dingoo');
