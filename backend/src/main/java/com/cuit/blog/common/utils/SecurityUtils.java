package com.cuit.blog.common.utils;

import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

// Helper methods for working with the Spring Security context.
public final class SecurityUtils {

    private SecurityUtils() {
    }

    // Returns the identifier of the currently authenticated user or {@code null} if not available.
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        if (principal instanceof UserDetails) {
            // UserDetails without id information is unsupported in this project.
            return null;
        }
        return null;
    }

    // Returns the current user identifier or throws an unauthorized business exception when missing.
    public static Long getRequiredUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    // Returns the role of the currently authenticated user.
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getRole();
        }
        return null;
    }

    // Indicates whether the current user possesses the given role.
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String expectedRole = "ROLE_" + role.toUpperCase();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (expectedRole.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
