package org.example.aipoweredmentalhealthassistant.util;

import org.example.aipoweredmentalhealthassistant.DTO.response.UserloginResponseDTO;
import org.example.aipoweredmentalhealthassistant.entity.User;
import org.example.aipoweredmentalhealthassistant.enumClass.UserStatus;

public final class UserConvert {

    private UserConvert() {
    }

    public static UserloginResponseDTO.UserDetailResponseDTO toUserDetailResponse(User user) {
        if (user == null) {
            return null;
        }

        UserloginResponseDTO.UserDetailResponseDTO response =
                new UserloginResponseDTO.UserDetailResponseDTO();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        response.setBirthday(user.getBirthday() == null ? null : user.getBirthday().toString());
        response.setUserType(user.getUserType());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt() == null ? null : user.getCreatedAt().toString());
        response.setUpdatedAt(user.getUpdatedAt() == null ? null : user.getUpdatedAt().toString());
        response.setDisplayName(user.getNickname() == null
                || user.getNickname().isBlank() ? user.getUsername() : user.getNickname());

        UserStatus status = UserStatus.fromCode(user.getStatus());
        if (status != null) {
            response.setStatusDisplayName(status.getDisplayName());
        }
        return response;
    }
}
