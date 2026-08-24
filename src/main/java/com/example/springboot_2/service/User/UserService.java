package com.example.springboot_2.service.User;


import com.example.springboot_2.dto.UserResponse;
import com.example.springboot_2.exception.AppException;
import com.example.springboot_2.exception.ErrorCode;

import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static org.hibernate.dialect.SybaseASEDialect.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //Method private: Tạo Pageable (phân trang) chung từ tham số + validate
    private Pageable buildPageable(int page,
                                   int size,
                                   String sortBy,
                                   String direction){
        //Ép page ko âm
        int safePage = Math.max(page,0);

        //Ép size trong khoảng hợp lệ (1 đén MAX_PAGE_SIZE)
        int safeSize = (size <= 0 || size > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : size;

        //Xác định hướng sắp xếp (ASC/DESC), mặc định là ASC nếu không truyền
        Sort.Direction sortDirection =
                Sort.Direction.fromString(
                        (direction == null || direction.isBlank())
                                ? "asc"
                                :direction
                );

        //Tạo đối tượng Sort theo cột và hướng sắp xếp
        //Nếu không truyền sortBy thì không sắp xếp
        Sort sort = (sortBy == null || sortBy.isBlank())
                ? Sort.unsorted()
                : Sort.by(sortDirection, sortBy);

        //Tạo đối tượng Pageable gồm:
        //- Trang hiện tại
        //- Số bản ghi mỗi trang
        //- Thông tin sắp xếp theo
        return PageRequest.of(safePage,safeSize,sort);
    }

    //Method public: Các API
    //API 1 + 2 (basic + sort)
    public Page<UserResponse> getUsers(int page,
                                       int size,
                                       String sortBy,
                                       String direction){
        Pageable pageable = buildPageable(page,size,sortBy,direction);
        Page<User> users = userRepository.findAll(pageable);
        Page<UserResponse> result = users.map(this::toDTO);

        return result;
    }
    private UserResponse toDTO(User user){
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles());
        return dto;
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        return toDTO(user);
    }
}
