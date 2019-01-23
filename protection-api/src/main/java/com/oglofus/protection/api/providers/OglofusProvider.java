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
import com.oglofus.protection.api.TransformException;
import com.sk89q.intake.parametric.Provider;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The interface OglofusProvider.
 *
 * @param <T> the type parameter
 * @param <D> the type parameter
 */
public interface OglofusProvider<T, D extends Nameable> extends Iterable<D>, Provider<D> {
    Collection<T> keys();

    Collection<D> values();

    boolean contains(T key);

    Optional<D> get(T key);

    <E> Optional<D> get(E target, Class<E> eClass) throws TransformException;

    int size();

    boolean isEmpty();

    default boolean contains(String name) {
        return stream().anyMatch(account -> account.getName().equals(name));
    }

    default Optional<D> get(String name) {
        return stream().filter(account -> account.getName().equals(name)).findFirst();
    }

    @Override
    default Iterator<D> iterator() {
        return values().iterator();
    }

    @Override
    default void forEach(Consumer<? super D> action) {
        values().forEach(action);
    }

    @Override
    default Spliterator<D> spliterator() {
        return values().spliterator();
    }

    default Stream<D> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<D> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    @Override
    default boolean isProvided() {
        return false;
    }
}
