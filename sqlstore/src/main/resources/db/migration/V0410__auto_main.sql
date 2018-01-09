-- unique_pool_alias

ALTER TABLE Pool ADD CONSTRAINT uk_pool_alias UNIQUE (alias);


