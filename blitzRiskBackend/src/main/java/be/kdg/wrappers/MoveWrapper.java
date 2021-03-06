package be.kdg.wrappers;

import be.kdg.model.Move;
import be.kdg.model.Player;
import be.kdg.model.PlayerStatus;
import be.kdg.model.Territory;
import org.apache.log4j.Logger;

/**
 * Wraps the move model in a simple wrapper that is used in REST-calls
 */
public class MoveWrapper {
    static Logger log = Logger.getLogger(MoveWrapper.class);


    private Integer id;
    private Integer turnId;
    private Integer turnNumber;
    private Integer origin;
    private Integer originNrOfUnits;
    private Integer originPlayer;
    private Integer destination;
    private Integer destinationNrOfUnits;
    private Integer destinationPlayer;
    private Integer unitsToAttackOrReinforce;
    private Integer playerOnTurn;
    private PlayerStatus playerStatus;

    public MoveWrapper (Move move) {
        id = move.getId();
        origin = move.getOriginTerritory().getId();
        originNrOfUnits = move.getOriginTerritory().getNumberOfUnits();
        originPlayer = move.getOriginTerritory().getPlayer().getId();
        turnId = move.getTurn().getId();
        turnNumber = move.getTurn().getNumber();
        Territory dest = move.getDestinationTerritory();
        destination = dest.getId();
        destinationNrOfUnits = dest.getNumberOfUnits();
        Player player = dest.getPlayer();
        destinationPlayer = player.getId();
        unitsToAttackOrReinforce = move.getNumberOfUnitsToAttack();
        playerOnTurn = move.getTurn().getGame().getPlayerTurn();
        playerStatus = move.getTurn().getGame().getPlayers().get(playerOnTurn).getPlayerStatus();
    }

    public MoveWrapper() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getUnitsToAttackOrReinforce() {
        return unitsToAttackOrReinforce;
    }

    public void setUnitsToAttackOrReinforce(int unitsToAttackOrReinforce) {
        this.unitsToAttackOrReinforce = unitsToAttackOrReinforce;
    }

    public int getTurnId() {
        return turnId;
    }

    public void setTurnId(int turnId) {
        this.turnId = turnId;
    }

    public int getOriginNrOfUnits() {
        return originNrOfUnits;
    }

    public void setOriginNrOfUnits(int originNrOfUnits) {
        this.originNrOfUnits = originNrOfUnits;
    }

    public int getOriginPlayer() {
        return originPlayer;
    }

    public void setOriginPlayer(int originPlayer) {
        this.originPlayer = originPlayer;
    }

    public int getDestinationNrOfUnits() {
        return destinationNrOfUnits;
    }

    public void setDestinationNrOfUnits(int destinationNrOfUnits) {
        this.destinationNrOfUnits = destinationNrOfUnits;
    }

    public int getDestinationPlayer() {
        return destinationPlayer;
    }

    public void setDestinationPlayer(int destinationPlayer) {
        this.destinationPlayer = destinationPlayer;
    }

    public int getPlayerOnTurn() {
        return playerOnTurn;
    }

    public void setPlayerOnTurn(int playerOnTurn) {
        this.playerOnTurn = playerOnTurn;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }
}
