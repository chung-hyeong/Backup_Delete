package mission;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class mailSending {
    private String message = "";
    private String Id="";
    private String passwd="";

    mailSending(String Id, String passwd){
        this.Id = Id;
        this.passwd = passwd;
    }

    public void messageAdd(String message){
        this.message += message;
    }

    public void sendTest(){

            Properties props = System.getProperties();
            props.put("mail.smtp.host", "smtp.naver.com");
            props.put("mail.smtp.port", "25");
            props.put("defaultEncoding", "utf-8");
            props.put("mail.smtp.auth", "true");

            try {
                String sender = Id;
                String subject = "이메일 전송 확인";

                Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(Id.substring(0,Id.indexOf("@")), passwd);
                    }
                });

                session.setDebug(false); //Debug 모드 설정.
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(sender));
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(Id) ); //수신자 셋팅
                mimeMessage.setSubject(subject); //제목 세팅
                mimeMessage.setText("정상 이메일 확인."); //본문 세팅
                Transport.send(mimeMessage);
                System.out.println("테스트 메일 발송.");
            } catch (Exception e) {
                System.out.println("메일보내기 오류 : "+ e.getMessage());
            }
    }

    public void mailSend(){
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.port", "25");
        props.put("defaultEncoding", "utf-8");
        props.put("mail.smtp.auth", "true");

        try {
            String sender = Id;
            String subject = "파일 삭제 보고";

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(Id.substring(0,Id.indexOf("@")), passwd);
                }
            }
            );

            session.setDebug(false); //Debug 모드 설정.
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(Id) ); //수신자 셋팅
            mimeMessage.setSubject(subject); //제목 세팅
            mimeMessage.setText(message); //본문 세팅
            Transport.send(mimeMessage);
            message = "";
        } catch (Exception e) {
            System.out.println("메일보내기 오류 : "+ e.getMessage());
        }
    }
}
