package com.example.springboot_2.controller.User;

import com.example.springboot_2.dto.ApiResponse;
import com.example.springboot_2.dto.UserResponse;
import com.example.springboot_2.model.Product.Product;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Page<UserResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        return userService.getUsers(page, size, sortBy, direction);
    }

    @GetMapping("/myInfo")
    public ApiResponse<Object> getMyInfo(){
        return ApiResponse.builder().result(userService.getMyInfo()).build();
    }
}
