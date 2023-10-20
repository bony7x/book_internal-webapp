package com.example.auth.controller.mapper;

import com.example.auth.controller.dto.UserDto;
import com.example.auth.controller.dto.UserResponseDto;
import com.example.auth.controller.dto.UserUpdateDto;
import com.example.auth.controller.dto.UserUpdateRoleDto;
import com.example.auth.domain.entity.User;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.utils.requests.ExtendedRequest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class LoginMapper {

    public abstract User map(UserDto dto);

    public Integer mapToInt(Borrowing value){
        return value.getId();
    }

    public abstract Borrowing mapToBrw(Integer value);

    public abstract UserDto map (User login);

    public abstract List<UserDto> map(List<User> users);

    public abstract User map(UserUpdateRoleDto user);

    public abstract User map(UserUpdateDto updateDto);

    public UserResponseDto mapToResponse(List<UserDto> users, ExtendedRequest request, Integer size){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsers(users);
        userResponseDto.setPageSize(request.getPageable().getPageSize());
        userResponseDto.setPageNumber(request.getPageable().getPageNumber());
        userResponseDto.setTotalCount(size);
        return userResponseDto;
    }
}
