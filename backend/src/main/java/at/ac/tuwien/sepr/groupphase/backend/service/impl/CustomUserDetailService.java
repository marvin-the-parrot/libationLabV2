package at.ac.tuwien.sepr.groupphase.backend.service.impl;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.ResetToken;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ResetTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;

import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * User service implementation.
 */
@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    private final UserMapper userMapper;

    /**
     * Customer user detail service.
     *
     * @param userRepository       - for persistence call
     * @param resetTokenRepository
     * @param passwordEncoder      - of use password
     * @param jwtTokenizer         - token
     * @param userMapper           - mapper
     */
    @Autowired
    public CustomUserDetailService(UserRepository userRepository,
                                   ResetTokenRepository resetTokenRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userMapper = userMapper;
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
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s",
            email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
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
    public void register(UserCreateDto userCreateDto) throws ConstraintViolationException {
        LOGGER.debug("Register new user");
        ApplicationUser applicationUser = new ApplicationUser(
            userCreateDto.getName(),
            userCreateDto.getEmail(),
            passwordEncoder.encode(userCreateDto.getPassword())
        );
        userRepository.save(applicationUser);
        userRepository.flush();

    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        LOGGER.debug("Reset password");

        ResetToken resetToken = resetTokenRepository.findByToken(resetPasswordDto.getToken());
        //TODO: make validator
        if (resetToken == null) {
            throw new NotFoundException(String.format("Could not find the token %s", resetPasswordDto.getToken()));
        }

        ApplicationUser applicationUser = userRepository.findById(resetToken.getUserId()).orElse(null);

        if (applicationUser != null) {
            applicationUser.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            userRepository.save(applicationUser);
            userRepository.flush();
        } else {
            throw new NotFoundException(String.format("Could not find the user with the ID %s", resetToken.getUserId()));
        }
    }

    @Override
    public List<UserListDto> search(UserSearchDto searchParams) {
        LOGGER.trace("search({})", searchParams);
        return userMapper.userToUserListDto(userRepository.findByName(searchParams.getName()));
    }

    @Override
    public void forgotPassword(String email) throws NotFoundException{
        LOGGER.debug("Forgot password");
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            sendEmail(applicationUser.getEmail());
        } else {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    private void sendEmail(String email) {
        // Sender's email address and password
        final String senderEmail = "dionysuslibationlab@gmail.com";

        //DONT DELTE THIS!!!
        final String senderPassword = "mvry hsuu mjvm mxrz ";
        //DONT DELTE THIS!!!

        // Recipient's email address
        String recipientEmail = email;

        long userId = userRepository.findByEmail(recipientEmail).getId();

        // Setup properties for the SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", "465");

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
            message.setText("Here is your link to reset your password: " + ResetPasswordLinkGenerator.generateResetLink(generateToken(userId)));

            // Send the email
            Transport.send(message);

            LOGGER.info("Sent email successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
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

    private class ResetPasswordLinkGenerator {

        // Generate a reset password link with a token parameter
        public static String generateResetLink(String token) {
            return "http://localhost:4200/#/reset-password?token=" + token;
        }
    }

}
