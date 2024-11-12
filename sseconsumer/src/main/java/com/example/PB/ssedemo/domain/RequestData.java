package com.example.PB.ssedemo.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestData {

    @JsonProperty("id")
    public int id;
}
