import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerRoute;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.Properties;

public class SparkMail {

    private String mailUser = "";
    private String mailPassword = "";

    public SparkMail(String  user, String password) {
        this.mailUser = user;
        this.mailPassword = password;
    }

    public void sendMail(String from, String to, String subject, ModelAndView body) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailUser, mailPassword);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            StringWriter e = new StringWriter();
            Configuration configuration = new Configuration();
            configuration.setClassForTemplateLoading(FreeMarkerRoute.class, "");
            Template template =  configuration.getTemplate(body.getViewName());
            template.process(body.getModel(), e);

            message.setText(e.toString());

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
