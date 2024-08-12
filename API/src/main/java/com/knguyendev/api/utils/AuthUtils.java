package com.knguyendev.api.utils;

import com.knguyendev.api.domain.dto.User.UserDetailsImpl;
import com.knguyendev.api.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    /**
     * Returns the ID of the authenticated user
     * @return ID of the authenticated user
     * NOTE: Careful about returning data from the SecurityContext because it's not always fresh or
     *       up to date! For example, if you update the authenticated user via updateAccountProfile,
     *       remember that the user data isn't updated in the security context. So viewing the
     *       user from the security context after will show you the old info. However, when doing
     *       things such as logging out, which happens when the user changes their password, the
     *       SecurityContext is cleared, and refreshed when they log in again. The only thing that
     *       you can trust, however, is definitely the 'id' of the user from the SecurityContext.
     *       This is something that is immutable and never changes, unlike something like username,
     *       firstName, etc. And so you can trust it as something that represents an identifier of the
     *       logged-in user.
     */
    public Long getAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new ServiceException("User is not authenticated!", HttpStatus.UNAUTHORIZED);
        }
        return userDetails.getUser().getId();
    }
}
