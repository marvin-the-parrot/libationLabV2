package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordResetDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service for ApplicationUser Entity.
 */
public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email) throws NotFoundException;

    /**
     * Find an application user based on the id.
     *
     * @param userId the id of the user
     * @return a application user
     */
    ApplicationUser findApplicationUserById(Long userId) throws NotFoundException;

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Register a new user.
     *
     * @param userCreateDto create credentials
     */
    void register(UserCreateDto userCreateDto) throws ConstraintViolationException;

    /**
     * Reset the password of a user.
     *
     * @param passwordResetDto reset credentials
     */
    void resetPassword(PasswordResetDto passwordResetDto);

    /**
     * Retrieve all stored users, that match the given parameters.
     * The parameters may include a limit on the amount of results to return.
     *
     *  @param searchParams parameters to search users by
     *  @return a stream of users matching the parameters
     */
    List<UserListDto> search(UserSearchDto searchParams);


    /**
     * Send an email to the user with a link to reset his password.
     *
     *  @param email the email address of the user who forgot his password
     */
    void forgotPassword(String email);
}
