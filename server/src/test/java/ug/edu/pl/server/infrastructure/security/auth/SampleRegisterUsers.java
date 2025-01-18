package ug.edu.pl.server.infrastructure.security.auth;


import ug.edu.pl.server.infrastructure.security.auth.dto.RegisterUserDto;

public final class SampleRegisterUsers {

    public static final String EMAIL_THAT_DOES_NOT_EXIST = "email@email.pl";
    public static final String PASSWORD_THAT_DOES_NOT_EXIST = "password";
    public static final Long ID_THAT_DOES_NOT_EXIST = 1L;

    public static final RegisterUserDto VALID_USER = RegisterUserDto.builder()
            .email("john.doe@example.com")
            .password("StrongPassword123!")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final RegisterUserDto VALID_USER_2 = RegisterUserDto.builder()
            .email("john.doe2@example.com")
            .password("StrongPassword123!")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final RegisterUserDto USER_WITH_INVALID_PASSWORD = RegisterUserDto.builder()
            .email("john.doe@example.com")
            .password("short")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("123-456-7890")
            .build();

    public static final RegisterUserDto USER_WITH_BLANK_EMAIL = RegisterUserDto.builder()
            .email("")
            .password("Password123!")
            .firstName("Invalid")
            .lastName("EmailUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final RegisterUserDto USER_WITH_INVALID_EMAIL = RegisterUserDto.builder()
            .email("invalid-email-format")
            .password("Password123!")
            .firstName("Invalid")
            .lastName("FormatUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final RegisterUserDto USER_WITH_BLANK_PASSWORD = RegisterUserDto.builder()
            .email("blank.password@example.com")
            .password("")
            .firstName("Blank")
            .lastName("PasswordUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final RegisterUserDto USER_WITH_BLANK_FIRST_NAME = RegisterUserDto.builder()
            .email("blank.firstname@example.com")
            .password("ValidPassword123!")
            .firstName("")
            .lastName("BlankNameUser")
            .phoneNumber("987-654-3210")
            .build();

    public static final RegisterUserDto USER_WITH_BLANK_LAST_NAME = RegisterUserDto.builder()
            .email("blank.lastname@example.com")
            .password("ValidPassword123!")
            .firstName("Blank")
            .lastName("")
            .phoneNumber("987-654-3210")
            .build();
}
