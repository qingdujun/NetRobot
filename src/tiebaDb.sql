create database tiebadb default charset utf8;

create table `setting` (
	`id` int(11) not null AUTO_INCREMENT,
	`bar_url` varchar(100) not null unique,
	`bar_name` varchar(100),
	`bar_crawl_note_count` int(11) default '200000',
	`crawl_frequency` int(11) default '60',
	`last_crawl_time` varchar(50),
	`state` enum('1','0'),
	
	primary key(`id`),
	index(`bar_url`)
)ENGINE=InnoDB default charset utf8;
	
create table `topicnote` (
	`id` int(11) not null AUTO_INCREMENT,
	`note_url` varchar(100) not null unique,
	`topic_reply_count` int(11) default '0',
	`note_title` varchar(100) not null,
	`last_reply_time` varchar(50),
	`state` enum('1','0'),
	
	primary key(`id`),
	index(`note_url`)
)ENGINE=InnoDB default charset utf8;
	
create table `notedetail` (
	`id` int(11) not null AUTO_INCREMENT,
	`note_url` varchar(100) not null,
	`reply_floor_id` varchar(100) not null unique,
	`reply_parent_id` varchar(100) not null,
	`reply_context` varchar(6000),
	`lzl_reply_count` int(11) default '-1',
	`state` enum('1','0'),
	
	primary key(`id`),
	index(`note_url`),
	index(`reply_floor_id`),
	index(`reply_parent_id`)
)ENGINE=InnoDB default charset utf8;