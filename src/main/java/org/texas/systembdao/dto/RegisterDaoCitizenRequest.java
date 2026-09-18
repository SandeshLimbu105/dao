package org.texas.systembdao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDaoCitizenRequest {

    @NotBlank(message = "NID is required")
    @Pattern(regexp = "\\d{10}", message = "NID must be exactly 10 digits")
    private String nid;

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must be at most 150 characters")
    private String fullName;

    @NotBlank(message = "Address is required")
    @Size(max = 250, message = "Address must be at most 250 characters")
    private String address;

    @NotBlank(message = "Parents' names are required")
    @Size(max = 250, message = "Parents' names must be at most 250 characters")
    private String parentsNames;
}