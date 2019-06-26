package com.oglofus.protection.api.configuration.region;

import com.oglofus.protection.api.value.Value;
import org.apache.commons.lang.Validate;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * The interface Flag entity.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface FlagEntity extends Value<String> {

    /**
     * Builder builder.
     *
     * @param name the name
     * @return the builder
     */
    static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * Gets group.
     *
     * @return the group
     */
    Optional<String> getGroup();

    /**
     * Sets group.
     *
     * @param group the group
     * @return the group
     */
    String setGroup(String group);

    /**
     * The type Builder.
     */
    class Builder {
        /**
         * The Name.
         */
        private final String name;
        /**
         * The Permission.
         */
        private String permission;
        /**
         * The Group.
         */
        private String group;
        /**
         * The Value.
         */
        private String value;
        /**
         * The Def.
         */
        private boolean def;

        /**
         * Instantiates a new Builder.
         *
         * @param name the name
         */
        Builder(String name) {
            this.name = name;
            this.def = false;
        }

        /**
         * Permission builder.
         *
         * @param permission the permission
         * @return the builder
         */
        public Builder permission(@Nullable String permission) {
            this.permission = permission;

            return this;
        }

        /**
         * Group builder.
         *
         * @param group the group
         * @return the builder
         */
        public Builder group(String group) {
            this.group = group;

            return this;
        }

        /**
         * Def builder.
         *
         * @param def the def
         * @return the builder
         */
        public Builder def(boolean def) {
            this.def = def;

            return this;
        }

        /**
         * Value builder.
         *
         * @param value the value
         * @return the builder
         */
        public Builder value(String value) {
            this.value = value;

            return this;
        }

        /**
         * Build flag entity.
         *
         * @return the flag entity
         */
        public FlagEntity build() {
            Validate.notNull(this.name);
            Validate.notNull(this.value);

            return new FlagEntity() {
                @Override
                public Optional<String> getGroup() {
                    return Optional.ofNullable(group);
                }

                @Override
                public String setGroup(String g) {
                    return group = g;
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public Optional<String> getPermission() {
                    return Optional.ofNullable(permission);
                }

                @Override
                public String setPermission(String perm) {
                    return permission = perm;
                }

                @Override
                public boolean isDefault() {
                    return def;
                }

                @Override
                public boolean setDefault(boolean d) {
                    return def = d;
                }

                @Override
                public String getValue() {
                    return value;
                }

                @Override
                public String setValue(String v) {
                    return value = v;
                }
            };
        }
    }
}
