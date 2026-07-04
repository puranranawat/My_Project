package com.devsecops.ems.util;

/**
 * Application-wide constants.
 *
 * <p>This class is not instantiable and contains only static final fields
 * used across the application for pagination defaults, sorting, and
 * employee status values.</p>
 */
public final class ApplicationConstants {

    // ---------------------------------------------------------------
    // Pagination Defaults
    // ---------------------------------------------------------------
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";

    // ---------------------------------------------------------------
    // Sorting Defaults
    // ---------------------------------------------------------------
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // ---------------------------------------------------------------
    // Employee Status Constants
    // ---------------------------------------------------------------
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    private ApplicationConstants() {
        throw new UnsupportedOperationException("Utility class — cannot be instantiated");
    }
}
