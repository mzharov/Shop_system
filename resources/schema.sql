CREATE TABLE SHOPS (
  shop_id INT4 NOT NULL AUTO_INCREMENT
  , name VARCHAR(20) NOT NULL
  , budget INT4 NOT NULL
  , UNIQUE (shop_id)
  , PRIMARY KEY (shop_id)
);