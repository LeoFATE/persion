package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;

@Data
public class Emp {
    @JsonProperty("eId")
    private Integer eId;

    @JsonProperty("eName")
    private String eName;

    @JsonProperty("eDId")
    private Integer eDId;

    @JsonProperty("eHireDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date eHireDate;

    @JsonProperty("eSex")
    private String eSex;
}
