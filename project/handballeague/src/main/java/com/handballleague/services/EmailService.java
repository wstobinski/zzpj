package com.handballleague.services;

import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.User;
import com.handballleague.repositories.UserRepository;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
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


    public Message sendEmail(String email, String role) {
        if (email == null) throw new InvalidArgumentException("Passed email is invalid.");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
            throw new ObjectNotFoundInDataBaseException("User with given email was not found in database.");
        String host = "poczta.interia.pl";
        String username = "handball.league@interia.pl";
        String password = this.password;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        String roleToEmail;
        User user= userRepository.findByEmail(email).orElseThrow(() -> new ObjectNotFoundInDataBaseException("User with given email was not found in database."));

        if (role.equals("captain")) {
            roleToEmail = "kapitana";
        }  else {
            roleToEmail = "sędziego";
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Witamy w HandBallLeague!");
            MimeMultipart multipart = new MimeMultipart("related");

            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<html>" +
                    "<head>" +
                    "<meta charset=\"UTF-8\">" +
                    "</head>" +
                    "<body>" +
                    "<p>Twoje konto " + roleToEmail + " jest już gotowe!</p>" +
                    "<p>Oto twój kod aktywacyjny: " + user.getCode() + "</p>" +
                    "<p>Zespół Handball League</p>" +
                    "<img src=\"cid:logo\">" +
                    "</body>" +
                    "</html>";
            messageBodyPart.setContent(htmlText, "text/html; charset=UTF-8");
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(new File("project/handballeague/src/main/resources/logo.jpg"));
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<logo>");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);


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
