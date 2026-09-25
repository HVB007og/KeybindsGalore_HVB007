package net.hvb007.keybindsgalore.api;

import java.util.List;

/**
 * Read-only discovery and exact-invocation contract for other mods.
 *
 * <p>Obtain the instance from {@code KeybindsGalore.getApi()}. Register a
 * {@link BindingSource} to expose your mod's actions, then use
 * {@link #discoverConflicts()} to find keys claimed by more than one source.
 *
 * <p>External mutation of another mod's bindings is intentionally not supported.
 * {@link #invoke(InvocationRequest)} only succeeds when the target source itself
 * supplied a callback for that exact source, action and physical key.
 *
 * @see BindingSource
 * @see ApiVersion
 */
public interface KeybindApi {
    /**
     * Returns the API contract version, so callers can gate on availability.
     *
     * @return the current API version
     */
    ApiVersion version();

    /**
     * Registers a source of keybind actions.
     *
     * @param source the source to register
     * @throws IllegalArgumentException if {@code source.sourceId()} is null or blank
     * @throws IllegalStateException    if a source with the same id is already registered
     */
    void registerSource(BindingSource source);

    /**
     * Removes a previously registered source.
     *
     * @param sourceId the id passed by {@link BindingSource#sourceId()}
     * @return true if a source was removed
     */
    boolean unregisterSource(String sourceId);

    /**
     * Collects every binding exposed by every registered source.
     *
     * @return an immutable list of bindings
     * @throws IllegalStateException if a source returns null or a duplicate binding
     */
    List<BindingDescriptor> discoverBindings();

    /**
     * Groups all discovered bindings by physical key and returns groups of two or more.
     *
     * @return an immutable list of conflicting groups
     */
    List<ConflictDescriptor> discoverConflicts();

    /**
     * Invokes an action, but only through a callback the owning source provided.
     *
     * @param request the exact source, action and physical key to invoke
     * @return the outcome; never null
     */
    InvocationResult invoke(InvocationRequest request);
}
