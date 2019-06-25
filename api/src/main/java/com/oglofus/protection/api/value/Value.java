package com.oglofus.protection.api.value;

import org.apache.commons.lang.Validate;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This file is part of protection-bukkit project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 28/06/2017.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Value<T> {

    /**
     * Builder builder.
     *
     * @param <T>  the type parameter
     * @param name the name
     * @return the builder
     */
    static <T> Builder<T> builder(String name) {
        return new Builder<>(name);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets permission.
     *
     * @return the permission
     */
    Optional<String> getPermission();

    /**
     * Sets permission.
     *
     * @param permission the permission
     * @return the permission
     */
    String setPermission(String permission);

    /**
     * Is default boolean.
     *
     * @return the boolean
     */
    boolean isDefault();

    /**
     * Sets default.
     *
     * @param def the def
     * @return the default
     */
    boolean setDefault(boolean def);

    /**
     * Gets value.
     *
     * @return the value
     */
    T getValue();

    /**
     * Sets value.
     *
     * @param value the value
     * @return the value
     */
    T setValue(T value);

    /**
     * The type Builder.
     *
     * @param <T> the type parameter
     */
    class Builder<T> {
        /**
         * The Name.
         */
        private final String name;
        /**
         * The Permission.
         */
        private String permission;
        /**
         * The Def.
         */
        private boolean def;
        /**
         * The Value.
         */
        private T value;

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
        public Builder<T> permission(@Nullable String permission) {
            this.permission = permission;

            return this;
        }

        /**
         * Def builder.
         *
         * @param def the def
         * @return the builder
         */
        public Builder<T> def(boolean def) {
            this.def = def;

            return this;
        }

        /**
         * Value builder.
         *
         * @param value the value
         * @return the builder
         */
        public Builder<T> value(T value) {
            this.value = value;

            return this;
        }

        /**
         * Build value.
         *
         * @return the value
         */
        public Value<T> build() {
            Validate.notNull(this.name);
            Validate.notNull(this.value);

            return new Value<T>() {
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
                public T getValue() {
                    return value;
                }

                @Override
                public T setValue(T v) {
                    return value = v;
                }
            };
        }
    }
}
