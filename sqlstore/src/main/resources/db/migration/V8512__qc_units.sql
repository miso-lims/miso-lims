UPDATE QCType SET units = REPLACE(units, '&#181;', 'µ');
UPDATE QCType SET units = REPLACE(units, '&#37;', '%');
UPDATE QCType SET units = REPLACE(units, '&#178;', '²');
UPDATE QCType SET units = 'µL' WHERE units = 'uL';
