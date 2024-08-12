package com.knguyendev.api.services;

import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;

import java.util.List;

public interface UserRelationshipService {

    /*
     * The authenticated user id can be gotten from an AuthUtil and also the
     * enumeration will definitely be handled by the function itself, rather than
     * having that enumeration being passed in. As a result the front-end wouldn't
     * have to worry about an enumeration that the backend has defined
     *
     */

    /**
     * Handles creating a friend request. This attempts to create the relationship
     * @param targetUserId ID of the user that the authenticated user is sending a friend request to.
     * @return DTO of the newly saved friend request
     */
    UserRelationshipDTO requestFriendship(Long targetUserId);

    /**
     * Handles the process of having the authenticated user accept a friend request
     * from another user.
     *
     * @param targetUserId The user ID of the other user in the UserRelationship that sent the friend request to the authenticated user.
     * @return A DTO representing the UserRelationshipEntity that was either accepted .
     */
     UserRelationshipDTO acceptFriendRequest(Long targetUserId);


    /**
     * Handles the process of deleting an existing friend request. This is when the authenticated user is declining
     * a friend request that they received from another user, or revoking a friend request that they sent to another other.
     * @param targetUserId The id of the other user in the UserRelationship that represents the friend request.
     * @return A DTO representing the deleted UserRelationship
     */
    UserRelationshipDTO deleteFriendRequest(Long targetUserId);

    /**
     * Handles the process of deleting an existing friendship. This represents the scenario of simply removing someone from
     * your 'friends' list, which would be both of you not friends anymore.
     * @param targetUserId ID of the other user that's referenced in the friendship.
     * @return A DTO representing the deleted friendship.
     */
     UserRelationshipDTO deleteFriendship(Long targetUserId);

    /**
     * Handles the process of the authenticated user blocking another user. It should be noted that this could result
     * in a relationship where the authenticated user has blocked the otherUser, or block users have each other blocked.
     * @param targetUserId The id of the user that the authenticated user is blocking.
     * @return DTO representing the resulting blocked relationship.
     */
    UserRelationshipDTO blockUser(Long targetUserId);

    /**
     * Handles the process of letting the authenticated user unblock a user that they already have blocked
     * @param targetUserId The id of the user that the authenticated user has blocked.
     * @return DTO representing the newly updated UserRelationship
     */
    UserRelationshipDTO unblockUser(Long targetUserId);

    List<UserRelationshipDTO> getAuthUserRelationships();
}
