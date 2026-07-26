package com.javaspringboot_tutorial.security.service;

import com.javaspringboot_tutorial.security.dto.request.UserRequestDTO;
import com.javaspringboot_tutorial.security.dto.response.PageResponse;
import com.javaspringboot_tutorial.security.dto.response.UserDetailResponse;
import com.javaspringboot_tutorial.security.util.UserStatus;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;

public interface UserService {
    long saveUser(UserRequestDTO requestDTO) throws MessagingException, UnsupportedEncodingException;

    void updateUser(long userid, UserRequestDTO requestDTO);

    void changeStatus (long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getALlUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getALlUsersWithSortByMultipleColumns(int pageNo, int pageSize, String ... sorts); // ...: list String JAVA11

    PageResponse<?> getALlUsersWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String search, String sortBy); // ...: list String JAVA11

    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search); // ...: list String JAVA11

    PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address);

    void confirmUser(@Min(1) int userId, String sercretCode);
}
