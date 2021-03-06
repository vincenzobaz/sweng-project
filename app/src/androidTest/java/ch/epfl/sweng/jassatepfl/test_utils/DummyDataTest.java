package ch.epfl.sweng.jassatepfl.test_utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.sweng.jassatepfl.model.GPSPoint;
import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.model.Player;
import ch.epfl.sweng.jassatepfl.model.Player.PlayerID;

import static ch.epfl.sweng.jassatepfl.model.Match.GameVariant.CHIBRE;
import static ch.epfl.sweng.jassatepfl.model.Match.GameVariant.OBEN_ABE;

public class DummyDataTest {

    public static Player amaury = new Player(new PlayerID(235400), "Combes", "Amaury", 123);
    public static Player vincenzo = new Player(new PlayerID(249733), "Bazzucchi", "Vincenzo", 456);
    public static Player dorian = new Player(new PlayerID(234832), "Laforest", "Dorian", 789);
    public static Player alexis = new Player(new PlayerID(245433), "Montavon", "Alexis", 321);
    public static Player nicolas = new Player(new PlayerID(239293), "Phan Van", "Nicolas", 654);
    public static Player jimmy = new Player(new PlayerID(888888), "Boulet", "Jimmy", 987);
    public static Player random = new Player(new PlayerID(999999), "Smith", "John", 741);
    public static Player colin = new Player(new PlayerID(777777), "Branca", "Colin", 852);
    public static Player marco = new Player(new PlayerID(666666), "Ballerini", "Marco", 963);
    public static Player bricoloBob = new Player(new PlayerID(696969), "LeBricoleur", "Bob", 1000);

    public static GPSPoint rolexCoord = new GPSPoint(46.518299, 6.568323);
    public static GPSPoint BCCoord = new GPSPoint(46.518470, 6.561907);
    public static GPSPoint COCoord = new GPSPoint(46.520050, 6.564625);
    public static GPSPoint swissTechCoord = new GPSPoint(46.523103, 6.564649);
    public static GPSPoint CECoord = new GPSPoint(46.520525, 6.569554);
    public static GPSPoint flonCoord = new GPSPoint(46.520858, 6.629570);
    public static GPSPoint lemanCoord = new GPSPoint(46.453986, 6.553145);
    public static Map<String, Boolean> hasCardsEmpty = new HashMap<>();

    public static Match onePlayerMatch() {
        List<Player> match1Players = new ArrayList<>();
        match1Players.add(amaury);
        return new Match(match1Players, rolexCoord, "Rolex", false, CHIBRE, expirationTime(2), "one_player", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match onePlayerMatchWithBob() {
        List<Player> match1Players = new ArrayList<>();
        match1Players.add(bricoloBob);
        return new Match(match1Players, rolexCoord, "Rolex", false, CHIBRE, expirationTime(2), "one_player_bob");
    }

    public static Match twoPlayersMatch() {
        List<Player> match2Players = new ArrayList<>();
        match2Players.add(vincenzo);
        match2Players.add(dorian);
        return new Match(match2Players, BCCoord, "BC", false, CHIBRE, expirationTime(24), "two_players", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match threePlayersMatch() {
        List<Player> match3Players = new ArrayList<>();
        match3Players.add(colin);
        match3Players.add(nicolas);
        match3Players.add(alexis);
        return new Match(match3Players, CECoord, "CE", false, CHIBRE, expirationTime(3), "three_players", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match ownedMatch() {
        List<Player> matchPlayers = new ArrayList<>();
        matchPlayers.add(bricoloBob);
        matchPlayers.add(jimmy);
        matchPlayers.add(vincenzo);
        matchPlayers.add(nicolas);
        Match ownedMatch = new Match(matchPlayers, flonCoord, "Flon", false, CHIBRE, expirationTime(2), "owned");
        ownedMatch.setTeam(0, bricoloBob.getID());
        ownedMatch.setTeam(0, jimmy.getID());
        ownedMatch.setTeam(1, nicolas.getID());
        ownedMatch.setTeam(1, vincenzo.getID());
        return ownedMatch;
    }

    public static Match noPlayersMatch() {
        return new Match(new ArrayList<Player>(), lemanCoord,
                "Cette String est beaucoup trop longue, je me demande si l'affichage va foirer???",
                false, CHIBRE, expirationTime(12), "no_players", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match privateMatch() {
        List<Player> privatePlayers = new ArrayList<>();
        privatePlayers.add(jimmy);
        return new Match(privatePlayers, swissTechCoord, "SwissTech", true, CHIBRE, expirationTime(1), "private", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match fullMatch() {
        List<Player> fullPlayers = new ArrayList<>();
        fullPlayers.add(random);
        fullPlayers.add(marco);
        fullPlayers.add(dorian);
        fullPlayers.add(vincenzo);
        return new Match(fullPlayers, COCoord, "CO", false, CHIBRE, expirationTime(3), "full", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match fullMatchWithTeam() {
        List<Player> fullPlayers = new ArrayList<>();
        fullPlayers.add(random);
        fullPlayers.add(marco);
        fullPlayers.add(dorian);
        fullPlayers.add(vincenzo);
        Match fullMatch = new Match(fullPlayers, COCoord, "CO", false, CHIBRE, expirationTime(3), "full");
        fullMatch.setTeam(0, random.getID());
        fullMatch.setTeam(0, marco.getID());
        fullMatch.setTeam(1, dorian.getID());
        fullMatch.setTeam(1, vincenzo.getID());
        return fullMatch;
    }

    public static Match fullMatchWithBob() {
        List<Player> fullPlayers = new ArrayList<>();
        fullPlayers.add(marco);
        fullPlayers.add(bricoloBob);
        fullPlayers.add(dorian);
        fullPlayers.add(vincenzo);
        Match match = new Match(fullPlayers, COCoord, "CO", false, CHIBRE, expirationTime(3), "full_bob");
        match.setTeam(0, marco.getID());
        match.setTeam(0, bricoloBob.getID());
        match.setTeam(1, dorian.getID());
        match.setTeam(1, vincenzo.getID());
        return match;
    }

    public static Match matchWithBob() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(bricoloBob);
        return new Match(playerList, COCoord, "CO", false, CHIBRE, expirationTime(3), "bob", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match matchForTest() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(bricoloBob);
        playerList.add(random);
        return new Match(playerList, COCoord, "CO", false, OBEN_ABE, expirationTime(3), "pomme");
    }

    public static Match match_one_p_california() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(amaury);
        return new Match(playerList, new GPSPoint(37.422, -122.084), "Rolex", false, CHIBRE,
                expirationTime(3), "one_player_california", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match matchFullCalifornia() {
        List<Player> fullPlayers = new ArrayList<>();
        fullPlayers.add(random);
        fullPlayers.add(marco);
        fullPlayers.add(dorian);
        fullPlayers.add(vincenzo);
        return new Match(fullPlayers, new GPSPoint(37.422, -122.084), "CO", false, CHIBRE,
                expirationTime(3), "fullCalifornia", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Match matchBobCalifornia() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(bricoloBob);
        return new Match(playerList, new GPSPoint(37.422, -122.084), "CO", false, CHIBRE,
                expirationTime(3), "bobCalifornia", hasCardsEmpty, Match.MatchStatus.PENDING);
    }

    public static Set<Player> players() {
        Set<Player> players = new HashSet<>();
        players.add(amaury);
        players.add(vincenzo);
        players.add(dorian);
        players.add(alexis);
        players.add(nicolas);
        players.add(jimmy);
        players.add(random);
        players.add(colin);
        players.add(marco);
        players.add(bricoloBob);
        return players;
    }

    private static long expirationTime(int hourOffset) {
        return Calendar.getInstance().getTimeInMillis() + hourOffset * 3600 * 1000;
    }

}
