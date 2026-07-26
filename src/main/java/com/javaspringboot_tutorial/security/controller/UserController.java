package com.javaspringboot_tutorial.security.controller;

import com.javaspringboot_tutorial.security.configuration.Translator;
import com.javaspringboot_tutorial.security.dto.request.UserRequestDTO;
import com.javaspringboot_tutorial.security.dto.response.ResponseData;
import com.javaspringboot_tutorial.security.dto.response.ResponseError;
import com.javaspringboot_tutorial.security.dto.response.UserDetailResponse;
import com.javaspringboot_tutorial.security.exception.ResourceNotFoundException;
import com.javaspringboot_tutorial.security.service.UserService;
import com.javaspringboot_tutorial.security.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name="User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //    @Operation(summary = "summary", description = "description",responses = {
//            @ApiResponse(responseCode = "201", description = "User added successfully",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                                examples = @ExampleObject(name = "ex name", summary = "ex summary",
//                                value = """
//                                        {
//                                            "data": 1,
//                                            "message": "User added successfully",
//                                            "status": 201
//                                        }
//                                        """
//                                ))
//            )
//    }) //dung khi có sử dụng swagger

    @Operation(summary = "Create User", description = "API create user")
    @PostMapping("/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO user) {
        log.info("Request add user, {} {}", user.getFirstName(), user.getLastName());

        try {
            long userId = userService.saveUser(user);

            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }
    }

    @Operation(summary = "Update User", description = "API update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) int userId, @Valid @RequestBody UserRequestDTO userRequestDTO){ //test postman Body raw
        log.info("Request update userId={}", userId);

        try{
            userService.updateUser(userId,userRequestDTO);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.update.success"));

        }catch(ResourceNotFoundException e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }
    @Operation(summary = "Change Status", description = "API change status")
    @PatchMapping("/{userId}")
    public ResponseData<?> changeStatus(@Min(1) @PathVariable int userId, @RequestParam UserStatus status){     // test postman params
        log.info("Request change status, userId={}", userId);
        try{
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("status.change.success"));

        }catch(Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }
    }
    @GetMapping("/confirm/{userId}")
    public ResponseData<?> confirmUser(@Min(1) @PathVariable int userId, @RequestParam String verifyCode, HttpServletResponse response) throws IOException {
        log.info("Confirm user userId={}, verifyCode={}", userId, verifyCode);

        try {
            userService.confirmUser(userId, verifyCode);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User confirmed!");
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Confirmation was failure");
        }finally {
            response.sendRedirect("https://www.java.com/en/"); //nhay domain sau khi confirm user
        }
    }
    @Operation(summary = "Delete User", description = "API delete user")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@PathVariable @Min(value = 1,message = "userId must be greater than 0")int userId){
        log.info("Request delete userId={}", userId);
        try{
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.delete.success"));

        }catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }
    }
    @Operation(summary = "Get User Detail", description = "API get user detail")
    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable @Min(1) int userId){
        log.info("Request get user detail, userId={}", userId);
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "User", userService.getUser(userId));
        }catch(ResourceNotFoundException e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @Operation(summary = "Get List User", description = "API get list user")
    @GetMapping("/list")
    public ResponseData<?> getAllUsers(
            @RequestParam (required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy){

        log.info("Request get all user of list");
        return new ResponseData<>(HttpStatus.OK.value(), "user",userService.getALlUsersWithSortBy(pageNo, pageSize, sortBy));

    }
    @Operation(summary = "Get List User Multiple Columns", description = "API get list user")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getALlUsersWithSortByMultipleColumns(
            @RequestParam (required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false) String... sorts){
        log.info("Request get all of user with multiple columns");

        return new ResponseData<>(HttpStatus.OK.value(), "user",userService.getALlUsersWithSortByMultipleColumns(pageNo, pageSize, sorts));

    }
    @Operation(summary = "Get List User Multiple Columns and search with paging and sorting", description = "Send a request this API to get user list by pageNo, pageSize and sort with multicolumn")
    @GetMapping("/list-with-sort-by-multiple-columns-search")
    public ResponseData<?> getALlUsersWithSortByMultipleColumnsAndSearch(
            @RequestParam (required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) String sortBy){
        log.info("Request get all of user with multiple columns and search");

        return new ResponseData<>(HttpStatus.OK.value(), "user",userService.getALlUsersWithSortByMultipleColumnsAndSearch(pageNo, pageSize, search, sortBy));

    }
    @Operation(summary = "Advance search query with by criteria", description = "Send a request this API to get user list by pageNo, pageSize and sort by criteria")
    @GetMapping("/advance-search-by-criteria")
    public ResponseData<?> advanceSearchByCriteria(
            @RequestParam (required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String address,
            @RequestParam(required = false, defaultValue = "") String... search) {
        log.info("Request advance search with criteria and paging and sorting");

        return new ResponseData<>(HttpStatus.OK.value(), "user",userService.advanceSearchByCriteria(pageNo, pageSize, sortBy, address, search));

    }
    @Operation(summary = "Advance search query with specification", description = "Send a request this API to get user list by pageNo, pageSize and sort with user and address")
    @GetMapping("/advance-search-with-specification")
    public ResponseData<?> advanceSearchWithSpecification(
            Pageable pageable,
            @RequestParam(required = false) String[] user,
            @RequestParam(required = false) String[] address) {
        log.info("Request advance search with criteria and paging and sorting");

        return new ResponseData<>(HttpStatus.OK.value(), "user",userService.advanceSearchWithSpecification(pageable, user, address));

    }
}
