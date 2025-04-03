CREATE TABLE IF NOT EXISTS `Actor` (
    `ActorID` INTEGER PRIMARY KEY AUTOINCREMENT,
    `FirstName` varchar(30) NOT NULL,
    `LastName` varchar(50) not NULL,
    `DoB` date NOT NULL,
    `nationality` varchar(30)
);