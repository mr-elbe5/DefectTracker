/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.mail;

import de.elbe5.base.data.BinaryFile;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Mailer {

    public enum SmtpConnectionType {
        plain, tls, ssl
    }

    protected String from = null;
    protected String to = null;
    protected String cc = null;
    protected String bcc = null;
    protected String subject = null;
    protected String content = "";
    protected String contentType = "text/plain";
    protected List<BinaryFile> files = null;
    protected String replyTo = null;
    protected String smtpHost = null;
    protected int smtpPort = 25;
    protected SmtpConnectionType smtpConnectionType = SmtpConnectionType.plain;
    protected String smtpUser = "";
    protected String smtpPassword = "";

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String str) {
        this.content = str;
        this.contentType = "text/plain; charset=UTF-8";
    }

    public void setHtml(String str) {
        this.content = str;
        this.contentType = "text/html; charset=UTF-8";
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void addFile(BinaryFile data) {
        if (files == null) {
            files = new ArrayList<>();
        }
        files.add(data);
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public void setSmtpConnectionType(SmtpConnectionType smtpConnectionType) {
        this.smtpConnectionType = smtpConnectionType;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public MimeMessage createMessage(Session session) throws Exception {
        MimeMessage msg = new MimeMessage(session);
        if (from != null) {
            msg.setFrom(new InternetAddress(from));
        } else {
            msg.setFrom();
        }
        if (replyTo == null) {
            replyTo = from;
        }
        if (replyTo != null) {
            msg.setReplyTo(InternetAddress.parse(replyTo, false));
        }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        if (cc != null) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
        }
        if (bcc != null) {
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
        }
        if (subject != null) {
            msg.setSubject(subject, "UTF-8");
        } else {
            msg.setSubject("");
        }
        if (files != null) {
            MimeMultipart mp = new MimeMultipart();
            MimeBodyPart mbpText = new MimeBodyPart();
            mbpText.setContent(content, contentType);
            mp.addBodyPart(mbpText);
            for (BinaryFile file : files) {
                InternetHeaders headers = new InternetHeaders();
                headers.addHeader("Content-type", file.getContentType());
                headers.addHeader("Content-disposition", "attachment; filename=" + file.getFileName());
                MimeBodyPart mbpFile = new MimeBodyPart(headers, file.getBytes());
                mp.addBodyPart(mbpFile);
            }
            msg.setContent(mp);
        } else {
            msg.setContent(content, contentType);
        }
        msg.setSentDate(new Date());
        return msg;
    }

    public boolean sendMail() throws Exception {
        if (to == null || smtpHost == null) {
            return false;
        }
        Properties props = System.getProperties();
        switch (smtpConnectionType) {
            case tls:
                return sendTLSMail(props);
            case ssl:
                return sendSSLMail(props);
            default:
                return sendPlainMail(props);
        }

    }

    public boolean sendTLSMail(Properties props) throws Exception {
        props.setProperty("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", Integer.toString(smtpPort));
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
        MimeMessage msg = createMessage(session);
        Transport transport = session.getTransport("smtp");
        transport.connect(smtpHost, smtpUser, smtpPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
        return true;
    }

    public boolean sendSSLMail(Properties props) throws Exception {
        props.setProperty("mail.smtps.host", smtpHost);
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.port", Integer.toString(smtpPort));
        props.put("mail.smtps.auth", "true");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
        MimeMessage msg = createMessage(session);
        Transport transport = session.getTransport("smtps");
        transport.connect(smtpHost, smtpUser, smtpPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
        return true;
    }

    public boolean sendPlainMail(Properties props) throws Exception {
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", Integer.toString(smtpPort));
        Session session = Session.getInstance(props, null);
        MimeMessage msg = createMessage(session);
        Transport.send(msg);
        return true;
    }
}
