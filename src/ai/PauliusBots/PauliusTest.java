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

    private HashMap<Long, ArrayList<UnitAction>> actionRanks;
    private HashSet<PlayerAction> playerActions;
    private HashMap<Long, List<UnitAction>> possibleLegalMovesThisFrame;

    public PauliusTest(UnitTypeTable utt) {

        super(-1,-1);
        m_utt = utt;

        actionRanks = new HashMap<>();
        playerActions = new HashSet<>();
        possibleLegalMovesThisFrame = new HashMap<>();
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
        // Get Units Ready for action. Copied from RandomSingleUnitAI Command
        ArrayList<Unit> unitsReadyForAction = getUnitsReadyForAction(gs, player);
        possibleLegalMovesThisFrame.clear();
        getUnitPossibleMoves(gs, player);
        // Go through each free unit and assign it an action.
        for(Unit unit: unitsReadyForAction) {
            List<UnitAction> possibleUnitActions = possibleLegalMovesThisFrame.get(unit.getID());
            ArrayList<UnitAction> actionsTried = actionRanks.getOrDefault(unit.getID(), new ArrayList<>());

            boolean unitIsIdle = true;

            for (UnitAction action : possibleUnitActions) {
                if (!actionsTried.contains(action) && playerAction.getAction(unit) == null) {
                    actionsTried.add(action);
                    actionRanks.put(unit.getID(), actionsTried);
                    playerAction.addUnitAction(unit, action);
                    unitIsIdle = false;
                }
            }

            if(unitIsIdle){
                ArrayList<UnitAction> bestUnitActions = actionRanks.get(unit.getID());
                for(UnitAction action: bestUnitActions){
                    if(playerAction.getAction(unit) == null && possibleUnitActions.contains(action) && legalAction(gs, unit, action)){
                        playerAction.addUnitAction(unit, action);
                        break;
                    }
                }
            }
        }

        return playerAction;
    }
    // This will be called by the microRTS GUI to get the
    // list of parameters that this bot wants exposed
    // in the GUI.

    protected boolean legalAction(GameState gs, Unit unit, UnitAction action){
        return gs.isUnitActionAllowed(unit, action) && unit.canExecuteAction(action, gs);
    }

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

    protected void getUnitPossibleMoves(GameState gs, int player) throws Exception {

        PlayerActionGenerator playerActionGenerator = new PlayerActionGenerator(gs, player);
        List<Pair<Unit, List<UnitAction>>> generatedMoves = playerActionGenerator.getChoices();
        for(Pair<Unit, List<UnitAction>> allPossibleActions: generatedMoves){
            ArrayList<UnitAction> legalActions = new ArrayList<>();
            Unit unit = allPossibleActions.m_a;
            for(UnitAction action: unit.getUnitActions(gs)){
                if(legalAction(gs, unit, action)){
                    legalActions.add(action);
                }
            }

            this.possibleLegalMovesThisFrame.put(allPossibleActions.m_a.getID(), legalActions);
        }
    }

    public List<ParameterSpecification> getParameters() {
        return new ArrayList<>();
    }
}
