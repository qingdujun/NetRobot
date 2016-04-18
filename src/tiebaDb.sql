create database tiebadb default charset utf8;

create table `tieba` (
	`id` int(20) not null AUTO_INCREMENT,
	`url` varchar(100) not null,
	`name` varchar(100),
	`crawl_topic` int(11) default '200',
	`crawl_reply` int(11) default '50',
	`frequency` int(11) default '60',
	`last_crawl` bigint(20),
	`state` enum('1','0'),
	
	primary key(`id`),
	index(`url`)
)ENGINE=InnoDB default charset utf8;


create table `tiebanote` (
	`id` int(20) not null AUTO_INCREMENT,
	`tid` varchar(100) not null,
	`rid` varchar(100) not null,
	`pid` varchar(100) not null,
	`title` varchar(100),
	`context` varchar(6000),
	`reply_count` int(11) default '0',
	`last_crawl` bigint(20),
	`state` enum('1','0'),
	
	primary key(`id`),
	index(`tid`),
	index(`rid`),
	index(`pid`)
)ENGINE=InnoDB default charset utf8;