package it.asg.hustle;

/**
 * Created by andrea on 26/08/15.
 */
public class InitialQuery {
    public static String create_episodes_table = "CREATE TABLE IF NOT EXISTS `episodes` (\n" +
            "  `id` int(10) NOT NULL AUTO_INCREMENT,\n" +
            "  `episodeid` int(10) NOT NULL,\n" +
            "  `Director` text,\n" +
            "  `EpisodeName` varchar(255) DEFAULT NULL,\n" +
            "  `EpisodeNumber` int(10) DEFAULT NULL,\n" +
            "  `FirstAired` varchar(45) DEFAULT NULL,\n" +
            "  `GuestStars` text,\n" +
            "  `IMDB_ID` varchar(25) NOT NULL,\n" +
            "  `Language` varchar(2) DEFAULT NULL,\n" +
            "  `Overview` text,\n" +
            "  `ProductionCode` varchar(45) DEFAULT NULL,\n" +
            "  `Rating` float DEFAULT NULL,\n" +
            "  `RatingCount` int(10) DEFAULT NULL,\n" +
            "  `SeasonNumber` int(10) DEFAULT NULL,\n" +
            "  `Writer` text,\n" +
            "  `absolute_number` int(3) DEFAULT NULL,\n" +
            "  `filename` varchar(100) DEFAULT NULL,\n" +
            "  `lastupdated` int(10) DEFAULT NULL,\n" +
            "  `seasonid` int(10) DEFAULT NULL,\n" +
            "  `seriesid` int(10) DEFAULT NULL,\n" +
            "  `thumb_added` datetime DEFAULT NULL,\n" +
            "  `thumb_height` smallint(5) DEFAULT NULL,\n" +
            "  `thumb_width` smallint(5) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ")";

    public static String create_series_table = "CREATE TABLE IF NOT EXISTS `tvseries` (\n" +
            "  `id` int(10) NOT NULL AUTO_INCREMENT,\n" +
            "  `seriesid` int(10) unsigned NOT NULL,\n" +
            "  `Actors` text,\n" +
            "  `Airs_DayOfWeek` varchar(45) DEFAULT NULL,\n" +
            "  `Airs_Time` varchar(45) DEFAULT NULL,\n" +
            "  `ContentRating` varchar(45) DEFAULT NULL,\n" +
            "  `FirstAired` varchar(100) DEFAULT NULL,\n" +
            "  `Genre` varchar(100) DEFAULT NULL,\n" +
            "  `IMDB_ID` varchar(25) DEFAULT NULL,\n" +
            "  `Language` varchar(2) DEFAULT NULL,\n" +
            "  `Network` varchar(100) DEFAULT NULL,\n" +
            "  `NetworkID` int(10) DEFAULT NULL,\n" +
            "  `Overview` text,\n" +
            "  `Rating` float DEFAULT NULL,\n" +
            "  `RatingCount` int(10) DEFAULT NULL,\n" +
            "  `Runtime` varchar(100) DEFAULT NULL,\n" +
            "  `SeriesName` varchar(255) DEFAULT NULL,\n" +
            "  `Status` varchar(100) DEFAULT NULL,\n" +
            "  `added` datetime DEFAULT NULL,\n" +
            "  `banner` varchar(100) DEFAULT NULL,\n" +
            "  `fanart` varchar(100) DEFAULT NULL,\n" +
            "  `lastupdated` int(10) DEFAULT NULL,\n" +
            "  `poster` varchar(100) DEFAULT NULL,\n" +
            "  `zap2it_id` varchar(12) DEFAULT NULL,\n" +
            "  `seasons` int(10) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ")";
        public static String create_users_table = "CREATE TABLE IF NOT EXISTS `users` (\n" +
                "  `id` int(10) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(64) DEFAULT NULL,\n" +
                "  `password` text,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ")";
        public static String create_seen_episodes_table = "CREATE TABLE IF NOT EXISTS `seen_episodes` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `user_id` int(12) NOT NULL,\n" +
                "  `episodeid` int(10) NOT NULL,\n" +
                "  `seriesid` int(10) NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ")";
}
