package com.javaspringboot_tutorial.security.repository;

import com.javaspringboot_tutorial.security.dto.response.PageResponse;
import com.javaspringboot_tutorial.security.model.Address;
import com.javaspringboot_tutorial.security.model.User;
import com.javaspringboot_tutorial.security.repository.criteria.SearchCriteria;
import com.javaspringboot_tutorial.security.repository.criteria.UserSearchCriteriaQueryConsumer;
import com.javaspringboot_tutorial.security.repository.specification.SpecSearchCriteria;
import com.javaspringboot_tutorial.security.util.Gender;
import com.javaspringboot_tutorial.security.util.UserStatus;
import com.javaspringboot_tutorial.security.util.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> getALlUsersWithSortByMultipleColumnsAndSearch(int pageNo, int pageSize, String search, String sortBy) {

        StringBuilder sqlQuery = new StringBuilder("select new com.javaspringboot_tutorial.sample1.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) from User u where 1=1");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower (u.firstName) like lower(:firstName)");
            sqlQuery.append(" or lower (u.lastName) like lower(:lastName)");
            sqlQuery.append(" or lower (u.email) like lower(:email)");

        }

        if (StringUtils.hasLength(sortBy)) {
            //firstName:asc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.find()) {
                    sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1), matcher.group(3)));
                }

            }
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());

        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format("%%%s%%", search));
            selectQuery.setParameter("lastName", String.format("%%%s%%", search));
            selectQuery.setParameter("email", String.format("%%%s%%", search));

        }
        List users = selectQuery.getResultList();

//        System.out.println(users);

        // querry ra list user

        //querry so record
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1=1");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" and lower (u.firstName) like lower(?1)");
            sqlCountQuery.append(" or lower (u.lastName) like lower(?2)");
            sqlCountQuery.append(" or lower (u.email) like lower(?3)");

        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());

        if (StringUtils.hasLength(search)) {
            selectCountQuery.setParameter(1, String.format("%%%s%%", search));
            selectCountQuery.setParameter(2, String.format("%%%s%%", search));
            selectCountQuery.setParameter(3, String.format("%%%s%%", search));

        }
        Long totalElements = (Long) selectCountQuery.getSingleResult();
        System.out.println(totalElements);


        Page<?> page = new PageImpl<Object>(users, PageRequest.of(pageNo, pageSize), totalElements);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(totalElements.intValue() / pageSize)
                .items(page.stream().toList())
                .build();
    }

    public PageResponse advanceSearchUser(int pageNo, int pageSize, String sortBy, String address, String... search) {
        // firstName: T, lastName: T

        List<SearchCriteria> criteriaList = new ArrayList<>();
        // 1. lay ra ds user
        if (search != null) {
            for (String s : search) {
                if (s == null || s.trim().isEmpty()) {
                    continue;
                }
                //firstName:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }

        }
        // 2. lay ra so luong ban ghi (phan trang)
        List<User> users = getUsers(pageNo, pageSize, criteriaList, sortBy, address);

        Long totalElements = getTotalElements(criteriaList, address);

        return PageResponse.builder()
                .pageNo(pageNo) //offset: vi tri cua ban ghi trong danh sach
                .pageSize(pageSize)
                .totalPage(totalElements.intValue()) //totalElement
                .items(users)
                .build();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // Xu ly cac dieu kien tim kiem
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder, predicate, root);

        if (StringUtils.hasLength(address)) {
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            // tim kiem cac field cua address
            query.where(predicate);
        } else {
            criteriaList.forEach(queryConsumer);
            predicate = queryConsumer.getPredicate();

            query.where(predicate);
        }


        // sort
        if (StringUtils.hasLength(sortBy)) {
            //firstName:asc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String columnName = matcher.group(1);

                if (matcher.group(3).equalsIgnoreCase("desc")) {

                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));

                } else {

                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));

                }

            }


        }

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();

    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer searchConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder, predicate, root);

        if (StringUtils.hasLength(address)) {
            Join<Address, User> addressUserJoin = root.join("addresses");
            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            // tim kiem tren tat ca cac field cua address ?
            query.select(criteriaBuilder.count(root));
            query.where(predicate, addressPredicate);
        } else {
            criteriaList.forEach(searchConsumer);
            predicate = searchConsumer.getPredicate();
            query.select(criteriaBuilder.count(root));
            query.where(predicate);
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    public PageResponse getUserJoinedAddress(Pageable pageable, String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address, User> addressRoot = userRoot.join("addresses");

        // Build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)");
        if (user != null) {
            for (String u : user) {
                Matcher matcher = pattern.matcher(u);
                if (matcher.find()) {
                    SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                    Predicate predicate = toUserPredicate(userRoot, criteriaBuilder, criteria);
                    userPre.add(predicate);

                }
            }
        }
        if(address !=null) {
            for (String a : address) {
                Matcher matcher = pattern.matcher(a);
                if (matcher.find()) {
                    SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                    Predicate predicate = toAddressPredicate(addressRoot, criteriaBuilder, criteria);
                    addressPre.add(predicate);

                }
            }
        }

        Predicate userPredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));
        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));
        Predicate finalPre = criteriaBuilder.and(userPredicateArr, addressPredicateArr);

        query.where(finalPre);

         List<User> users = entityManager.createQuery(query).
                setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long count = count(user, address);

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(count)
                .items(users)
                .build();


    }
    private long count(String[] user, String[] address) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<User> userRoot = query.from(User.class);
        Join<Address, User> addressRoot = userRoot.join("addresses");

        // Build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
        if(user !=null) {
            for (String u : user) {
                Matcher matcher = pattern.matcher(u);
                if (matcher.find()) {
                    SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                    Predicate predicate = toUserPredicate(userRoot, builder, criteria);
                    userPre.add(predicate);
                }
            }
        }
        if(address !=null) {
            for (String a : address) {
                Matcher matcher = pattern.matcher(a);
                if (matcher.find()) {
                    SpecSearchCriteria criteria = new SpecSearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                    Predicate predicate = toAddressPredicate(addressRoot, builder, criteria);
                    addressPre.add(predicate);
                }
            }
        }

        Predicate userPredicateArr = builder.or(userPre.toArray(new Predicate[0]));
        Predicate addressPredicateArr = builder.or(addressPre.toArray(new Predicate[0]));
        Predicate finalPre = builder.and(userPredicateArr, addressPredicateArr);

        query.select(builder.count(userRoot));
        query.where(finalPre);

        return entityManager.createQuery(query).getSingleResult();
    }

    public Predicate toUserPredicate(@NonNull final Root<User> root,  @NonNull final CriteriaBuilder builder, SpecSearchCriteria criteria) {
        String key = criteria.getKey().trim();
        String valueStr = criteria.getValue().toString().trim();

        return switch (criteria.getOperation()) {
            case EQUALITY -> {
                // Neu la truong gender thi chuyen sang Enum Gender
                if ("gender".equalsIgnoreCase(key)) {
                    yield builder.equal(root.get(key), Gender.valueOf(valueStr.toUpperCase()));
                }
                // Neu la truong status thi chuyen sang Enum UserStatus
                if ("status".equalsIgnoreCase(key)) {
                    yield builder.equal(root.get(key), UserStatus.valueOf(valueStr.toUpperCase()));
                }
                // Neu la truong type thi chuyen sang Enum UserType
                if ("type".equalsIgnoreCase(key)) {
                    yield builder.equal(root.get(key), UserType.valueOf(valueStr.toUpperCase()));
                }
                // Cac truong String/Long khac so sanh binh thuong
                yield builder.equal(root.get(key), valueStr);
            }
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }
    public Predicate toAddressPredicate(@NonNull final Join<Address, User> root,  @NonNull final CriteriaBuilder builder, SpecSearchCriteria criteria) {
        String key = criteria.getKey().trim();
        String valueStr = criteria.getValue().toString().trim();
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(key), valueStr);
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

}
