package io.wispforest.owo.util;

import com.google.common.collect.ForwardingMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_3497;
import net.minecraft.class_7924;

/**
 * A simple utility for inserting values into Tags at runtime
 */
public final class TagInjector {

    @ApiStatus.Internal
    public static final HashMap<TagLocation, Set<class_3497>> ADDITIONS = new HashMap<>();

    private static final Map<TagLocation, Set<class_3497>> ADDITIONS_VIEW = new ForwardingMap<>() {
        @Override
        protected @NotNull Map<TagLocation, Set<class_3497>> delegate() {
            return Collections.unmodifiableMap(ADDITIONS);
        }

        @Override
        public Set<class_3497> get(@Nullable Object key) {
            return Collections.unmodifiableSet(this.delegate().get(key));
        }
    };

    private TagInjector() {}

    /**
     * @return A view of all planned tag injections
     */
    public static Map<TagLocation, Set<class_3497>> getInjections() {
        return ADDITIONS_VIEW;
    }

    /**
     * Inject the given identifiers into the given tag
     * <p>
     * If any of the identifiers don't correspond to an entry in the
     * given registry, you <i>will</i> break the tag.
     * If the tag does not exist, it will be created.
     *
     * @param registry   The registry for which the injected tags should apply
     * @param tag        The tag to insert into, this could contain all kinds of values
     * @param entryMaker The function to use for creating tag entries from the given identifiers
     * @param values     The values to insert
     */
    public static void injectRaw(class_2378<?> registry, class_2960 tag, Function<class_2960, class_3497> entryMaker, Collection<class_2960> values) {
        ADDITIONS.computeIfAbsent(new TagLocation(class_7924.method_60916(registry.method_46765()), tag), identifier -> new HashSet<>())
                .addAll(values.stream().map(entryMaker).toList());
    }

    public static void injectRaw(class_2378<?> registry, class_2960 tag, Function<class_2960, class_3497> entryMaker, class_2960... values) {
        injectRaw(registry, tag, entryMaker, Arrays.asList(values));
    }

    // -------

    /**
     * Inject the given values into the given tag, obtaining
     * their identifiers from the given registry
     *
     * @param registry The registry the target tag is for
     * @param tag      The identifier of the tag to inject into
     * @param values   The values to inject
     * @param <T>      The type of the target registry
     */
    public static <T> void inject(class_2378<T> registry, class_2960 tag, Collection<T> values) {
        injectDirectReference(registry, tag, values.stream().map(registry::method_10221).toList());
    }

    @SafeVarargs
    public static <T> void inject(class_2378<T> registry, class_2960 tag, T... values) {
        inject(registry, tag, Arrays.asList(values));
    }

    // -------

    /**
     * Inject the given identifiers into the given tag
     *
     * @param registry The registry the target tag is for
     * @param tag      The identifier of the tag to inject into
     * @param values   The values to inject
     */
    public static void injectDirectReference(class_2378<?> registry, class_2960 tag, Collection<class_2960> values) {
        injectRaw(registry, tag, class_3497::method_43937, values);
    }

    public static void injectDirectReference(class_2378<?> registry, class_2960 tag, class_2960... values) {
        injectDirectReference(registry, tag, Arrays.asList(values));
    }

    // -------

    /**
     * Inject the given tags into the given tag,
     * effectively nesting them. This is equivalent to
     * prefixing an entry in the tag JSON's {@code values} array
     * with a {@code #}
     *
     * @param registry The registry the target tag is for
     * @param tag      The identifier of the tag to inject into
     * @param values   The values to inject
     */
    public static void injectTagReference(class_2378<?> registry, class_2960 tag, Collection<class_2960> values) {
        injectRaw(registry, tag, class_3497::method_43945, values);
    }

    public static void injectTagReference(class_2378<?> registry, class_2960 tag, class_2960... values) {
        injectTagReference(registry, tag, Arrays.asList(values));
    }

    public record TagLocation(String type, class_2960 tagId) {}

}
