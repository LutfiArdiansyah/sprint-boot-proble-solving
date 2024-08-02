package kct.co.id.skilltest.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AddressDTO implements Serializable {
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
