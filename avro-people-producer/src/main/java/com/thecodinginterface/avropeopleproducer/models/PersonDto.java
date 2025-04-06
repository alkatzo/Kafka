package com.thecodinginterface.avropeopleproducer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonDto {
    private String firstName;
    private String lastName;
    private String title;
}
