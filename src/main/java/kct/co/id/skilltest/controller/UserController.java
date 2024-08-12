package kct.co.id.skilltest.controller;

import jakarta.validation.Valid;
import kct.co.id.skilltest.dto.UserDTO;
import kct.co.id.skilltest.model.BaseResponse;
import kct.co.id.skilltest.model.UserPayload;
import kct.co.id.skilltest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public BaseResponse<List<UserDTO>> get() throws Exception {
        return userService.get();
    }

    @GetMapping("/{guid}")
    public BaseResponse<UserDTO> getById(@PathVariable("id") UUID id) throws Exception {
        return userService.getById(id);
    }

    @PostMapping
    public BaseResponse<UserDTO> post(@RequestBody @Valid UserPayload userPayload) throws Exception {
        return userService.post(userPayload);
    }

    @PutMapping("/{id}")
    public BaseResponse<UserDTO> put(@PathVariable("id") Long id, @RequestBody @Valid UserPayload userPayload) throws Exception {
        return userService.put(id, userPayload);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable("id") Long id) throws Exception {
        return userService.delete(id);
    }
}
