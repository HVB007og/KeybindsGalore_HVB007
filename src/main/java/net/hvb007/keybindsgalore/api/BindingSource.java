package net.hvb007.keybindsgalore.api;

import java.util.List;

/**
 * Implemented by a mod to expose its keybind actions to KeybindsGalore.
 *
 * <p>A source is the only party allowed to invoke its own actions. Return
 * {@link InvocationResult#REJECTED} from {@link #invoke(InvocationRequest)} for
 * anything the source cannot perform, which is the correct response for a
 * read-only source such as vanilla Minecraft mappings.
 */
public interface BindingSource {
    /**
     * Returns this source's stable identifier, for example {@code "minecraft"}.
     *
     * @return a non-blank identifier, unique across registered sources
     */
    String sourceId();

    /**
     * Lists every action this source currently exposes.
     *
     * @return the bindings; must not be null
     */
    List<BindingDescriptor> discoverBindings();

    /**
     * Performs the requested action, if this source supports it.
     *
     * @param request the exact source, action and physical key to invoke
     * @return the outcome; must not be null
     */
    InvocationResult invoke(InvocationRequest request);
}
