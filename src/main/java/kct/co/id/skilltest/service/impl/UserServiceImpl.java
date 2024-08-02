package kct.co.id.skilltest.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kct.co.id.skilltest.dto.UserDTO;
import kct.co.id.skilltest.entity.Address;
import kct.co.id.skilltest.entity.User;
import kct.co.id.skilltest.model.BaseResponse;
import kct.co.id.skilltest.model.UserPayload;
import kct.co.id.skilltest.repository.UserRepository;
import kct.co.id.skilltest.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Autowired
    private UserRepository userRepository;

    @Override
    public BaseResponse<List<UserDTO>> get() throws Exception {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = OBJECT_MAPPER.convertValue(users, new TypeReference<>() {
        });
        return BaseResponse
                .<List<UserDTO>>builder()
                .success(true)
                .message("List users")
                .data(userDTOS)
                .build();
    }

    @Override
    public BaseResponse<UserDTO> post(UserPayload userPayload) throws Exception {
        User user = new User();
        Address address = new Address();
        BeanUtils.copyProperties(userPayload, user);
        BeanUtils.copyProperties(userPayload.getAddress(), address);
        user.setAddress(address);
        user = userRepository.save(user);
        UserDTO userDTO = OBJECT_MAPPER.convertValue(user, UserDTO.class);
        return BaseResponse
                .<UserDTO>builder()
                .message("Create success")
                .success(true)
                .data(userDTO)
                .build();
    }

    @Override
    public BaseResponse<UserDTO> put(Long id, UserPayload userPayload) throws Exception {
        User user = userRepository.getReferenceById(id);
        BeanUtils.copyProperties(userPayload, user);
        Address address = user.getAddress();
        BeanUtils.copyProperties(userPayload.getAddress(), address);
        address.setUpdatedAt(LocalDateTime.now());
        user.setAddress(address);
        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        UserDTO userDTO = OBJECT_MAPPER.convertValue(user, UserDTO.class);
        return BaseResponse
                .<UserDTO>builder()
                .message("Create success")
                .success(true)
                .data(userDTO)
                .build();
    }

    @Override
    public BaseResponse<Boolean> delete(Long id) throws Exception {
        userRepository.deleteById(id);
        return BaseResponse
                .<Boolean>builder()
                .message("Delete success!")
                .success(true)
                .build();
    }
}
