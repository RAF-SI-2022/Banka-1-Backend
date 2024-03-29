package org.banka1.bankservice.domains.dtos.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.user.Gender;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate birthDate;

    private Gender gender;
    private String email;
    private String phoneNumber;
    private String homeAddress;
    private List<String> roles;

}
