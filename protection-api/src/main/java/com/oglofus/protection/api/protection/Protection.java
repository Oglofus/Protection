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

package com.oglofus.protection.api.protection;

import com.oglofus.protection.OglofusProtection;
import com.oglofus.protection.api.Identifiable;
import com.oglofus.protection.api.Nameable;
import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.region.Region3d;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Protection extends Identifiable, Nameable, Iterable<Account> {
    Region3d getRegion();

    MemberCategory getCategory(Account account);

    Collection<Account> getAccounts();

    default Collection<Account> getAccounts(MemberCategory memberCategory) {
        return OglofusProtection.getPlatform().getAccounts().stream()
                .filter(account -> account.getCategory(this) == memberCategory)
                .collect(Collectors.toList());
    }

    default Collection<Account> getOwners() {
        return getAccounts(MemberCategory.Owner);
    }

    default Collection<Account> getMembers() {
        return getAccounts(MemberCategory.Member);
    }

    default Collection<Account> getUncategorizables() {
        return getAccounts(MemberCategory.Uncategorizable);
    }

    @Override
    default Iterator<Account> iterator() {
        return getAccounts().iterator();
    }

    @Override
    default void forEach(Consumer<? super Account> action) {
        getAccounts().forEach(action);
    }

    @Override
    default Spliterator<Account> spliterator() {
        return getAccounts().spliterator();
    }

    default Stream<Account> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<Account> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
