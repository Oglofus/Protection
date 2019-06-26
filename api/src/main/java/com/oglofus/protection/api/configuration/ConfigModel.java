package com.oglofus.protection.api.configuration;

import com.oglofus.protection.api.value.Value;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 06/04/2018.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ConfigModel<D, T extends Value<D>> extends Iterable<T> {
    /**
     * Gets values.
     *
     * @return the values
     */
    Collection<T> getValues();

    /**
     * Gets value.
     *
     * @param id the id
     * @return the value
     */
    Optional<T> getValue(String id);

    /**
     * Append value t.
     *
     * @param value the value
     * @return the t
     */
    T appendValue(T value);

    /**
     * Remove value t.
     *
     * @param id the id
     * @return the t
     */
    T removeValue(String id);

    /**
     * Gets default.
     *
     * @return the default
     */
    default Optional<T> getDefault() {
        return getValues().parallelStream()
                .filter(Value::isDefault)
                .findFirst();
    }

    /**
     * Gets defaults.
     *
     * @return the defaults
     */
    default Collection<T> getDefaults() {
        return getValues().parallelStream()
                .filter(value -> value.isDefault() || (!value.getPermission().isPresent()))
                .collect(Collectors.toList());
    }

    /**
     * By permission collection.
     *
     * @param sender the sender
     * @return the collection
     */
    default Collection<T> byPermission(CommandSender sender) {
        return getValues().parallelStream()
                .filter(value -> value.getPermission().isPresent()
                        && (sender.hasPermission(value.getPermission().get())
                        || sender.hasPermission("oglofus.protection.bypass.limit")))
                .collect(Collectors.toList());
    }

    /**
     * Returns an iterator over elements of type {@code IntegerValue}.
     *
     * @return an Iterator.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    default Iterator<T> iterator() {
        return getValues().iterator();
    }

    /**
     * Reload.
     *
     * @param section the section
     */
    @SuppressWarnings("unchecked")
    default void reload(ConfigurationSection section) {
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            ConfigurationSection sec = section.getConfigurationSection(key);

            if (sec == null) {
                Bukkit.getLogger().warning("Something got wrong with the configuration.");

                continue;
            }

            if (!sec.isSet("value")) {
                continue;
            }

            Optional<T> valueOptional = this.getValue(key);

            if (valueOptional.isPresent()) {
                T value = valueOptional.get();
                value.setDefault(sec.isBoolean("default") && sec.getBoolean("default"));
                value.setPermission(sec.isString("permission") ? sec.getString("permission") : null);
                value.setValue((D) sec.get("value"));
            } else {
                appendValue((T) Value.<D>builder(key)
                        .def(sec.isBoolean("default") && sec.getBoolean("default"))
                        .permission(sec.isString("permission") ? sec.getString("permission") : null)
                        .value((D) sec.get("value"))
                        .build());
            }
        }

        for (Value value : this) {
            if (!keys.contains(value.getName())) {
                removeValue(value.getName());
            }
        }
    }
}
