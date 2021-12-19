/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.log.Log;
import de.elbe5.base.mail.Mailer;

public class MailHelper {

    public static boolean sendPlainMail(String to, String subject, String text) {
        Mailer mailer = getMailer();
        mailer.setTo(to);
        mailer.setSubject(subject);
        mailer.setText(text);
        try {
            if (!mailer.sendMail()) {
                Log.error("could not end mail");
                return false;
            }
        } catch (Exception e) {
            Log.error("could not end mail", e);
            return false;
        }
        return true;
    }

    public static boolean sendHtmlMail(String to, String subject, String html) {
        Mailer mailer = getMailer();
        mailer.setTo(to);
        mailer.setSubject(subject);
        mailer.setHtml(html);
        try {
            if (!mailer.sendMail()) {
                Log.error("could not end mail");
                return false;
            }
        } catch (Exception e) {
            Log.error("could not end mail", e);
            return false;
        }
        return true;
    }

    public static Mailer getMailer() {
        Mailer mailer = new Mailer();
        mailer.setSmtpHost(Configuration.getSmtpHost());
        mailer.setSmtpPort(Configuration.getSmtpPort());
        mailer.setSmtpConnectionType(Configuration.getSmtpConnectionType());
        mailer.setSmtpUser(Configuration.getSmtpUser());
        mailer.setSmtpPassword(Configuration.getSmtpPassword());
        mailer.setFrom(Configuration.getMailSender());
        mailer.setReplyTo(Configuration.getMailSender());
        return mailer;
    }
}
