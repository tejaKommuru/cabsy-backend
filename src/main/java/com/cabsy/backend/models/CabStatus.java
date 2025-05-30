package com.cabsy.backend.models;
public enum CabStatus {
    IN_SERVICE,          // Ready for rides
    UNDER_MAINTENANCE,   // Temporarily out of service for repairs/maintenance
    OUT_OF_SERVICE,      // Permanently out of service or disabled
    PENDING_APPROVAL     // Newly registered cab awaiting verification
}

