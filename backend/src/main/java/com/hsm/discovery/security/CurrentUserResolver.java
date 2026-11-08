
package com.hsm.discovery.security;

import com.hsm.account.entity.User;
import com.hsm.security.SessionFilter;
import com.hsm.common.ApiException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    // Lấy ID nếu người dùng đã đăng nhập
    // Nếu là Guest thì trả về null
    public Long optionalUserId(HttpServletRequest request) {

        Object user = request.getAttribute(SessionFilter.USER);

        if (user instanceof User currentUser) {
            return currentUser.getId();
        }

        return null;
    }

    // Bắt buộc phải đăng nhập
    public Long requiredUserId(HttpServletRequest request) {

        Long userId = optionalUserId(request);

        if (userId == null) {
            throw new ApiException(
                    401,
                    "UNAUTHENTICATED",
                    "Authentication is required."
            );
        }

        return userId;
    }
}
