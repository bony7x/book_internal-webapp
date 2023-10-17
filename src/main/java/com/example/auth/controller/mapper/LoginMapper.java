package com.example.auth.controller.mapper;

import com.example.auth.controller.dto.UserDto;
import com.example.auth.controller.dto.UserUpdateDto;
import com.example.auth.controller.dto.UserUpdateRoleDto;
import com.example.auth.domain.entity.User;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class LoginMapper {

    public abstract User map(UserDto dto);

    public abstract UserDto map (User login);

    public abstract List<UserDto> map(List<User> users);

    public abstract User map(UserUpdateRoleDto user);

    public abstract User map(UserUpdateDto updateDto);
}
