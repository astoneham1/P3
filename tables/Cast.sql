CREATE TABLE IF NOT EXISTS `Cast` (
  `character` VARCHAR(30) NOT NULL,
  `MovieID` INTEGER NOT NULL,
  `ActorID` INTEGER NOT NULL,
  PRIMARY KEY(`character`, `MovieID`, `ActorID`),
  FOREIGN KEY (`ActorID`) REFERENCES `Actor`(`ActorID`),
  FOREIGN KEY (`MovieID`) REFERENCES `Movie`(`MovieID`)
);