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
public class DobFetchResponse {

    private String nid;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String source;   // SYSTEM_A or MANUAL
}