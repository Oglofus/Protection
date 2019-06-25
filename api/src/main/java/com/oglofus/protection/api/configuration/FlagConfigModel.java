package com.oglofus.protection.api.configuration;

import com.oglofus.protection.api.configuration.region.FlagEntity;
import com.oglofus.protection.api.value.Value;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 10/04/2018.
 */
@SuppressWarnings("unused")
public interface FlagConfigModel extends ConfigModel<String, FlagEntity> {

    /**
     * By group collection.
     *
     * @param group the group
     * @return the collection
     */
    default Collection<FlagEntity> byGroup(String group) {
        return getValues().stream().filter(g -> {
            Optional<String> groupOptional = g.getGroup();

            return groupOptional.isPresent() && groupOptional.get().equalsIgnoreCase(group);
        }).collect(Collectors.toList());
    }

    /**
     * Reload.
     *
     * @param section the section
     */
    default void reload(ConfigurationSection section) {
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            ConfigurationSection sec = section.getConfigurationSection(key);

            if (sec == null) {
                Bukkit.getLogger().warning("Something got wrong with the flag configuration.");

                continue;
            }

            if (!sec.isString("value")) {
                Bukkit.getLogger().warning("Config invalid value on " + sec.getCurrentPath());
                Bukkit.getLogger().warning("FlagEntities accepts only String values.");

                continue;
            }

            Optional<FlagEntity> valueOptional = this.getValue(key);

            if (valueOptional.isPresent()) {
                FlagEntity value = valueOptional.get();
                value.setDefault(sec.isBoolean("default") && sec.getBoolean("default"));
                value.setPermission(sec.isString("permission") ? sec.getString("permission") : null);
                value.setGroup(sec.isString("group") ? sec.getString("group") : null);
                value.setValue(sec.getString("value"));
            } else {
                appendValue(FlagEntity.builder(key)
                        .def(sec.isBoolean("default") && sec.getBoolean("default"))
                        .permission(sec.isString("permission") ? sec.getString("permission") : null)
                        .group(sec.isString("group") ? sec.getString("group") : null)
                        .value(sec.getString("value"))
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
