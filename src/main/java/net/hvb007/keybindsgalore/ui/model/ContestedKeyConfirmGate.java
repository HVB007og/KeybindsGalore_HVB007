package net.hvb007.keybindsgalore.ui.model;

/**
 * Decides whether a press of the contested key should confirm the current selection.
 *
 * <p>In keyboard control mode the contested key doubles as confirm, which creates a problem the
 * mod cannot solve by ignoring the key. The operating system auto-repeats while a key is held, and
 * the mod receives those repeat presses as ordinary presses. Treating every one as a confirm
 * closes the menu the instant it opens, and treating none as a confirm means the key cannot be
 * used to confirm at all.
 *
 * <p>The rule is that confirmation requires an observed release since the menu opened. The mod
 * does receive real key-up events, delivered to the screen's release handler rather than through
 * the key-mapping press path, so this is evidence from an event the mod actually sees.
 *
 * <p>This is deliberately <em>not</em> a physical key-state poll. Polling the keyboard, or latching
 * "this key is still down" at finalisation, was written and reverted earlier: the mod cannot
 * reliably observe key-up at finalisation time, so such a latch swallows the next genuine press
 * and the menu stops opening entirely. Here the flag can only be set by a real release event, so a
 * press that arrives after it is by definition deliberate.
 */
public final class ContestedKeyConfirmGate {
    private boolean releaseObserved;

    /** Records that the contested key was released while the menu was open. */
    public void onRelease() {
        releaseObserved = true;
    }

    /**
     * @return {@code true} exactly once per observed release, when the player has deliberately
     *         pressed the contested key again and it should confirm.
     */
    public boolean shouldConfirmOnPress() {
        if (!releaseObserved) {
            return false;
        }
        releaseObserved = false;
        return true;
    }
}
