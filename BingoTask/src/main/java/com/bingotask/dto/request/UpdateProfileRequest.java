package com.bingotask.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String avatarUrl;
    private String email;
}