package be.kdg.services;

import be.kdg.dao.*;
import be.kdg.exceptions.IllegalMoveException;
import be.kdg.exceptions.IllegalTurnException;
import be.kdg.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Attack service provides a service for moving units between territories
 */
@Service("moveUnitsService")
public class MoveUnitsService {
    static Logger log = Logger.getLogger(TurnService.class);

    @Autowired
    private MoveDao moveDao;

    @Autowired
    private TurnDao turnDao;

    @Autowired
    private TurnService turnService;


    public void moveUnits(Turn turn, Player player, List<Move> moves) throws IllegalMoveException, IllegalTurnException {
        checkMove(turn, player, moves);
        List<Move> calculatedMoves = executeMoves(moves);
        turnService.setPlayerTurn(player, PlayerStatus.WAITING);
        turnService.updateTurnAfterMove(turn, calculatedMoves);
    }


    private void checkMove(Turn turn, Player player, List<Move> moves) throws IllegalMoveException, IllegalTurnException {
        turnService.playerOnTurnCheck(turn, player);
        for (Move move : moves) {
            Territory origin = move.getOriginTerritory();
            Territory destination = move.getDestinationTerritory();
            int newOriginUnits = origin.getNumberOfUnits() - move.getNumberOfUnitsToAttack();

            if ((newOriginUnits) < 1) throw new IllegalMoveException("Origin territory doesn't have enough units");
            if (!(origin.getPlayer().getId().equals(player.getId())))
                throw new IllegalMoveException("player doesn't own origin");
            if (!(destination.getPlayer().getId().equals(player.getId())))
                throw new IllegalMoveException("player doesn't own destination");

            /*boolean isNeighbour = true;
            for (Territory territory : origin.getNeighbourTerritories()) {
                if (territory.getId().equals(destination.getId())) isNeighbour = true;
            }
            if (!isNeighbour) throw new IllegalMoveException("territories aran't neighbours");*/
        }
    }

    private List<Move> executeMoves( List<Move> moves) {
        List<Move> calculatedMoves = new ArrayList<>();
        for(Move move: moves) {
            Territory origin = move.getOriginTerritory();
            Territory destination = move.getDestinationTerritory();
            int newOriginUnits = origin.getNumberOfUnits() - move.getNumberOfUnitsToAttack();
            int newDestinationUnits = destination.getNumberOfUnits() + move.getNumberOfUnitsToAttack();

            move.setDestinationTerritoryRemainingNrUnits(newDestinationUnits);
            move.setOriginTerritoryRemainingNrUnits(newOriginUnits);
            origin.setNumberOfUnits(newOriginUnits);
            destination.setNumberOfUnits(newDestinationUnits);

            calculatedMoves.add(move);
        }
        return calculatedMoves;
    }
}