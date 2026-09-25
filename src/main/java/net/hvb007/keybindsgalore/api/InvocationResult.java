package net.hvb007.keybindsgalore.api;

/** Outcome of an {@link InvocationRequest}. */
public enum InvocationResult {
    /** The owning source performed the action. */
    SUCCEEDED,
    /** No source is registered under the requested id. */
    UNKNOWN_SOURCE,
    /** The source does not currently expose that action on that key. */
    UNKNOWN_BINDING,
    /** The source declined, for example because it is read-only or threw. */
    REJECTED
}
