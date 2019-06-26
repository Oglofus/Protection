package com.oglofus.protection.api.manager;

import com.oglofus.protection.api.reguest.Request;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

/**
 * This file is part of Oglofus Protection project.
 * Created by Nikolaos Grammatikos <nikosgram@oglofus.com> on 03/06/2017.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface RequestManager extends Iterable<Request> {

    /**
     * Gets requests.
     *
     * @return the requests
     */
    Collection<Request> getRequests();

    /**
     * Gets request.
     *
     * @param uuid the uuid
     * @return the request
     */
    Optional<Request> getRequest(UUID uuid);

    /**
     * Append request request.
     *
     * @param request the request
     * @return the request
     */
    Request appendRequest(Request request);

    /**
     * Remove value request.
     *
     * @param uuid the uuid
     * @return the request
     */
    Request removeValue(UUID uuid);

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    default Iterator<Request> iterator() {
        return getRequests().iterator();
    }
}
