package ai.PauliusBots;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.*;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PauliusTest extends AIWithComputationBudget {
    /**
     * Constructs the controller with the specified time and iterations budget
     *
     * @param timeBudget       time in milisseconds
     * @param iterationsBudget number of allowed iterations
     */
    UnitTypeTable m_utt = null;
    // This is the default constructor that microRTS will call:

    /*
    Strategy is to pick best action and rank it.
     */

    private HashMap<Long, HashSet<UnitAction>> actionRanks;
    private HashSet<PlayerAction> playerActions;

    public PauliusTest(UnitTypeTable utt) {

        super(-1,-1);
        m_utt = utt;

        actionRanks = new HashMap<>();
        playerActions = new HashSet<>();

    }

    // This will be called by microRTS when it wants to create new instances of this bot (e.g., to play multiple games).

    public AI clone() {
        return new PauliusTest(m_utt);
    }

    // This will be called once at the beginning of each new game:

    public void reset() {

    }

    // Called by microRTS at each game cycle.
    // Returns the action the bot wants to execute.

    public PlayerAction getAction(int player, GameState gs) throws Exception {

        PlayerAction playerAction = new PlayerAction();
        if (!gs.canExecuteAnyAction(player)) return new PlayerAction();

        ArrayList<Unit> unitsReadyForAction = getUnitsReadyForAction(gs, player);

        for(Unit unit: unitsReadyForAction) {

            List<UnitAction> possibleUnitActions = unit.getUnitActions(gs);

            HashSet<UnitAction> actionsTried = actionRanks.getOrDefault(unit.getID(), new HashSet<UnitAction>());

            for (UnitAction action : possibleUnitActions) {
                if (!actionsTried.contains(action) && playerAction.getAction(unit) == null && unit.canExecuteAction(action, gs)) {

                    actionsTried.add(action);
                    actionRanks.put(unit.getID(), actionsTried);
                    playerAction.addUnitAction(unit, action);

                }
            }

        }

        return playerAction;

    }
    // This will be called by the microRTS GUI to get the
    // list of parameters that this bot wants exposed
    // in the GUI.

    protected ArrayList<Unit> getUnitsReadyForAction(GameState gs, int player){

        PhysicalGameState pgs = gs.getPhysicalGameState();

        ArrayList<Unit> unitsReadyForAction = new ArrayList<>();
        for(Unit u:pgs.getUnits()) {
            if (u.getPlayer()==player) {
                if (gs.getActionAssignment(u)==null) {
                    unitsReadyForAction.add(u);
                }
            }
        }

        return unitsReadyForAction;
    }

    public List<ParameterSpecification> getParameters() {
        return new ArrayList<>();
    }
}
