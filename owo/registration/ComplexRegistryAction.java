package io.wispforest.owo.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.class_2378;
import net.minecraft.class_2960;

/**
 * An action to be executed by a {@link RegistryHelper} if and only if
 * all of it's required entries are present in that helper's registry
 *
 * @see ComplexRegistryAction.Builder#create(Runnable)
 */
public class ComplexRegistryAction {

    private final List<class_2960> predicates;
    private final Runnable action;

    protected ComplexRegistryAction(List<class_2960> predicates, Runnable action) {
        this.predicates = predicates;
        this.action = action;
    }

    protected <T> boolean preCheck(class_2378<T> registry) {
        predicates.removeIf(registry::method_10250);
        if (!predicates.isEmpty()) return false;

        action.run();
        return true;
    }

    protected boolean update(class_2960 id, Collection<Runnable> actionList) {
        predicates.remove(id);
        if (!predicates.isEmpty()) return false;

        actionList.add(action);
        return true;
    }

    public static class Builder {

        private final Runnable action;
        private final List<class_2960> predicates;

        private Builder(Runnable action) {
            this.action = action;
            this.predicates = new ArrayList<>();
        }

        /**
         * Creates a new builder to link the provided action
         * to a list of identifiers
         *
         * @param action The action to run once all identifiers are found in the targeted registry
         * @see #entry(class_2960)
         * @see #entries(Collection)
         */
        public static Builder create(Runnable action) {
            return new Builder(action);
        }

        public Builder entry(class_2960 id) {
            this.predicates.add(id);
            return this;
        }

        public Builder entries(Collection<class_2960> ids) {
            this.predicates.addAll(ids);
            return this;
        }

        /**
         * Creates a registry action that can get run by a {@link RegistryHelper} once all the entries
         * added via this builder are found in the target registry
         *
         * @return The built action
         */
        public ComplexRegistryAction build() {
            if (predicates.isEmpty()) throw new IllegalStateException("Predicate list must not be empty");
            return new ComplexRegistryAction(predicates, action);
        }

    }

}
