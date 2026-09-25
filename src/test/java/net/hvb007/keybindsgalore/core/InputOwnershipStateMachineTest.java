package net.hvb007.keybindsgalore.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputOwnershipStateMachineTest {
    @Test
    void followsSelectorSelectionAndRelease() {
        InputOwnershipStateMachine machine = new InputOwnershipStateMachine();

        assertEquals(InputOwnershipState.IDLE, machine.state());
        machine.selectorOpened();
        assertEquals(InputOwnershipState.SELECTOR_OPEN, machine.state());
        machine.selectionMade();
        assertEquals(InputOwnershipState.SELECTED, machine.state());
        machine.released();
        assertEquals(InputOwnershipState.RELEASED, machine.state());
    }

    @Test
    void tracksPriorityAndCancellation() {
        InputOwnershipStateMachine machine = new InputOwnershipStateMachine();
        machine.priorityActivated();
        assertEquals(InputOwnershipState.PRIORITY_ACTIVE, machine.state());
        machine.cancelled();
        assertEquals(InputOwnershipState.CANCELLED, machine.state());
        machine.reset();
        assertEquals(InputOwnershipState.IDLE, machine.state());
    }
}
