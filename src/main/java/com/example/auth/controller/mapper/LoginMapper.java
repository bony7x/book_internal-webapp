package com.example.auth.controller.mapper;

import com.example.auth.controller.dto.LoginDto;
import com.example.auth.domain.entity.Login;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class LoginMapper {

    public abstract Login map(LoginDto dto);

    public abstract LoginDto map (Login login);
}
