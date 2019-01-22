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

package com.oglofus.protection.api.cosmos;

import com.oglofus.protection.api.Identifiable;
import com.oglofus.protection.api.Nameable;
import com.oglofus.protection.api.Transformable;
import com.oglofus.protection.api.account.Account;
import com.oglofus.protection.api.protection.Protection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Cosmos extends Identifiable, Nameable, Transformable, Iterable<Protection> {
    Collection<Protection> getProtections();

    Collection<Account> getAccounts();

    @Override
    default Iterator<Protection> iterator() {
        return getProtections().iterator();
    }

    @Override
    default void forEach(Consumer<? super Protection> action) {
        getProtections().forEach(action);
    }

    @Override
    default Spliterator<Protection> spliterator() {
        return getProtections().spliterator();
    }

    default Stream<Protection> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<Protection> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
