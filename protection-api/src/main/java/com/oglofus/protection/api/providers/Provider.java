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

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The interface Provider.
 *
 * @param <T> the type parameter
 * @param <D> the type parameter
 */
public interface Provider<T, D> extends Iterable<D> {
    /**
     * Keys collection.
     *
     * @return the collection
     */
    Collection<T> keys();

    /**
     * Values collection.
     *
     * @return the collection
     */
    Collection<D> values();

    /**
     * Contains boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean contains(T key);

    /**
     * Get optional.
     *
     * @param key the key
     * @return the optional
     */
    Optional<D> get(T key);

    /**
     * Size int.
     *
     * @return the int
     */
    int size();

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    boolean isEmpty();

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

    /**
     * Stream stream.
     *
     * @return the stream
     */
    default Stream<D> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Parallel stream stream.
     *
     * @return the stream
     */
    default Stream<D> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
