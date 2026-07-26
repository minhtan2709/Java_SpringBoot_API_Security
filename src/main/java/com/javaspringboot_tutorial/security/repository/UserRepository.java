package com.javaspringboot_tutorial.security.repository;

import com.javaspringboot_tutorial.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

//    //@Query(value = "SELECT * FROM TBL_USER", nativeQuery = true) -> Native SQL
//    //@Query(value = "select u from User u inner join Address a on u.id = a.userId.id where a.city = :city") //do u.id la int, a.userId là Object nen gay bat dong du lieu
//    List<User> getAllUser(String city);
//
//    // - - Distinct - -
//    //@Query(value = "select distinct u from User u where u.firstName=:firstName and u.lastName=:lastName")
//    List<User> findDistinctByFirstNameAndLastName(String firstName, String LastName);
//
//    // - - Single field - -
//    //@Query(value = "select u from User u where u.email=:?1")
//    List<User> findByEmail(String email);
//
//    // - - OR - -
//    //@Query(value = "select u from User u where u.firstName=:name or u.lastName:name")
//    List<User> findByFirstNameOrLastName(String name);
//
//    // - - Is, Equals - -
//    //@Query(value = "select u from User u where u.firstName=:name")
//    List<User> findByFirstNameIs(String name);
//
//    List<User> findByFirstNameEquals(String name);
//
//    List<User> findByFirstName(String name);
//
//    // - - Between - -
//    //@Query(value = "select u from User u where u.createdAt between ?1 and 2?")
//    List<User> findByCreatedAtBetween(Date startDate, Date endDate);
//
//    // - - LessThan - -
//    //@Query(value = "select u from User u where u.age < :age>")
//    List<User> findByAgeLessThan(int age);
//    List<User> findByAgeLessThanEqual();
//    List<User> findByAgeGreaterThan();
//    List<User> findByAgeGreaterThanEquals();
//
//    // - - Before And After - -
//    //@Query(value = "select u from User u where u.createdAt < :date")
//    List<User> findByCreatedAtBefore(Date date);
//    List<User> findByCreatedAtAfter(Date date);
//
//    // - - Null, IsNull - -
//    //@Query(value = "select u from User u where u.age is null")
//    List<User> findByAgeIsNull();
//
//    // - - NotNull, IsNotNull - -
//    //@Query(value = "select u from User u where u.age is not null")
//    List<User> findByAgeIsNotNull();
//
//    // - - Like - -
//    //@Query(value = "select u from User u where u.lastName like %:lastName%")
//    List<User> findByLastNameLike(String lastName);
//
//    // - - NotLike - -
//    //@Query(value = "select u from User u where u.lastName not like %:lastName%")
//    List<User> findByLastNameNotLike(String lastName);
//
//    //  - - StartingWith - -
//    // @Query(value = "select u from User u where u.lastName not like :lastName%")
//    List<User> findByLastNameStartingWith(String lastName);
//
//    //  - - EndingWith - -
//    // @Query(value = "select u from User u where u.lastName not like %:lastName")
//    List<User> findByLastNameEndingWith(String lastName);
//
//    //  - - Containing - -
//    // @Query(value = "select u from User u where u.lastName not like %:lastName%")
//    List<User> findByLastNameContaining(String name);
//
//    //  - - Not - -
//    // @Query(value = "select u from User u where u.lastName <> :name")
//    List<User> findByLastNameNot(String name);
//
//    //  - - In - -
//    // @Query(value = "select u from User u where u.age in (18,25,30)")
//    List<User> findByAgeIn(Collection<Integer> ages);
//
//    //  - - Not in - -
//    //@Query(value = "select u from User u where u.age not in (18,25,30)")
//    List<User> findByAgeNotIn(Collection<Integer> ages);
//
//    //  - - True/False - -
//    //@Query(value = "select u from User u where u.activated=true")
//    //List<User> findByActivatedTrue();
//    //List<User> findByActivatedFalse();
//
//    //  - - IgnoreCase - -
//    // @Query(value = "select u from User u where LOWER(u.lastName) <> LOWER(:name)")
//    List<User> findByFirstNameIgnoreCase(String name);
//
//    //  - - Order by - -
//    // @Query(value = "select u from User u order by createdAt desc")
//    List<User> findByFirstNameOrderByCreatedAtDesc(String name);
//
//    //
//    List<User> findByFirstNameAndLastNameAllIgnoreCase(String firstName, String lastName);
}

