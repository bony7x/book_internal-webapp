package com.example.auth.controller.dto;

import com.example.auth.domain.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAndCountDto {

    private Integer totalCount;

    private List<User> users;
}
