/*
 * Copyright 2019 Nikolaos Grammatikos <nikosgram@oglofus.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oglofus.protection.api.providers;

import com.oglofus.protection.api.Nameable;
import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.point.Point3d;
import com.oglofus.protection.api.protection.MemberCategory;
import com.oglofus.protection.api.protection.Protection;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ProtectionsProvider extends OglofusProvider<UUID, Protection> {
    default Optional<Protection> getProtectionOn(Point3d point3d) {
        return Optional.empty();
    }

    default Collection<Protection> getProtectionsByAccount(Account account) {
        return stream()
                .filter(protection -> protection.getCategory(account) != MemberCategory.Uncategorizable)
                .collect(Collectors.toList());
    }

    default Collection<Protection> getProtectionsByAccount(Account account, MemberCategory memberCategory) {
        return stream()
                .filter(protection -> protection.getCategory(account) == memberCategory)
                .collect(Collectors.toList());
    }

    default Collection<Protection> getProtectionsByOwner(Account account) {
        return getProtectionsByAccount(account, MemberCategory.Owner);
    }

    default Collection<Protection> getProtectionsByMember(Account account) {
        return getProtectionsByAccount(account, MemberCategory.Member);
    }

    default Collection<Protection> getProtectionsByUncategorizable(Account account) {
        return getProtectionsByAccount(account, MemberCategory.Uncategorizable);
    }

    @Nullable
    @Override
    default Protection get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
        String name = arguments.next();

        return get(name).orElseThrow(() -> new ArgumentParseException(
                "No protection area by the name of '" + name + "' is known!"
        ));
    }

    @Override
    default List<String> getSuggestions(String prefix) {
        return stream().filter(nameable -> nameable.getName()
                .startsWith(prefix))
                .map(Nameable::getName)
                .collect(Collectors.toList());
    }
}
