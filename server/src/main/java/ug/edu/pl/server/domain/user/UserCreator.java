package ug.edu.pl.server.domain.user;

import ug.edu.pl.server.domain.user.dto.CreateUserDto;

class UserCreator {

    User from(CreateUserDto dto) {
        var user = new User();

        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPhoneNumber(dto.phoneNumber());

        return user;
    }
}
