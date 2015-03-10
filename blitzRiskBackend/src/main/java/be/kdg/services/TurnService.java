package be.kdg.services;

import be.kdg.dao.*;
import be.kdg.exceptions.IllegalMoveException;
import be.kdg.exceptions.IllegalTurnException;
import be.kdg.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Created by Alexander on 13/2/2015.
 */

@Transactional
@Service("turnService")
public class TurnService {
    static Logger log = Logger.getLogger(TurnService.class);

    @Autowired
    private MoveDao moveDao;

    @Autowired
    private TurnDao turnDao;

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private TerritoryDao territoryDao;

    public void saveTurn(Turn turn) {
        turnDao.updateTurn(turn);
    }

    public void removeTurns(Game game) {
        for(Turn turn: game.getTurns()) {
            for(Move move: turn.getMoves()) {
                moveDao.removeMove(move);
            }
            turnDao.removeTurn(turn);
        }
    }

    public int getTurnNumber(int turnId) {
        Turn turn = turnDao.getTurnById(turnId);
        return turn.getNumber();
    }

    public List<Move> getMoves() {
        return moveDao.findall();
    }

    public Turn getTurn(int turnId) {
        return turnDao.getTurnById(turnId);
    }

    public Turn getTurn(Player player) throws IllegalMoveException{
        if(player.getPlayerStatus().equals(PlayerStatus.WAITING)) throw new IllegalMoveException("player is not on turn");
        else {
            Game game = player.getGame();
            List<Turn> turns = game.getTurns();
            return turns.get(turns.size()-1);
        }
    }

    public List<Turn> getRecentTurns(int gameId, int number) {
        List<Turn> turns = new ArrayList<>();
        Game game = gameDao.getGame(gameId);
        List<Turn> allTurns = game.getTurns();

        for (; number < allTurns.size(); number++) {
            turns.add(allTurns.get(number));
        }
        return turns;
    }

    public Turn createTurn(int playerId){
        Player player = playerDao.getPlayerById(playerId);
        Game game = player.getGame();
        return createTurn(game, player);
    }

    public Turn createTurn(Game game, Player player){
        List <Turn> turns = game.getTurns();
        if (turns.size() > 0) {
            Turn oldTurn = turns.get(turns.size() - 1);
            oldTurn.setActive(false);
            turnDao.updateTurn(oldTurn);
        }

        Turn turn = new Turn();
        turn.setGame(game);
        turn.setPlayer(player);
        turn.setNumber(turns.size());
        turn.setActive(true);
        turnDao.updateTurn(turn);
        return turn;
    }

    public void updateTurnAfterMove(Turn turn, List<Move> moves) {
        for (Move move : moves) {
            turn.getCalculatedMoves().add(move);
            moveDao.updateMove(move);
            territoryDao.updateTerritory(move.getOriginTerritory());
            territoryDao.updateTerritory(move.getDestinationTerritory());
        }
        turnDao.updateTurn(turn);
    }

    public void playerOnTurnCheck(Turn turn, Player player) throws IllegalTurnException {
        if (!turn.isActive() || !turn.getPlayer().getId().equals(player.getId())) throw new IllegalTurnException();
    }

    public void setPlayerTurn(Player player, PlayerStatus playerStatus) throws IllegalMoveException{
        //TODO: player on turn check
        PlayerStatus currentPlayerStatus = player.getPlayerStatus();
        //checking if new playerStatus is a sequence of the current player status
        if ((playerStatus.equals(PlayerStatus.REINFORCE)&& currentPlayerStatus.equals(PlayerStatus.WAITING))
                || (playerStatus.equals(PlayerStatus.ATTACK) && currentPlayerStatus.equals(PlayerStatus.REINFORCE))
                || (playerStatus.equals(PlayerStatus.MOVE) && currentPlayerStatus.equals(PlayerStatus.ATTACK))
                || playerStatus.equals(PlayerStatus.WAITING) && currentPlayerStatus.equals(PlayerStatus.MOVE) ) {
            player.setPlayerStatus(playerStatus);
            playerDao.updatePlayer(player);
            if (playerStatus.equals(PlayerStatus.WAITING)) {
                Game game = player.getGame();
                int newPlayerTurn = game.getPlayerTurn() + 1;
                if (newPlayerTurn >= game.getPlayers().size()) newPlayerTurn = 0;
                Player newPlayer = game.getPlayers().get(newPlayerTurn);
                newPlayer.setPlayerStatus(PlayerStatus.REINFORCE);
                playerDao.updatePlayer(newPlayer);
                game.setPlayerTurn(newPlayerTurn);
                gameDao.updateGame(game);
            }
        }
        else throw new IllegalMoveException("new playerStatus isn't allowed: current = " + currentPlayerStatus + " new = " + playerStatus);
    }
}
