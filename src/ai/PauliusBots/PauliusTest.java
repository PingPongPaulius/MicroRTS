package ai.PauliusBots;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

import java.util.ArrayList;
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

    public PauliusTest(UnitTypeTable utt) {

        super(-1,-1);
        m_utt = utt;

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

    public PlayerAction getAction(int player, GameState gs) {
        PlayerAction pa = new PlayerAction();
        pa.fillWithNones(gs, player, 10);
        return pa;

    }

    // This will be called by the microRTS GUI to get the
    // list of parameters that this bot wants exposed
    // in the GUI.

    public List<ParameterSpecification> getParameters() {
        return new ArrayList<>();
    }
}
