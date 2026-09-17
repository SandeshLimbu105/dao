package org.texas.systembdao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DaoCitizenResponse {

    private String nid;
    private String fullName;
    private String address;
    private String parentsNames;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String dobSource;
}