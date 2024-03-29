package br.usp.language.automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Representacao de um estado em um automato finito.
 * 
 * @author Marcelo Li Koga
 * @version 1.0 InputType Generico
 * @version 1.1 Somente Strings sao entrada
 */
public class State {

    /** Enum definition: StateType */
    public enum StateType {
        ACCEPT_STATE, NORMAL_STATE, ERROR_STATE
    };

    /* Attributes */

    /** Nome identificador do estado */
    private String name;

    private StateType type;

    /** Lista das transições que partem do estado em direção a outro */
    private List<Transition> transitions;

    /** Submachine call, special type of transition */
    private List<MachineCall> machineCalls;

    /** State may have epsilon transitions */
    private List<Transition> epsilonTransitions;

    /**
     * Transição que será considerada caso nenhuma das condições das transições normais, contidas na lista acima, seja
     * atendida
     */
    private Transition alternateTransition;

    /* Constructor */

    public State(String name) {
        this.name = name;
        this.transitions = new ArrayList<Transition>();
        this.machineCalls = new ArrayList<MachineCall>();
        this.epsilonTransitions = new ArrayList<Transition>();
        this.alternateTransition = null;
        this.type = StateType.NORMAL_STATE;
    }

    /* Methods */

    public String getName() {
        return name;
    }

    /**
     * Returns all transitions with a normal condition, i.e.,
     * do not return: machine calls or epsilon transitions
     * @return List with these transitions
     * @see getAllTransitions()
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    public StateType getType() {
        return this.type;
    }

    public List<MachineCall> getMachineCalls() {
        return this.machineCalls;
    }

    /**
     * Cria uma nova transição e a insere na lista de transições do estado.
     * 
     * @param to Estado destino da transição
     * @param condition Entrada necessária para que a transição ocorra
     * @param action Ação que deve ser tomada quando a transição ocorrer. Pode ser null.
     */
    public void createTransitionTo(State to, String condition, Action action) {
        this.transitions.add(new Transition(this, to, condition, action));
    }

    /**
     * Cria uma nova transição e a insere na lista de transições do estado.
     * 
     * @param to Estado destino da transição
     * @param conditions Entradas necessárias para que a transição ocorra
     * @param action Ação que deve ser tomada quando a transição ocorrer. Pode ser null.
     */
    public void createTransitionTo(State to, List<String> conditions, Action action) {
        this.transitions.add(new Transition(this, to, conditions, action));
    }

    /**
     * Cria uma nova transição e a insere na lista de transições do estado.
     * 
     * @param to Estado destino da transição
     * @param machineName Nome da maquina que será chamada
     * @param action Ação que deve ser tomada quando a transição ocorrer. Pode ser null.
     */
    public void createMachineCallTo(State to, String machineName, Action actionBefore, Action actionAfter) {
        this.machineCalls.add(new MachineCall(this, to, actionBefore, actionAfter, machineName));
    }

    /**
     * Cria uma nova transição e a insere na lista de transições do estado.
     * 
     * @param to Estado destino da transição
     * @param condition Entrada necessária para que a transição ocorra
     * @param action Ação que deve ser tomada quando a transição ocorrer. Pode ser null.
     */
    public void createEpsilonTransitionTo(State to, Action action) {
        this.epsilonTransitions.add(new Transition(this, to, Transition.EPSILON, action));
    }

    @Deprecated
    public Transition getEpsilonTransition() {
        if (!this.epsilonTransitions.isEmpty())
            return this.epsilonTransitions.get(0);
        else
            return null;
    }

    public List<Transition> getEpsilonTransitions() {
        return this.epsilonTransitions;
    }

    public boolean hasEpsilonTransition() {
        return !this.epsilonTransitions.isEmpty();
    }

    /**
     * Creates an alternate transition to the ones in the list. This transision does not have conditions.
     * 
     * @param alternateTransition
     */
    public void setAlternateTransition(State to, Action action) {
        this.alternateTransition = new Transition(this, to, action);
    }

    public Transition getAlternateTransition() {
        return alternateTransition;
    }

    /**
     * @return whether the state has an alternative transition or not
     */
    public boolean hasAlternateTransition() {
        if (this.alternateTransition != null)
            return true;
        return false;
    }

    /**
     * @return whether the state has machine calls or not
     */
    public boolean hasMachineCalls() {
        if (this.machineCalls.size() > 0)
            return true;
        return false;
    }
    
    /**
     * Returns all Transitions from this state, including machineCalls and epsilon.
     * @return
     */
    public List<Transition> getAllTransitions() {
        ArrayList<Transition> all = new ArrayList<Transition>();
        all.addAll(this.transitions);
        all.addAll(this.epsilonTransitions);
        all.addAll(this.machineCalls);
        
        return all;
    }

    /**
     * Given an input, verifies which transition will be activated and return the next state.
     * 
     * @param input
     * @return next state
     */
    public State getNextState(String input) {
        // Searches for the right condition

        for (Transition trans : this.transitions) {
            if (trans.getConditions().contains(input)) {
                return trans.getStateTo();
            }
        }
        for (Transition trans : this.machineCalls) {
            if (trans.getConditions().contains(input)) {
                return trans.getStateTo();
            }
        }
        // If none of the conditions is met
        return null;
    }

    /**
     * Returns the state which the first epsilon transition leads to
     * 
     * @return null if there is not an epsilon transition
     */
    @Deprecated
    public State getNextEpsilonState() {
        if (!this.epsilonTransitions.isEmpty()) {
            return this.epsilonTransitions.get(0).getStateTo();
        }
        return null;
    }

    /**
     * Returns the set of state which the epsilon transition leads to
     * 
     * @return null if there is not an epsilon transition
     */
    public State[] getNextEpsilonStates() {
        Collection<State> nextStates = new ArrayList<State>();
        for (Transition e : this.epsilonTransitions) {
            nextStates.add(e.getStateTo());
        }
        return nextStates.toArray(new State[0]);
    }

    /**
     * @return Next State of the alternative transition
     */
    @Deprecated
    public State getNextState() {
        if (this.alternateTransition != null)
            return this.alternateTransition.execute();
        else
            return null;
    }

    /**
     * @param input the input to be verified
     * @return whether the given input leads to a state change or not
     */
    public boolean containsTransitionForInput(String input) {
        for (Transition transition : this.transitions) {
            if (transition.getConditions().contains(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param s a state
     * @return whether there is a transition from this state to s or not
     */
    public boolean containsTransitionTo(State s) {
        for (Transition transition : this.getAllTransitions()) {
            if (transition.getStateTo().equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param s a state
     * @return Transition from this state to state s. Null, if does not exist.
     */
    public Transition getTransitionTo(State s) {
        for (Transition transition : this.getAllTransitions()) {
            if (transition.getStateTo().equals(s)) {
                return transition;
            }
        }
        return null;
    }

    /**
     * @param Name of machine to be called
     * @return whether this state has it or not
     */
    public boolean containsMachineCall(String machineName) {
        for (MachineCall m : this.machineCalls) {
            if (m.getCalledMachineName().equals(machineName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Turns the state into an accept State
     */
    public void setAcceptState() {
        this.type = StateType.ACCEPT_STATE;
    }

    /**
     * Turns the state into an error State
     */
    public void setErrorState() {
        this.type = StateType.ERROR_STATE;
    }

    public boolean isAcceptState() {
        if (this.type == StateType.ACCEPT_STATE)
            return true;
        return false;
    }

    public boolean isErrorState() {
        if (this.type == StateType.ERROR_STATE)
            return true;
        return false;
    }

    // methods that change the automata behavior
    // intended to allow adaptatvive actions

    /**
     * 
     * @param t a valid state transition
     * @return true if the transition was removed; false if there were not such transition
     */
    public boolean removeTransition(Transition t) {

        return this.transitions.remove(t);
    }

    public void setStateType(StateType type) {

        this.type = type;
    }
    
    public void addTransition(Transition t) {
        if (t.isEpsilon()) {
            this.epsilonTransitions.add(t);
        } else if (t.isMachineCall()) {
            this.machineCalls.add((MachineCall)t);
        } else {
            this.transitions.add(t);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.getType();
    }
}
