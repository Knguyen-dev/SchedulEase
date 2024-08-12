package com.knguyendev.api.enumeration;

/**
 * + UserRelationShipsStatus Enumerations:
 * - PENDING_FIRST_SECOND: The first user sent a request, and is waiting for the second to respond.
 * - PENDING_SECOND_FIRST: The second user sent a request, and is waiting for the first user respond.
 * - FRIENDS: The first and second users are currently friends with each other.
 * - BLOCK_FIRST_SECOND: The first user has blocked the second user.
 * - BLOCK_SECOND_FIRST: The second user has blocked the first user.
 * - BLOCK_BOTH: Both users have blocked each other.
 */
public enum UserRelationshipStatus {
    PENDING_FIRST_SECOND,
    PENDING_SECOND_FIRST,
    FRIENDS,
    BLOCK_FIRST_SECOND,
    BLOCK_SECOND_FIRST,
    BLOCK_BOTH
}
