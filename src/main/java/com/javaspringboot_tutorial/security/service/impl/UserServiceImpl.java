package com.javaspringboot_tutorial.security.service.impl;

import com.javaspringboot_tutorial.security.dto.request.AddressDTO;
import com.javaspringboot_tutorial.security.dto.request.UserRequestDTO;
import com.javaspringboot_tutorial.security.dto.response.PageResponse;
import com.javaspringboot_tutorial.security.dto.response.UserDetailResponse;
import com.javaspringboot_tutorial.security.exception.ResourceNotFoundException;
import com.javaspringboot_tutorial.security.model.Address;
import com.javaspringboot_tutorial.security.model.User;
import com.javaspringboot_tutorial.security.repository.SearchRepository;
import com.javaspringboot_tutorial.security.repository.UserRepository;
import com.javaspringboot_tutorial.security.repository.specification.UserSpecificationsBuilder;
import com.javaspringboot_tutorial.security.service.UserService;
import com.javaspringboot_tutorial.security.util.UserStatus;
import com.javaspringboot_tutorial.security.util.UserType;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SearchRepository searchRepository;
    //private final MailService mailService;
    private final KafkaTemplate<String,String> kafkaTemplate;


    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional //postman tra req loi se khong add vao db
    public long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUserName())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .addresses(convertToAddress(request.getAddresses()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        // Lưu vào cơ sở dữ liệu qua Repository
        userRepository.save(user);
        if( user.getId() !=null){
            // Send email confirm here
            //mailService.sendConfirmLink(user.getEmail(), user.getId(),"secretCode");

            String message = String.format("%s,%s,%s", user.getEmail(), user.getId(),"code@123");
            kafkaTemplate.send("confirm-account-topic",message);
        }

        log.info("User has saved!");

        return user.getId();
    }



    @Override
    @Transactional
    public void updateUser(long userId, UserRequestDTO requestDTO) {
        User user = getUserById(userId);
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setDateOfBirth(requestDTO.getDateOfBirth());
        user.setGender(requestDTO.getGender());
        user.setPhone(requestDTO.getPhone());

        if (!requestDTO.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(requestDTO.getEmail());
        }

        user.setUsername(requestDTO.getUserName());
        user.setPassword(requestDTO.getPassword());
        user.setStatus(requestDTO.getStatus());
        user.setType(UserType.valueOf(requestDTO.getType().toUpperCase()));
        user.setAddresses(convertToAddress(requestDTO.getAddresses()));

        userRepository.save(user);

        log.info("User updated successfully");
        System.out.println("User updated successfully");
    }


    @Override
    @Transactional
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        log.info("Status change");
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User user = getUserById(userId); //tim userId
        userRepository.findById(userId);

    }


    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
    @Override
    public PageResponse<?> getALlUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
        int page = 0;
        if(pageNo > 0){
            page = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();

        // neu co gia tri
        if(StringUtils.hasLength(sortBy)){
            //firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }
                else{
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));

        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> responses = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(responses)
                .build();

    }

    @Override
    public PageResponse<?> getALlUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }
        List<Sort.Order> orders = new ArrayList<>();

        for (String sortBy : sorts) {
            if (sortBy == null || sortBy.trim().isEmpty()) {
                continue;
            }
            //firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));

        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailResponse> responses = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(responses)
                .build();
    }

    @Override
    public PageResponse<?> getALlUsersWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepository.getALlUsersWithSortByMultipleColumnsAndSearch(pageNo, pageSize, search, sortBy);

    }
    @Override
    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        return searchRepository.advanceSearchUser(pageNo,pageSize, sortBy, address, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecification(Pageable pageable, String[] user, String[] address) {

        Page<User> users = null;

        List<User> list = new ArrayList<>();

        if(user != null && address!=null){
            // tim kiem tren user va address --> join table
            return searchRepository.getUserJoinedAddress(pageable, user, address);

        } else if (user != null && address==null) {
            // tim kiem tren bang ser -> khong can join sang bang address

//            Specification<User> spec = UserSpec.hasFirstName("T"); //%T% khong quan tam truoc va sau, mien co ky tu T
//            Specification<User> genderSpec = UserSpec.notEqualGender(Gender.MALE);
//
//            Specification<User> finalSpec = spec.and(genderSpec);
            UserSpecificationsBuilder builder = new UserSpecificationsBuilder(); //
            for (String s : user) { //
                Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)"); //
                Matcher matcher = pattern.matcher(s); //
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)); //
                }
            }


            list = userRepository.findAll(builder.build());

            return  PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPage(10)
                    .items(list)
                    .build();
        }else{

            users = userRepository.findAll(pageable);

        }
        return  PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public void confirmUser(int userId, String sercretCode) {
        log.info("Confirmed");
    }

    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }

    private User getUserById (long userId){
        return  userRepository.findById(userId).orElseThrow(() ->new ResourceNotFoundException("User not found"));
    }
}
