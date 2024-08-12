package com.knguyendev.api.controllers;

import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipRequest;
import com.knguyendev.api.services.UserRelationshipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/userRelationships")
public class UserRelationshipController {
    private final UserRelationshipService userRelationshipService;
    public UserRelationshipController(UserRelationshipService userRelationshipService) {
        this.userRelationshipService = userRelationshipService;
    }

    @PostMapping(path="/request")
    public ResponseEntity<UserRelationshipDTO> requestFriendship(@Valid @RequestBody UserRelationshipRequest relationshipRequest) {
         UserRelationshipDTO dto = userRelationshipService.requestFriendship(relationshipRequest.getTargetUserId());
         return new ResponseEntity<>(dto, HttpStatus.OK);
     }

    @PutMapping(path="/accept")
    public ResponseEntity<UserRelationshipDTO> acceptFriendRequest(@Valid @RequestBody UserRelationshipRequest relationshipRequest) {
        UserRelationshipDTO dto = userRelationshipService.acceptFriendRequest(relationshipRequest.getTargetUserId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
     }

    @DeleteMapping(path="/request")
    public ResponseEntity<UserRelationshipDTO> deleteFriendRequest(@Valid @RequestBody UserRelationshipRequest relationshipRequest) {
        UserRelationshipDTO dto = userRelationshipService.deleteFriendRequest(relationshipRequest.getTargetUserId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(path="/friend")
    public ResponseEntity<UserRelationshipDTO> deleteFriendship(@Valid @RequestBody UserRelationshipRequest relationshipRequest) {
        UserRelationshipDTO dto = userRelationshipService.deleteFriendship(relationshipRequest.getTargetUserId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping(path="/block")
    public ResponseEntity<UserRelationshipDTO> blockUser(@Valid @RequestBody UserRelationshipRequest relationshipRequest) {
        UserRelationshipDTO dto = userRelationshipService.blockUser(relationshipRequest.getTargetUserId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping(path="/unblock")
    public ResponseEntity<UserRelationshipDTO> unblockUser(@Valid @RequestBody UserRelationshipRequest relationshipRequest) {
        UserRelationshipDTO dto = userRelationshipService.unblockUser(relationshipRequest.getTargetUserId());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path="")
    public ResponseEntity<List<UserRelationshipDTO>> getRelationships() {
        List<UserRelationshipDTO> relationships = userRelationshipService.getAuthUserRelationships();
        return new ResponseEntity<>(relationships, HttpStatus.OK);
    }











    /*
     * + Controller routes being suggested (all protected routes by the way)
     * POST /relationships/request - Request a friendship.
     * PUT /relationships/accept - Accept a friend request.
     * DELETE /relationships/request - Delete a friend request.
     * DELETE /relationships/friend - Delete a friendship.
     * PUT /relationships/block - Block a user.
     * PUT /relationships/unblock - Unblock a user.
     * GET /relationships - Get all relationships for a given user or the authenticated user
     * So you can have optional query parameters such as 'targetUserId' or 'type'. But if you don't provide the
     * targetUserId, the controller will assume that you're referring to the currently authenticated user. In that
     * case we will get that user Id from the session and use it. Probably will have a getRelationshipsByUserId() service
     * function and a 'getAuthUserRelationship' function that uses the former as a helper function.
     *
     * Nearly every (except the GET '/relationships') controller will provide
     * a 'targetUserId' in the request body to indicate the id of the other user in the relationship
     * that the authenticated user is referring to. This is because we already have the authenticated user id available
     * to us, we don't need the client passing in extra data that we won't use.
     *
     * NOTE: I can see 3 cases where you'd use the primary key. Accepting or deleting a request,
     * deleting a friendship, or unblocking a user. In all of these, a relationship has to exist
     * for them to successfully finish. The others, request friendship, block user, and
     * getRelationships involve the firstUserId or secondUserId. Do with this as you will.
     * I never intended to have 'id' PK, but here it is.
     *
     *
     *
     * + Service functions:
     * In the service functions being suggested, they suggest having a conditional
     * which swaps the firstUserId and secondUserId so that the Ids are in order, which is smart.
     * The swapping could be delegated to a function that returns an object or array though. I doubt
     * we'd be able to do the things we've seen in C++ where they swap the values of
     *
     * Here are the method signatures that were suggested:
     * public UserRelationshipDTO requestFriendship(Long targetUserId);
     * public UserRelationshipDTO acceptFriendship(Long targetUserId);
     * public void deleteFriendRequest(Long targetUserId);
     * public void deleteFriendship(Long targetUserId);
     * public UserRelationshipDTO blockUser(Long targetUserId);
     * public UserRelationshipDTO unblockUser(Long targetUserId);
     *
     * // Gets the relationships of the authenticated user.
     * public getAuthUserRelationships(UserRelationshipStatus status);
     * public getRelationshipsByUserId(Long id, UserRelationshipStatus status);
     *
     * Our service functions are going to be used primarily for the API. They allow us to pass in the id of another user
     * that's going to be involved in the relationship with the authenticated user. The authenticated user acts as the
     * 'initiator' as sending a friend request will indicate that the authenticated user did the action.
     * The logic for getting the ID of the authenticated user is embedded in the service layer, so the controller doesn't
     * have to deal with it. The only thing the controller needs to do is to pass the targetUserId into the service layer.
     * Then the service layer can work its magic and create the user relationships.
     *
     *
     * */






}
