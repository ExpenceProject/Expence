package ug.edu.pl.server.domain.user;

import ug.edu.pl.server.domain.user.dto.CreateUserDto;

public final class SampleUsers {

    public static final String INVALID_EMAIL = "email-that-does-not-exist";
    public static final Long ID_THAT_DOES_NOT_EXIST = 1L;

    public static final CreateUserDto VALID_USER = CreateUserDto.builder()
            .email("john.doe@example.com")
            .password("StrongPassword123!")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final CreateUserDto ANOTHER_VALID_USER = CreateUserDto.builder()
            .email("anna@example.com")
            .password("StrongPassword123!")
            .firstName("Anna")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();
}
