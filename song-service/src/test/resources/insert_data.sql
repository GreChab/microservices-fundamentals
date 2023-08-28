create schema xxx;

set schema xxx;

create table songs
(
    `id`          INTEGER PRIMARY KEY AUTO_INCREMENT,
    `name`        varchar,
    `artist`      varchar,
    `album`       varchar,
    `length`      varchar,
    `resource_id` INTEGER,
    `year`        INTEGER
);

insert into songs (`id`, `name`, `artist`, `album`, `length`, `resource_id`, `year`)
values (1, 'sample name', 'sample artist', 'sample album', 'sample length', 1, 1111);
