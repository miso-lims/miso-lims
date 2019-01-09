-- watch

DROP TABLE Pool_Watcher;
DROP TABLE Project_Watcher;
DROP TABLE Run_Watcher;
DELETE FROM _Group WHERE name in ('RunWatchers', 'ProjectWatchers', 'PoolWatchers');


