package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserListGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLocalStorageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserSearchExistingGroupDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationGroup;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetToken;
import at.ac.tuwien.sepr.groupphase.backend.entity.UserGroup;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ResetTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserGroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

/**
 * User service implementation.
 */
@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator validator;
    private final UserMapper userMapper;

    // values for the sendEmail method
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.auth}")
    private boolean auth;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.password}")
    private String senderPassword;

    /**
     * Customer user detail service.
     *
     * @param userRepository       - for persistence call
     * @param resetTokenRepository - ?
     * @param userGroupRepository  - for persistence call
     * @param passwordEncoder      - of use password
     * @param jwtTokenizer         - token
     * @param validator            - user validation
     * @param userMapper           - mapper
     */

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, ResetTokenRepository resetTokenRepository,
                                   UserGroupRepository userGroupRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer, UserValidator validator,
                                   UserMapper userMapper, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.userGroupRepository = userGroupRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.validator = validator;
        this.userMapper = userMapper;
        this.groupRepository = groupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

            return new User(applicationUser.getEmail(), applicationUser.getPassword(),
                grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) throws NotFoundException {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s",
            email));
    }

    @Override
    public ApplicationUser findApplicationUserById(Long userId) throws NotFoundException {
        LOGGER.debug("Find application user by id");
        ApplicationUser applicationUser = userRepository.findById(userId).orElse(null);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException("Could not find the user");
    }

    @Override
    public String login(UserLoginDto userLoginDto) throws BadCredentialsException, UsernameNotFoundException {
        LOGGER.debug("Login user");
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    public void register(UserCreateDto userCreateDto) throws ConstraintViolationException, ValidationException {
        LOGGER.debug("Register new user");
        validator.validateForCreate(userCreateDto);
        ApplicationUser applicationUser = new ApplicationUser(
            userCreateDto.getName(),
            userCreateDto.getEmail(),
            passwordEncoder.encode(userCreateDto.getPassword())
        );
        userRepository.save(applicationUser);
        userRepository.flush();

    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) throws ValidationException {
        LOGGER.debug("Reset password");

        ResetToken resetToken = resetTokenRepository.findByToken(resetPasswordDto.getToken());

        if (resetToken == null) {
            throw new NotFoundException(String.format("The Token is not valid", resetPasswordDto.getToken()));
        }

        ApplicationUser applicationUser = userRepository.findById(resetToken.getUserId()).orElse(null);

        if (applicationUser != null) {
            if (resetPasswordDto.getPassword().length() < 8) {
                List<String> errors = new ArrayList<>();
                errors.add("Password must be at least 8 characters long");
                throw new ValidationException("ValidationException", errors);
            }
            applicationUser.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            userRepository.save(applicationUser);
            userRepository.flush();
            resetTokenRepository.deleteById(resetToken.getId());
        } else {
            throw new NotFoundException(String.format("Could not find the user for this Token", resetToken.getUserId()));
        }
    }

    @Override
    public List<UserListDto> search(UserSearchExistingGroupDto searchParams) {
        LOGGER.trace("search({})", searchParams);
        if (searchParams.getGroupId() == null) {
            return searchCreatingGroup(searchParams);
        } else {
            return searchExistingGroup(searchParams);
        }
    }

    @Override
    public void forgotPassword(String email) throws NotFoundException {
        LOGGER.debug("Forgot password");
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            sendEmail(applicationUser.getEmail());
        } else {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    @Override
    public UserLocalStorageDto getUserByEmail() {
        LOGGER.debug("Get user by email");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            UserLocalStorageDto user = new UserLocalStorageDto();
            user.setId(applicationUser.getId());
            user.setName(applicationUser.getName());
            user.setEmail(applicationUser.getEmail());
            return user;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public List<UserListGroupDto> findUsersByGroup(ApplicationGroup group) {
        LOGGER.debug("Find users by group");
        List<UserGroup> userGroup = userGroupRepository.findAllByApplicationGroup(group);
        List<UserListGroupDto> users = new ArrayList<>();
        for (UserGroup userGroup1 : userGroup) {
            ApplicationUser userData = (userRepository.findApplicationUsersByUserGroups(userGroup1));
            UserListGroupDto user = new UserListGroupDto();
            user.setId(userData.getId());
            user.setName(userData.getName());
            user.setHost(userGroup1.isHost());
            users.add(user);
        }
        return users;
    }

    @Override
    public void deleteUserByEmail() {
        LOGGER.debug("Delete user by email");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            List<UserGroup> userGroups = userGroupRepository.findAllByApplicationUser(applicationUser);
            for (UserGroup userGroup1 : userGroups) {
                if (userGroup1.isHost()) {

                    ApplicationGroup group = userGroup1.getGroup();
                    List<UserGroup> userGroupList = userGroupRepository.findAllByApplicationGroup(group);
                    //If user is only member of group
                    if (userGroupList.size() == 1) {
                        userGroupRepository.delete(userGroup1);
                        groupRepository.delete(group);
                    } else {
                        for (UserGroup userGroup2 : userGroupList) {
                            if (!userGroup2.isHost()) {
                                userGroup1.setHost(false);
                                userGroupRepository.save(userGroup1);
                                userGroup2.setHost(true);
                                userGroupRepository.save(userGroup2);
                            }
                        }
                    }
                }
            }
            userRepository.delete(applicationUser);
        } else {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    private List<UserListDto> searchExistingGroup(UserSearchExistingGroupDto searchParams) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ApplicationUser> users = userGroupRepository.findUsersByGroupId(searchParams.getGroupId());
        List<String> emails = new ArrayList<>();
        for (ApplicationUser user : users) {
            emails.add(user.getEmail());
        }
        return userMapper.userToUserListDto(userRepository.findFirst5ByEmailNotAndEmailNotInAndNameIgnoreCaseContaining(email, emails, searchParams.getName()));
    }

    private List<UserListDto> searchCreatingGroup(UserSearchExistingGroupDto searchParams) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.userToUserListDto(userRepository.findFirst5ByEmailNotAndNameIgnoreCaseContaining(email, searchParams.getName()));
    }

    private void sendEmail(String recipientEmail) {
        // Sender's email address and password
        long userId = userRepository.findByEmail(recipientEmail).getId();

        // Setup properties for the SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", port);

        // Create a Session object with authentication
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field of the header
            message.setFrom(new InternetAddress(senderEmail));

            // Set To: header field of the header
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Set Subject: header field
            message.setSubject("Reset your password");

            // Set the content of the email message
            message.setText("Here is your link to reset your password: " + generateResetLink(generateToken(userId)));

            // Send the email
            Transport.send(message);

            LOGGER.info("Sent email successfully");

        } catch (MessagingException e) {
            LOGGER.warn("Failed to send email");
        }
    }

    private String generateToken(long userId) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // Change the byte size as needed
        random.nextBytes(bytes);
        ResetToken resetToken = ResetToken.ResetTokenBuilder.resetToken()
            .withUserId(userId)
            .withToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
            .build();
        resetTokenRepository.save(resetToken);
        LOGGER.warn(resetTokenRepository.findAll().toString());

        return resetToken.getToken();
    }


    // Generate a reset password link with a token parameter
    public static String generateResetLink(String token) {
        return "http://localhost:4200/#/reset-password?token=" + token;
    }


}
