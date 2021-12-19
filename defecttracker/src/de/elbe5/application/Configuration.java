/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.base.mail.Mailer;

import javax.servlet.ServletContext;
import java.util.*;

public class Configuration {

    public static String ENCODING = "UTF-8";

    private static String appTitle = "";
    private static String salt = "";
    private static String smtpHost = null;
    private static int smtpPort = 25;
    private static Mailer.SmtpConnectionType smtpConnectionType = Mailer.SmtpConnectionType.plain;
    private static String smtpUser = "";
    private static String smtpPassword = "";
    private static String mailSender = null;
    private static String mailReceiver = null;
    private static int timerInterval = 30;
    private static Locale defaultLocale = Locale.GERMAN;
    private static Map<String,Locale> locales = new HashMap<>();

    private static boolean showInactiveContent = false;

    static{
        locales.put("de",Locale.GERMAN);
        locales.put("en",Locale.ENGLISH);
    }

    // base data

    public static String getAppTitle() {
        return appTitle;
    }

    public static void setAppTitle(String appTitle) {
        Configuration.appTitle = appTitle;
    }

    public static String getSalt() {
        return salt;
    }

    public static void setSalt(String salt) {
        Configuration.salt = salt;
    }

    public static String getSmtpHost() {
        return smtpHost;
    }

    public static void setSmtpHost(String smtpHost) {
        Configuration.smtpHost = smtpHost;
    }

    public static int getSmtpPort() {
        return smtpPort;
    }

    public static void setSmtpPort(int smtpPort) {
        Configuration.smtpPort = smtpPort;
    }

    public static Mailer.SmtpConnectionType getSmtpConnectionType() {
        return smtpConnectionType;
    }

    public static void setSmtpConnectionType(Mailer.SmtpConnectionType smtpConnectionType) {
        Configuration.smtpConnectionType = smtpConnectionType;
    }

    public static String getSmtpUser() {
        return smtpUser;
    }

    public static void setSmtpUser(String smtpUser) {
        Configuration.smtpUser = smtpUser;
    }

    public static String getSmtpPassword() {
        return smtpPassword;
    }

    public static void setSmtpPassword(String smtpPassword) {
        Configuration.smtpPassword = smtpPassword;
    }

    public static String getMailSender() {
        return mailSender;
    }

    public static void setMailSender(String mailSender) {
        Configuration.mailSender = mailSender;
    }

    public static String getMailReceiver() {
        return mailReceiver;
    }

    public static void setMailReceiver(String mailReceiver) {
        Configuration.mailReceiver = mailReceiver;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(Locale locale) {
        if (locale == null || !locales.containsValue(locale))
            return;
        defaultLocale = locale;
    }

    public static Collection<Locale> getLocales() {
        return locales.values();
    }

    public static Locale getLocale(String language) {
        return locales.get(language);
    }

    public static boolean hasLanguage(Locale locale) {
        return locales.containsValue(locale);
    }

    public static boolean hasLanguage(String language) {
        return locales.containsKey(language);
    }

    public static int getTimerInterval() {
        return timerInterval;
    }

    public static void setTimerInterval(int timerInterval) {
        Configuration.timerInterval = timerInterval;
    }

    public static boolean isShowInactiveContent() {
        return showInactiveContent;
    }

    public static void setShowInactiveContent(boolean showInactiveContent) {
        Configuration.showInactiveContent = showInactiveContent;
    }

    // read from config file

    private static String getSafeInitParameter(ServletContext servletContext, String key){
        String s=servletContext.getInitParameter(key);
        return s==null ? "" : s;
    }

    public static void setConfigs(ServletContext servletContext) {
        setSalt(getSafeInitParameter(servletContext,"salt"));
        setSmtpHost(getSafeInitParameter(servletContext,"mailHost"));
        setSmtpPort(Integer.parseInt(getSafeInitParameter(servletContext,"mailPort")));
        setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(getSafeInitParameter(servletContext,"mailConnectionType")));
        setSmtpUser(getSafeInitParameter(servletContext,"mailUser"));
        setSmtpPassword(getSafeInitParameter(servletContext,"mailPassword"));
        setMailSender(getSafeInitParameter(servletContext,"mailSender"));
        setMailReceiver(getSafeInitParameter(servletContext,"mailReceiver"));
        setTimerInterval(Integer.parseInt(getSafeInitParameter(servletContext,"timerInterval")));
        String language = getSafeInitParameter(servletContext,"defaultLanguage");
        try {
            setDefaultLocale(new Locale(language));
        } catch (Exception ignore) {
        }
        System.out.println("default locale is "+ getDefaultLocale().getDisplayName());
    }

}
