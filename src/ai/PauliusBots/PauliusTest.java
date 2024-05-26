package ai.PauliusBots;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PlayerAction;
import rts.PlayerActionGenerator;
import rts.UnitAction;
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

    HashMap<Long, HashSet<UnitAction>> actionRanks;

    public PauliusTest(UnitTypeTable utt) {

        super(-1,-1);
        m_utt = utt;

        actionRanks = new HashMap<>();

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

        PlayerActionGenerator actionGenerator = new PlayerActionGenerator(gs, player);
        List<Pair<Unit,List<UnitAction>>> possibleMoves = actionGenerator.getChoices();

        for(Pair<Unit, List<UnitAction>> pair: possibleMoves) {

            Unit unit = pair.m_a;
            List<UnitAction> possibleUnitActions = pair.m_b;

            boolean unitHasAction = false;

            if (actionRanks.containsKey(unit.getID())) {
                    HashSet<UnitAction> actionsTried = actionRanks.get(unit.getID());
                    for (UnitAction action : possibleUnitActions) {

                        if(unitHasAction) continue;

                        if (!actionsTried.contains(action)) {
                            if(unit.canExecuteAction(action, gs)) {
                                if(!unit.getType().canMove) {
                                    System.out.println("UNIT: " + unit);
                                    System.out.println("ACTIONS: " + actionsTried);
                                    System.out.println("NEXT ACTION: " + action);
                                }
                                actionsTried.add(action);
                                playerAction.addUnitAction(unit, action);
                                actionRanks.put(unit.getID(), actionsTried);
                                unitHasAction = true;
                            }
                        }
                    }
            } else {
                PlayerAction action = actionGenerator.getRandom();
                playerAction = playerAction.merge(action);
                actionRanks.put(unit.getID(), new HashSet<>(List.of(action.getAction(unit))));
            }

        }

        return playerAction;

    }
    // This will be called by the microRTS GUI to get the
    // list of parameters that this bot wants exposed
    // in the GUI.

    public List<ParameterSpecification> getParameters() {
        return new ArrayList<>();
    }
}
