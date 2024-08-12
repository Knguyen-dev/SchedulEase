package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import com.knguyendev.api.enumeration.UserRelationshipStatus;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserRelationshipMapper;
import com.knguyendev.api.repositories.UserRelationshipRepository;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.UserRelationshipService;
import com.knguyendev.api.utils.AuthUtils;
import com.knguyendev.api.utils.ServiceUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.knguyendev.api.enumeration.UserRelationshipStatus.*;

@Service
public class UserRelationshipServiceImpl implements UserRelationshipService {

    private final UserRepository userRepository;
    private final UserRelationshipRepository userRelationshipRepository;
    private final AuthUtils authUtils;
    private final UserRelationshipMapper userRelationshipMapper;
    private final ServiceUtils serviceUtils;
    public UserRelationshipServiceImpl(
            UserRepository userRepository,
            UserRelationshipRepository userRelationshipRepository,
            AuthUtils authUtils,
            UserRelationshipMapper userRelationshipMapper,
            ServiceUtils serviceUtils
            ) {
        this.userRepository = userRepository;
        this.userRelationshipRepository = userRelationshipRepository;
        this.authUtils = authUtils;
        this.userRelationshipMapper = userRelationshipMapper;
        this.serviceUtils = serviceUtils;
    }


    /**
     * Encapsulates two user IDs, ensuring that the IDs are ordered such that
     * the firstUserId is less than or equal to the secondUserId.
     *
     * NOTE: All of our service functions need to have the user IDS ordered as firstUserId and
     * secondUserId. So to reduce repet
     */
    @Getter
    private static class UserRelationshipContext {
        private final Long firstUserId;
        private final Long secondUserId;
        private final boolean authUserIsFirstUser;
        /**
         * Constructs a UserIds object with the provided IDs, ordering them such that
         * firstUserId < secondUserId.
         * NOTE: The order you pass in the ID values doesn't matter, as the returned class instance will handle the ordering
         * such that firstUserId < secondUserId.
         * @param id1 The first ID.
         * @param id2 The second ID.
         * @param authUserId The ID of the authenticated user. This would either be id1 or id2, and it's needed to determine
         *                   whether the authenticated user is the first or second user.
         */
        public UserRelationshipContext(Long id1, Long id2, Long authUserId) {
            if (id1 < id2) {
                this.firstUserId = id1;
                this.secondUserId = id2;
            } else {
                this.firstUserId = id2;
                this.secondUserId = id1;
            }
            this.authUserIsFirstUser = authUserId.equals(firstUserId);
        }
    }


    /**
     * Creates a new relationship in the UserRelationship table. It's a helper function that assumes that there doesn't
     * already exist a relationship between the users with these ID values.
     * @param firstUserId ID of the first user in the UserRelationship
     * @param secondUserId ID of the second user in the UserRelationship
     * @param status Status of the relationship
     * @return An entity representing the newly created relationship
     * @throws ServiceException An error that's thrown when the users with the specified user ID values don't exist.
     */
    private UserRelationshipEntity createRelationship(Long firstUserId, Long secondUserId, UserRelationshipStatus status) throws ServiceException {

        // Attempt to find the two users in the relationship. If there are less than 2, then we couldn't get both of the users
        List<Long> expectedIds = List.of(firstUserId, secondUserId);
        List<UserEntity> users = userRepository.findByIds(expectedIds);

        // If one of the user's wasn't found, we can't create a UserRelationship, so throw an error for the first id that didn't correlate to a user
        if (users.size() < 2) {
            List<Long> foundIds = users.stream()
                    .map(UserEntity::getId)
                    .toList();
            for (Long expectedId : expectedIds) {
                if (!foundIds.contains(expectedId)) {
                    throw new ServiceException("User with ID '" + expectedId + "' wasn't found!", HttpStatus.NOT_FOUND);
                }
            }
        }

        // At this both users were found, so assign the firstUser and secondUser variables; both are guaranteed to be updated.
        UserEntity firstUser = null;
        UserEntity secondUser = null;
        for (UserEntity user: users) {
            if (user.getId().equals(firstUserId)) {
                firstUser = user;
            } else {
                secondUser = user;
            }
        }

        UserRelationshipEntity relationshipEntity = UserRelationshipEntity.builder()
                .firstUser(firstUser)
                .secondUser(secondUser)
                .status(status)
                .build();
        return userRelationshipRepository.save(relationshipEntity);
    }

    @Override
    public UserRelationshipDTO requestFriendship(Long targetUserId) {
        Long authUserId = authUtils.getAuthUserId();
        UserRelationshipContext relationshipContext = new UserRelationshipContext(authUserId, targetUserId, authUserId);

        Optional<UserRelationshipEntity> relationshipResult = userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                relationshipContext.getFirstUserId(),
                relationshipContext.getSecondUserId()
        );

        if (relationshipResult.isPresent()) {
            // Can't create a friend request so throw an error
            UserRelationshipEntity relationship = relationshipResult.get();
            String otherUsername = relationshipContext.isAuthUserIsFirstUser()
                    ? relationship.getSecondUser().getUsername()
                    : relationship.getFirstUser().getUsername();
            String errorMessage;

            // Possible error messages
            String pendingRequestMessage = "A pending friend request has already been sent to '" + otherUsername + "'!";
            String receivedPendingRequestMessage = "You already have a pending friend request from '" + otherUsername + "'! Please accept or decline that request!";
            String alreadyFriendsMessage = "Can't send a friend request since you are already friends with this user!";
            String authBlockedTargetMessage = "You have blocked '" + otherUsername + "'. Unblock them to send a friend request!";
            String targetBlockedAuthMessage = "'" + otherUsername + "' has blocked you. They must unblock you before you can send a friend request.";

            switch (relationship.getStatus()) {
                case PENDING_FIRST_SECOND:
                    if (relationshipContext.isAuthUserIsFirstUser()) {
                        errorMessage = pendingRequestMessage;
                    } else {
                        errorMessage = receivedPendingRequestMessage;
                    }
                    break;
                case PENDING_SECOND_FIRST:
                    if (relationshipContext.isAuthUserIsFirstUser()) {
                        errorMessage = receivedPendingRequestMessage;
                    } else {
                        errorMessage = pendingRequestMessage;
                    }
                    break;
                case FRIENDS:
                    errorMessage = alreadyFriendsMessage;
                    break;
                case BLOCK_FIRST_SECOND:
                    if (relationshipContext.isAuthUserIsFirstUser()) {
                        errorMessage = authBlockedTargetMessage;
                    } else {
                        errorMessage = targetBlockedAuthMessage;
                    }
                    break;
                case BLOCK_SECOND_FIRST:
                    if (relationshipContext.isAuthUserIsFirstUser()) {
                        errorMessage = targetBlockedAuthMessage;
                    } else {
                        errorMessage = authBlockedTargetMessage;
                    }
                    break;
                case BLOCK_BOTH:
                    // If both users have blocked each other, just tell the auth. user that they have the other one blocked
                    errorMessage = authBlockedTargetMessage;
                    break;
                default:
                    errorMessage = "Unrecognized relationship status: " + relationship.getStatus();
                    break;
            }
            throw new ServiceException(errorMessage, HttpStatus.BAD_REQUEST);
        }

        /*
         * + At this point no relationship exists between the users, create a UserRelationship to store a friend request
         * from the authenticated user to the other user.
         *
         * Remember the auth. user is the one sending the request. So if they are the first user, then
         * we would do PENDING_FIRST_SECOND, to indicate the first user has sent a request to the second user, and
         * are awaiting the latter's response. Conversely, if they are the secondUser, then PENDING_SECOND_FIRST.
         */
        UserRelationshipEntity relationship = createRelationship(
                relationshipContext.getFirstUserId(),
                relationshipContext.getSecondUserId(),
                relationshipContext.isAuthUserIsFirstUser() ? PENDING_FIRST_SECOND : PENDING_SECOND_FIRST
        );
        return userRelationshipMapper.toDTO(relationship);
    }

    @Override
    public UserRelationshipDTO acceptFriendRequest(Long targetUserId) {
        // Get authenticated user, set it up so that firstUserId < secondUserId
        Long authUserId = authUtils.getAuthUserId();
        UserRelationshipContext userRelationshipContext = new UserRelationshipContext(authUserId, targetUserId, authUserId);

        String notFoundErrMessage = "A pending friend request wasn't found!";
        String invalidBlockErrMessage = "You cannot accept this friend request since you're the one who sent it!";


        // Check if a UserRelationship even exists
        Optional<UserRelationshipEntity> relationshipResult = userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                userRelationshipContext.getFirstUserId(),
                userRelationshipContext.getSecondUserId()
        );

        if (relationshipResult.isEmpty()) {
            throw new ServiceException(notFoundErrMessage, HttpStatus.NOT_FOUND);
        }
        UserRelationshipEntity relationship = relationshipResult.get();

        // Given that the UserRelationship does exist, is it in a pending state? If not then it doesn't count as a friend request
        if (relationship.getStatus() != PENDING_FIRST_SECOND && relationship.getStatus() != PENDING_SECOND_FIRST) {
            throw new ServiceException(notFoundErrMessage, HttpStatus.NOT_FOUND);
        }

        /*
         * + When can the user accept the friend request:
         * The status of the relationship must be pending, which is either PENDING_FIRST_SECOND or PENDING_SECOND_FIRST.
         * Any other statuses aren't indicative of a pending friend request, so we'll throw an error.
         *
         * In the case where the relationship is pending, in order for the auth. user to be able to accept the friend request,
         * they must be the person receiving the friend request.
         *
         * If status is PENDING_FIRST_SECOND, and authUserIsFirstUser, that means that the auth. user sent the friend request,
         * and now they're trying to accept the same request they sent. So reject this process. Conversely, if the status
         * is PENDING_SECOND_FIRST and !authUserIsFirstUser, it means the same thing.
         */
        switch (relationship.getStatus()) {
            case PENDING_FIRST_SECOND:
                // if the authenticated user sent the friend request, then they can't accept it on behalf of the other user.
                if (userRelationshipContext.isAuthUserIsFirstUser()) {
                    throw new ServiceException(invalidBlockErrMessage, HttpStatus.FORBIDDEN);
                }
                relationship.setStatus(FRIENDS);
                break;
            case PENDING_SECOND_FIRST:
                if (!userRelationshipContext.isAuthUserIsFirstUser()) {
                    throw new ServiceException(invalidBlockErrMessage, HttpStatus.FORBIDDEN);
                }
                relationship.setStatus(FRIENDS);
                break;
            default:
                throw new RuntimeException("Unrecognized UserRelationship status: " + relationship.getStatus());
        }

        return userRelationshipMapper.toDTO(userRelationshipRepository.save(relationship));
    }

    @Override
    public UserRelationshipDTO deleteFriendRequest(Long targetUserId) {
        // Get authenticated user, set it up so that firstUserId < secondUserId
        Long authUserId = authUtils.getAuthUserId();
        UserRelationshipContext userRelationshipContext = new UserRelationshipContext(authUserId, targetUserId, authUserId);
        String errMessage = "A pending friend request wasn't found!";

        // Check if a UserRelationship even exists
        Optional<UserRelationshipEntity> relationshipResult = userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                userRelationshipContext.getFirstUserId(),
                userRelationshipContext.getSecondUserId()
        );
        if (relationshipResult.isEmpty()) {
            throw new ServiceException(errMessage, HttpStatus.NOT_FOUND);
        }

        // Given that the UserRelationship does exist, is it in a pending state? If not then it doesn't count as a friend request
        UserRelationshipEntity relationship = relationshipResult.get();
        if (relationship.getStatus() != PENDING_FIRST_SECOND && relationship.getStatus() != PENDING_SECOND_FIRST) {
            throw new ServiceException(errMessage, HttpStatus.NOT_FOUND);
        }

        /*
         * At this point we know that there's a pending friend request between the authenticated user and another user.
         * Regardless of whether the authenticated user sent the friend request or received it, they should be able to
         * delete it. This is because there are two valid scenarios:
         *
         * 1. Authenticated user received the friend request, they can delete it to indicate that they declined it.
         * 2. Authenticated user sent the friend request, so they can delete it as it's the idea of revoking a friend request.
         *
         * - Summary: At this point we know a pending friend request exists, so the authenticated user can delete it.
         */
        userRelationshipRepository.delete(relationship);
        return userRelationshipMapper.toDTO(relationship);
    }

    @Override
    public UserRelationshipDTO deleteFriendship(Long targetUserId) {
        // Get authenticated user, set it up so that firstUserId < secondUserId
        Long authUserId = authUtils.getAuthUserId();
        UserRelationshipContext userRelationshipContext = new UserRelationshipContext(authUserId, targetUserId, authUserId);
        String errMessage = "Friendship being deleted wasn't found!";

        // Check if a UserRelationship even exists
        Optional<UserRelationshipEntity> relationshipResult = userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                userRelationshipContext.getFirstUserId(),
                userRelationshipContext.getSecondUserId()
        );
        if (relationshipResult.isEmpty()) {
            throw new ServiceException(errMessage, HttpStatus.NOT_FOUND);
        }

        // Given the UserRelationship exist, if it doesn't have the 'FRIENDS' status it isn't a friendship
        UserRelationshipEntity relationship = relationshipResult.get();
        if (relationship.getStatus() != FRIENDS) {
            throw new ServiceException(errMessage, HttpStatus.NOT_FOUND);
        }

        /*
         * At this point, a UserRelationship was found, and it was a friendship. Regardless of whether
         * the authenticated user is the first or secondUser, they should be able to delete the friendship. This is the idea
         * that both users are able to delete the friendship between them.
         */
        userRelationshipRepository.delete(relationship);
        return userRelationshipMapper.toDTO(relationship);
    }

    @Override
    public UserRelationshipDTO blockUser(Long targetUserId) {
        Long authUserId = authUtils.getAuthUserId();
        UserRelationshipContext userRelationshipContext = new UserRelationshipContext(authUserId, targetUserId, authUserId);

        /*
         * - Handling the existence of user relationships:
         *
         * 1. If a relationship doesn't exist, so we can simply just create a new UserRelationship
         * between the two users which is either BLOCK_FIRST_SECOND or BLOCK_SECOND_FIRST. We know it wouldn't be
         * BLOCK_BOTH because we would have found a UserRelationship if that was the case.
         *
         * 2. Else, a relationship already exists, we can use that existing relationship and update the status
         * to blocked. This is like the idea of already being friends with a person, but then blocking them, or
         * the idea of when a friend request is pending, but then one of the users block the other. Regardless of the status of
         * the relationship, it will now be overridden with a block state.
         * In this case the blocked state could be BLOCK_FIRST_SECOND, or BLOCK_BOTH. You still need to make sure to
         * see if the user is even allowed to block the other user.
         */
        Optional<UserRelationshipEntity> relationshipResult = userRelationshipRepository
                .findByFirstUserIdAndSecondUserId(userRelationshipContext.getFirstUserId(), userRelationshipContext.getSecondUserId());
        if (relationshipResult.isEmpty()) {
            UserRelationshipEntity relationship = createRelationship(
                    userRelationshipContext.getFirstUserId(),
                    userRelationshipContext.getSecondUserId(),
                    userRelationshipContext.isAuthUserIsFirstUser() ? BLOCK_FIRST_SECOND : BLOCK_SECOND_FIRST
            );
            return userRelationshipMapper.toDTO(relationship);
        }
        UserRelationshipEntity relationship = relationshipResult.get();

        /*
         * + Situations where the auth. user can block the other user:
         * 1. When there is no relationship between the users. If there's no relationship between the users, the auth. user
         * should still be able to block the other user. This involves creating a UserRelationship and indicating the auth. user
         * has blocked the other user in the status field. So if the auth. user is the first user do 'BLOCK_FIRST_SECOND' to indicate
         * that they have blocked the other user, and conversely if they're the second user then do 'BLOCK_SECOND_FIRST'.
         *
         * 2. When the relationship has a non-blocked status. So it can be one of the pending status or a 'friends' status.
         * Regardless, the auth. user should be able to block the other user, and this would indicate that the auth. user
         * has blocked the other user in the status field.
         *
         * 3. When the relationship has a blocked status, we have to check if the auth. user has already blocked the other
         * user. If that's the case, then they can't 'block' them again, so we'll cancel the request. Let's look at the scenarios:
         *
         *      1. BLOCK_FIRST_SECOND: Throw an error when the authenticated user is the first user since it just means the auth. user is trying to block
         *                             someone that they already have blocked. However, if the auth. user is the second user, this just represents
         *                             a case when the other user has the auth. user blocked, and now the auth. user is going to block the other user.
         *                             This leads to a case where they've blocked each other, so we'll do BLOCK_BOTH
         *      2. BLOCK_SECOND_FIRST: Throw an error when the authenticated user is the second user since it means they're trying to block the other user
         *                             when they already had them blocked in the first place.
         *      3. BLOCK_BOTH: Throw an error when this happens because it would mean they both have each other blocked, so having
         *                     the auth. user try to block again would be redundant and not make sense.
         */
        String errMessage = "You already have this user blocked!";
        switch (relationship.getStatus()) {
            case PENDING_FIRST_SECOND:
            case PENDING_SECOND_FIRST:
            case FRIENDS:
                relationship.setStatus(userRelationshipContext.isAuthUserIsFirstUser() ? BLOCK_FIRST_SECOND : BLOCK_SECOND_FIRST);
                break;
            case BLOCK_FIRST_SECOND:
                if (userRelationshipContext.isAuthUserIsFirstUser()) {
                    throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
                }
                relationship.setStatus(BLOCK_BOTH);
                break;
            case BLOCK_SECOND_FIRST:
                if (!userRelationshipContext.isAuthUserIsFirstUser()) {
                    throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
                }
                relationship.setStatus(BLOCK_BOTH);
                break;
            case BLOCK_BOTH:
                throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
            default:
                throw new RuntimeException("Unrecognized UserRelationship status: " + relationship.getStatus());
        }
        return userRelationshipMapper.toDTO(userRelationshipRepository.save(relationship));
    }

    @Override
    public UserRelationshipDTO unblockUser(Long targetUserId) {
        Long authUserId = authUtils.getAuthUserId();
        UserRelationshipContext userRelationshipContext = new UserRelationshipContext(authUserId, targetUserId, authUserId);
        String errMessage = "You haven't blocked this user, so you can't unblock them!";

        Optional<UserRelationshipEntity> relationshipResult = userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                userRelationshipContext.getFirstUserId(),
                userRelationshipContext.getSecondUserId()
        );
        if (relationshipResult.isEmpty()) {
            throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
        }

        UserRelationshipEntity relationship = relationshipResult.get();

        /*
         * + There are only two scenarios where letting the auth. user unblock another user is valid:
         * 1. (authUserIsFirstUser && status == BLOCK_FIRST_SECOND) || (!authUserIsFirstUser && status == BLOCK_SECOND_FIRST):
         *    This case describes when our authenticated user has blocked the other user. Therefore, they are able to unblock the other user.
         *    To do this, simply delete the relationship between them, because unblocking leads to a blank slate, indicating that
         *    there's no relationship between these users anymore. This is the default behavior in the application. If they haven't
         *    friended or requested each other, then no relationship should exist. This is covered in the first two switch
         *    cases' conditionals.
         * 2. status == BLOCK_BOTH:
         *    This indicates that both users have blocked each other. Since the auth. user is unblocking the other user,
         *    update the status of the relationship to show that only the other user has the auth. user blocked now.
         *    If the auth. user is the firstUser, set the status to 'BLOCK_SECOND_FIRST' to indicate that only the secondUser (other user)
         *    has the firstUser (auth. user) blocked. Conversely, if the auth. user is the secondUser, set the status to 'BLOCK_FIRST_SECOND'.
         *    This is covered in the third switch case.
         *
         *  + Invalid cases:
         * You can't unblock someone that you don't already have blocked. So if the relationship does not have  a 'blocked'
         * status. So the status of the relationship could be one of the pending enumerations, or friends. In any case, the
         *  auth. user won't be able to  'unblock' the other user because the relationship isn't in a blocked status in the first place.
         * You can't 'unblock' something that isn't blocked in the first place.
         */
        switch(relationship.getStatus()) {
            case BLOCK_FIRST_SECOND:
                // Indicates that the other user has blocked the auth. user in this relationship, terminate the unblocking process.
                if (!userRelationshipContext.isAuthUserIsFirstUser()) {
                    throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
                }
                userRelationshipRepository.delete(relationship);
                break;
            case BLOCK_SECOND_FIRST:
                // Again, this means the relationship has a 'blocked' status because the other user has blocked the auth. user
                if (userRelationshipContext.isAuthUserIsFirstUser()) {
                    throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
                }
                userRelationshipRepository.delete(relationship);
                break;
            case BLOCK_BOTH:
                relationship.setStatus(
                        userRelationshipContext.isAuthUserIsFirstUser() ? BLOCK_SECOND_FIRST : BLOCK_FIRST_SECOND
                );
                userRelationshipRepository.save(relationship);
                break;
            case PENDING_FIRST_SECOND:
            case PENDING_SECOND_FIRST:
            case FRIENDS:
                throw new ServiceException(errMessage, HttpStatus.BAD_REQUEST);
            default:
                throw new RuntimeException("Unrecognized UserRelationship status: " + relationship.getStatus());
        }

        return userRelationshipMapper.toDTO(relationship);
    }

    @Override
    public List<UserRelationshipDTO> getAuthUserRelationships() {
        Long authUserId = authUtils.getAuthUserId();
        List<UserRelationshipEntity> relationships = userRelationshipRepository.findByUserId(authUserId);
        return relationships.stream().map(userRelationshipMapper::toDTO).toList();
    }
}