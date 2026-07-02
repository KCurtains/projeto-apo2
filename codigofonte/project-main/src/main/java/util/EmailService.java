package util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    private static final String HOST = "sandbox.smtp.mailtrap.io";
    private static final String PORT = "2525"; 

    private static final String USER =
            System.getenv().getOrDefault("MAILTRAP_USER", "b4b40c0e30a852");
    private static final String PASS =
            System.getenv().getOrDefault("MAILTRAP_PASS", "adf7abaa05ea81");

    private static final String REMETENTE = "no-reply@easyparking.com";

    public static void enviarEmail(String destinatario, String assunto, String corpo) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, PASS);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setText(corpo);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    public static void enviarCodigo(String destinatario, String codigo) {
        String assunto = "Seu código de verificação - EasyParking";
        String corpo = "Olá!\n\n"
                     + "Seu código de verificação é: " + codigo + "\n\n"
                     + "Se você não solicitou, ignore este e-mail.\n\n"
                     + "Equipe EasyParking";
        enviarEmail(destinatario, assunto, corpo);
    }
}
