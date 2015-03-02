package be.kdg;

import be.kdg.dao.*;
import be.kdg.exceptions.IllegalMoveException;
import be.kdg.model.*;
import be.kdg.services.GameService;
import be.kdg.services.PlayerService;
import be.kdg.services.TurnService;
import be.kdg.services.UserService;
import junit.framework.Assert;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

/**
 * Created by Marlies on 22/02/2015.
 */

@ContextConfiguration(locations = {"file:src/test/resources/testcontext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class NewTurnServiceTest {
    private @Mock Game game;
    private List<Player> players;
    private List<Territory> territories;
    //private @Mock SessionFactory sessionFactory;
    //private @Mock Session session;
    //private @Mock Query query;
    private @Mock User user;

    /*@Autowired
    private TerritoryDao territoryDao;
    @Autowired
    private TurnDao turnDao;
    @Autowired
    private MoveDao moveDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private PlayerDao playerDao;*/

    @Autowired
    private UserService userService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;



    @Autowired
    private TurnService turnService;

    @Before
    public void createGame() {
        /*MockitoAnnotations.initMocks(this);
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        game.setPlayers(players);
        territoryDao.setSessionFactory(sessionFactory);
        turnDao.setSessionFactory(sessionFactory);
        moveDao.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);*/
        MockitoAnnotations.initMocks(this);
        /*territoryDao.setSessionFactory(sessionFactory);
        turnDao.setSessionFactory(sessionFactory);
        moveDao.setSessionFactory(sessionFactory);
        gameDao.setSessionFactory(sessionFactory);
        playerDao.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);*/


        for(int i = 0; i<3; i++) {
            userService.addUser("" + i, "" + i, i+"@test.be");
        }
        List<User> users = userService.findall();
        Game game = gameService.createNewGame();
        for (User user: users) {
            gameService.addUserToGame(user, game);
        }
        List<Player> playerList = game.getPlayers();
        for(Player player: playerList) {
            playerService.acceptGame(player.getId());
        }

        Set<Territory> territoriesPlayer1 = playerList.get(0).getTerritories();
        Set<Territory> territoriesPlayer2 = playerList.get(1).getTerritories();



        Territory origin = new Territory();
        origin.setNumberOfUnits(2);
        origin.setPlayer(players.get(0));
        Territory destination = new Territory();
        destination.setNumberOfUnits(1);
        destination.setPlayer(players.get(1));
        destination.addNeighbour(origin);
        origin.addNeighbour(destination);
        territories = new ArrayList<>();
        territories.add(origin);
        territories.add(destination);
        Set<Territory> playerTerritories = new HashSet<>();
        playerTerritories.add(origin);
        players.get(0).setTerritories(playerTerritories);
        playerTerritories = new HashSet<>();
        playerTerritories.add(destination);
        players.get(1).setTerritories(playerTerritories);

    }

    @Test
    public void attack() throws IllegalMoveException {
        Move move = new Move();
        move.setNumberOfUnitsToAttack(1);
        move.setDestinationTerritory(territories.get(1));
        move.setOriginTerritory(territories.get(0));
        List<Move> moves = new ArrayList<>();
        moves.add(move);
        Turn turn = turnService.createTurn(game, players.get(0), moves);
        Assert.assertTrue("turn isn't valid", turn.getCalculatedMoves().size() == 1);
    }

    @Test(expected = IllegalMoveException.class)
    public void attackFromForeignCountry() throws IllegalMoveException{
        Move move = new Move();
        move.setNumberOfUnitsToAttack(1);
        move.setDestinationTerritory(territories.get(1));
        move.setOriginTerritory(territories.get(0));
        List<Move> moves = new ArrayList<>();
        moves.add(move);
        turnService.createTurn(game, players.get(2), moves);
    }

    @Test(expected = IllegalMoveException.class)
    public void attackWithTooManyUnits() throws IllegalMoveException {
        Move move = new Move();
        move.setNumberOfUnitsToAttack(2);
        move.setDestinationTerritory(territories.get(1));
        move.setOriginTerritory(territories.get(0));
        List<Move> moves = new ArrayList<>();
        moves.add(move);
        turnService.createTurn(game, players.get(0), moves);
    }

    @Test
    public void someUnitsDied() throws IllegalMoveException {
        int count = 0;

        Move move = new Move();
        move.setNumberOfUnitsToAttack(35);
        move.setDestinationTerritory(territories.get(1));
        move.setOriginTerritory(territories.get(0));
        List<Move> moves = new ArrayList<>();
        moves.add(move);

        for(int i = 0; i< 5; i++) {
            territories.get(0).setNumberOfUnits(40);
            territories.get(1).setNumberOfUnits(40);
            turnService.createTurn(game, players.get(0), moves);
            if (move.getDestinationTerritoryRemainingNrUnits() == 40 || move.getOriginTerritoryRemainingNrUnits() == 40) {
                count++;
            }
        }
        Assert.assertFalse("Not all units should survive", count>4);
    }

    @Test
    public void someUnitsSurvived() throws IllegalMoveException {
        int count = 0;

        Move move = new Move();
        move.setNumberOfUnitsToAttack(35);
        move.setDestinationTerritory(territories.get(1));
        move.setOriginTerritory(territories.get(0));
        List<Move> moves = new ArrayList<>();
        moves.add(move);

        for(int i = 0; i< 5; i++) {
            territories.get(0).setNumberOfUnits(40);
            territories.get(1).setNumberOfUnits(40);
            turnService.createTurn(game, players.get(0), moves);
            if (move.getDestinationTerritoryRemainingNrUnits() == 0 || move.getOriginTerritoryRemainingNrUnits() < 2) {
                count++;
            }
        }
        Assert.assertFalse("Not all units should survive", count>4);
    }

    @Test
    public void differentResult() throws IllegalMoveException{
        int defendersCount = 0;
        int attackersCount = 0;

        for(int i = 0; i< 20; i++) {
            territories.get(0).setPlayer(players.get(0));
            territories.get(1).setPlayer(players.get(1));
            territories.get(0).setNumberOfUnits(2);
            territories.get(1).setNumberOfUnits(1);
            Move move = new Move();
            move.setNumberOfUnitsToAttack(1);
            move.setDestinationTerritory(territories.get(1));
            move.setOriginTerritory(territories.get(0));
            List<Move> moves = new ArrayList<>();
            moves.add(move);

            turnService.createTurn(game, players.get(0), moves);
            if(move.getDestinationTerritory().getPlayer() == players.get(0)) attackersCount++;
            else defendersCount++;
        }

        Assert.assertFalse("Player shouldn't always win", attackersCount==20);
        Assert.assertFalse("Player shouldn't always lose", defendersCount==20);
    }

    @Test
    public void reinforceWithFewTerritories() throws IllegalMoveException{
        Move move = new Move();
        move.setNumberOfUnitsToAttack(3);
        move.setDestinationTerritory(territories.get(0));
        move.setOriginTerritory(territories.get(0));
        List<Move> reinforcements = new ArrayList<>();
        reinforcements.add(move);
        Turn turn = turnService.createTurn(game, players.get(0));
        turnService.addReinforcements(turn, players.get(0), reinforcements);
        Assert.assertTrue("player should have 5 units", territories.get(0).getNumberOfUnits() == 5);
    }

    @Test
    public void reinforceWithManyTerritories() throws IllegalMoveException {
        HashSet playerTerritories = new HashSet();
        playerTerritories.add(territories.get(0));
        for (int i = 0; i < 12; i++) {
            Territory territory = new Territory();
            territory.setPlayer(players.get(0));
            territory.setNumberOfUnits(1);
            territories.add(territory);
            playerTerritories.add(territory);
        }
        players.get(0).setTerritories(playerTerritories);

        Move move = new Move();
        move.setNumberOfUnitsToAttack(4);
        move.setDestinationTerritory(territories.get(0));
        move.setOriginTerritory(territories.get(0));
        List<Move> reinforcements = new ArrayList<>();
        reinforcements.add(move);
        Turn turn = turnService.createTurn(game, players.get(0));
        turnService.addReinforcements(turn, players.get(0), reinforcements);
        Assert.assertTrue("Origin should have 6 units", territories.get(0).getNumberOfUnits() == 6);
    }

    @Test(expected = IllegalMoveException.class)
    public void reinforceTooManyUnits() throws IllegalMoveException{
        Move move = new Move();
        move.setNumberOfUnitsToAttack(5);
        move.setDestinationTerritory(territories.get(0));
        move.setOriginTerritory(territories.get(0));
        List<Move> reinforcements = new ArrayList<>();
        reinforcements.add(move);
        Turn turn = turnService.createTurn(game, players.get(0));
        turnService.addReinforcements(turn, players.get(0), reinforcements);
    }

    @Test(expected = IllegalMoveException.class)
    public void reinforceForeignTerritory() throws IllegalMoveException{
        Move move = new Move();
        move.setNumberOfUnitsToAttack(3);
        move.setDestinationTerritory(territories.get(0));
        move.setOriginTerritory(territories.get(0));
        List<Move> reinforcements = new ArrayList<>();
        reinforcements.add(move);
        Turn turn = turnService.createTurn(game, players.get(0));
        turnService.addReinforcements(turn, players.get(1), reinforcements);
    }

    @Test(expected = IllegalMoveException.class)
    public void wrongMoveSyntax() throws IllegalMoveException{
        Move move = new Move();
        move.setNumberOfUnitsToAttack(3);
        move.setDestinationTerritory(territories.get(0));
        move.setOriginTerritory(territories.get(1));
        List<Move> reinforcements = new ArrayList<>();
        reinforcements.add(move);
        Turn turn = turnService.createTurn(game, players.get(0));
        turnService.addReinforcements(turn, players.get(0), reinforcements);
    }
}