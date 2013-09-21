drop table fire_building;
drop table building;
drop table fire_hydrant;
delete from user_sdo_geom_metadata where table_name='BUILDING';
delete from user_sdo_geom_metadata where table_name='FIRE_BUILDING';
delete from user_sdo_geom_metadata where table_name='FIRE_HYDRANT';
SET DEFINE ON;
