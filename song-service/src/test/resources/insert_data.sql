create schema xxx;

set schema xxx;

create table t_songs
(
    `id`          INTEGER PRIMARY KEY AUTO_INCREMENT,
    `name`        varchar,
    `artist`      varchar,
    `album`       varchar,
    `length`      varchar,
    `resource_id` INTEGER,
    `year`        varchar
);

insert into t_songs (`id`, `name`, `artist`, `album`, `length`, `resource_id`, `year`)
values (1, 'sample name', 'sample artist', 'sample album', 'sample length', 1, '1111');
