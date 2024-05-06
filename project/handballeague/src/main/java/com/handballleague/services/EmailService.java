package com.handballleague.services;

import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.User;
import com.handballleague.repositories.UserRepository;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@Service
public class EmailService {
    private final UserRepository userRepository;

    @Autowired
    public EmailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${mail.password}")
    private String password;

    public int generateCode() {
        Random random = new Random();

        int randomNumber = 100000 + random.nextInt(900000);

        return randomNumber;
    }

    public Message sendEmail(String email) {
        if (email == null) throw new InvalidArgumentException("Passed email is invalid.");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
            throw new ObjectNotFoundInDataBaseException("User with given email was not found in database.");
        String host = "poczta.interia.pl";
        String username = "handball.league@interia.pl";
        String password = this.password;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        int code = generateCode();

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Witamy w HandBallLeague!");
            message.setText("Oto twój kod aktywacyjny: " + code);

            Transport.send(message);

            User user= userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundInDataBaseException("User with given email was not found in database."));
            System.out.println(code);
            user.setCode(code);
            userRepository.save(user);

            return message;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public User activateAcc(int code, String password) {
        if (password == null) throw new InvalidArgumentException("Passed password is invalid.");
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByCode(code));
        if (optionalUser.isEmpty())
            throw new ObjectNotFoundInDataBaseException("User with given code was not found in database.");
        User user = userRepository.findByCode(code);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setActive(true);
        user.setCode(0);

        return userRepository.save(user);
    }
}
