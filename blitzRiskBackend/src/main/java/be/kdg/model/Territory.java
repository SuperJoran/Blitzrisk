package be.kdg.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by user jorandeboever
 * Date:2/02/15.
 */
@Entity
@Table(name = "t_territory")
public class Territory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private Integer numberOfUnits;
    private Integer gameKey;

    @Cascade(value=org.hibernate.annotations.CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name = "playerId")
    private Player player;

    //@Cascade(value=org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name="territoryId")
    private Set<Territory> neighbourTerritories = new HashSet<Territory>();

    @Cascade(value=org.hibernate.annotations.CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name= "gameId")
    private Game game;


    public Integer getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(Integer numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Territory> getNeighbourTerritories() {
        return neighbourTerritories;
    }

    public void setNeighbourTerritories(Set<Territory> neighbourTerritories) {
        this.neighbourTerritories = neighbourTerritories;
    }

    public Integer getGameKey() {
        return gameKey;
    }

    public void setGameKey(Integer gameId) {
        this.gameKey = gameId;
    }

    public void addNeighbour(Territory territory) {
        neighbourTerritories.add(territory);
    }

    public Integer getId() {
        return Id;
    }
}
