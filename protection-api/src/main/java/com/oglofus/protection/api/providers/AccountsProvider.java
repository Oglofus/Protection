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

import java.util.Optional;
import java.util.UUID;

public interface AccountsProvider extends Provider<UUID, Account> {
    default boolean contains(String name) {
        return stream().anyMatch(account -> account.getName().equals(name));
    }

    default Optional<Account> get(String name) {
        return stream().filter(account -> account.getName().equals(name)).findFirst();
    }
}
