package kct.co.id.skilltest.service;

import kct.co.id.skilltest.dto.UserDTO;
import kct.co.id.skilltest.model.BaseResponse;
import kct.co.id.skilltest.model.UserPayload;

import java.util.List;

public interface UserService {
    BaseResponse<List<UserDTO>> get() throws Exception;

    BaseResponse<UserDTO> post(UserPayload userPayload) throws Exception;

    BaseResponse<UserDTO> put(Long id, UserPayload userPayload) throws Exception;

    BaseResponse<Boolean> delete(Long id) throws Exception;
}
