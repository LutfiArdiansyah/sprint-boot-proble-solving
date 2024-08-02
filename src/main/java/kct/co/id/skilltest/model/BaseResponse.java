package kct.co.id.skilltest.model;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@Embeddable
public class BaseResponse<T> implements Serializable {

    private Boolean success;

    private String message;

    private T data;

}
