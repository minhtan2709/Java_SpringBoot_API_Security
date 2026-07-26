package com.javaspringboot_tutorial.security.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.javaspringboot_tutorial.security.dto.validator.PhoneNumber; // Giữ nguyên custom validator nếu có tạo ở bài trước
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    @NotNull(message = "lastName must be not null") // Không cho phép giá trị null
    private String lastName;

    @Email(message = "email invalid format") // Chỉ chấp nhận đúng định dạng email
    private String email;

    // @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber(message = "phone invalid format") // Sử dụng Custom Annotation để validate số điện thoại giống video
    private String phone;

    public UserDetailResponse(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}