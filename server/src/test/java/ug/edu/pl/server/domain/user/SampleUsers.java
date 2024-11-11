package ug.edu.pl.server.domain.user;

import ug.edu.pl.server.domain.user.dto.CreateUserDto;

public final class SampleUsers {

    public static final String INVALID_EMAIL = "email-that-does-not-exist";
    public static final String EMAIL_THAT_DOES_NOT_EXIST = "email@email.pl";
    public static final String PASSWORD_THAT_DOES_NOT_EXIST = "password";
    public static final Long ID_THAT_DOES_NOT_EXIST = 1L;

    public static final CreateUserDto VALID_USER = CreateUserDto.builder()
            .email("john.doe@example.com")
            .password("StrongPassword123!")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final CreateUserDto VALID_USER_2 = CreateUserDto.builder()
            .email("john.doe2@example.com")
            .password("StrongPassword123!")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final CreateUserDto USER_WITH_INVALID_PASSWORD = CreateUserDto.builder()
            .email("john.doe@example.com")
            .password("short")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final CreateUserDto USER_WITH_BLANK_EMAIL = CreateUserDto.builder()
            .email("")
            .password("Password123!")
            .firstName("Invalid")
            .lastName("EmailUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final CreateUserDto USER_WITH_INVALID_EMAIL = CreateUserDto.builder()
            .email("invalid-email-format")
            .password("Password123!")
            .firstName("Invalid")
            .lastName("FormatUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final CreateUserDto USER_WITH_BLANK_PASSWORD = CreateUserDto.builder()
            .email("blank.password@example.com")
            .password("")
            .firstName("Blank")
            .lastName("PasswordUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final CreateUserDto USER_WITH_BLANK_FIRST_NAME = CreateUserDto.builder()
            .email("blank.firstname@example.com")
            .password("ValidPassword123!")
            .firstName("")
            .lastName("BlankNameUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final CreateUserDto USER_WITH_BLANK_LAST_NAME = CreateUserDto.builder()
            .email("blank.lastname@example.com")
            .password("ValidPassword123!")
            .firstName("Blank")
            .lastName("")
            .phoneNumber("987-654-3210")
            .build();
}
