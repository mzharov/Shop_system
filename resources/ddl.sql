CREATE USER 'ts.tsc.system'@'localhost' IDENTIFIED BY 'ts.tsc.system';

CREATE SCHEMA MUSICDB;
GRANT ALL PRIVILEGES ON MUSICDB . * TO 'ts.tsc.system'@'localhost';
FLUSH PRIVILEGES;