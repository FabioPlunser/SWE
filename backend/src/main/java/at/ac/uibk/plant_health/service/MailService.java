package at.ac.uibk.plant_health.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  @Autowired private JavaMailSender mailSender;

  /**
   * Sends an email to one or several people through the address
   * "simpson-lisa@gmx.at". Recipients cannot see whether other people received
   * the message too or who they would be
   * @param text the content of the mail as String
   * @param subject the subject of the mail as String
   * @param recipients String array of email addresses of everyone who should
   *     receive the message
   */
  public void sendMessage(String text, String subject, String[] recipients) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("" /* TODO */);
    message.setBcc(recipients);
    message.setSubject(subject);
    message.setText(text);

    mailSender.send(message);
  }
}