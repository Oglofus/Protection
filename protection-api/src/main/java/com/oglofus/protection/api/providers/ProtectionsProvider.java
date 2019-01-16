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

import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.position.Position;
import com.oglofus.protection.api.protection.Category;
import com.oglofus.protection.api.protection.Protection;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ProtectionsProvider extends Provider<UUID, Protection> {
    Collection<Protection> getProtections();

    default Optional<Protection> getProtectionOn(Position position) {
        return Optional.empty();
    }

    default Collection<Protection> getProtectionsByAccount(Account account) {
        return stream()
                .filter(protection -> protection.getCategory(account) != Category.Uncategorizable)
                .collect(Collectors.toList());
    }

    default Collection<Protection> getProtectionsByAccount(Account account, Category category) {
        return stream()
                .filter(protection -> protection.getCategory(account) == category)
                .collect(Collectors.toList());
    }

    default Collection<Protection> getProtectionsByOwner(Account account) {
        return getProtectionsByAccount(account, Category.Owner);
    }

    default Collection<Protection> getProtectionsByMember(Account account) {
        return getProtectionsByAccount(account, Category.Member);
    }

    default Collection<Protection> getProtectionsByUncategorizable(Account account) {
        return getProtectionsByAccount(account, Category.Uncategorizable);
    }
}
