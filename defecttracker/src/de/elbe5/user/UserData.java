/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.user;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.cache.Strings;
import de.elbe5.application.Configuration;
import de.elbe5.group.GroupData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.rights.SystemZone;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class UserData extends BaseData {

    public static final int ID_ROOT = 1;

    public static int MAX_PORTRAIT_WIDTH = 200;
    public static int MAX_PORTRAIT_HEIGHT = 200;

    public static int MIN_LOGIN_LENGTH = 4;
    public static int MIN_PASSWORD_LENGTH = 8;

    protected String title = "";
    protected String firstName = "";
    protected String lastName = "";
    protected String email = "";
    protected String login = "";
    protected String passwordHash = "";
    protected String token = "";
    protected LocalDateTime tokenExpiration = null;
    protected boolean locked = false;
    protected boolean deleted = false;
    protected String street = "";
    protected String zipCode = "";
    protected String city = "";
    protected String country = "";
    protected String phone = "";
    protected String fax = "";
    protected String mobile = "";
    protected String notes = "";
    protected boolean hasPortrait=false;
    protected byte[] portrait = null;

    protected Set<Integer> groupIds = new HashSet<>();

    protected Set<SystemZone> systemRights = new HashSet<>();

    // base data

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        if (firstName.length() == 0) {
            return lastName;
        }
        return firstName + ' ' + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean hasPassword() {
        return !passwordHash.isEmpty();
    }

    public void setPassword(String password) {
        if (password.isEmpty()) {
            setPasswordHash("");
        } else {
            setPasswordHash(UserSecurity.encryptPassword(password, Configuration.getSalt()));
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(LocalDateTime tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    protected List<GroupData> groups = new ArrayList<>();

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean hasPortrait() {
        return hasPortrait;
    }

    public void setHasPortrait(boolean hasPortrait) {
        this.hasPortrait = hasPortrait;
    }

    public byte[] getPortrait() {
        return portrait;
    }

    public void setPortrait(byte[] portrait) {
        this.portrait = portrait;
    }

    public void clearSystemRights(){
        systemRights.clear();
    }

    public void addSystemRight(SystemZone zone) {
        systemRights.add(zone);
    }

    public boolean hasAnySystemRight() {
        return !systemRights.isEmpty() || isRoot();
    }

    public boolean hasAnyElevatedSystemRight() {
        return hasSystemRight(SystemZone.APPLICATION) || hasSystemRight(SystemZone.USER) || hasSystemRight(SystemZone.CONTENTEDIT);
    }

    public boolean hasSystemRight(SystemZone zone) {
        return systemRights.contains(zone) || isRoot();
    }

    public boolean hasAnyContentRight() {
        return hasSystemRight(SystemZone.CONTENTEDIT) ||
                hasSystemRight(SystemZone.SPECIFICCONTENTEDIT);
    }

    public boolean isRoot(){
        return getId()== ID_ROOT;
    }

    public Set<Integer> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Integer> groupIds) {
        this.groupIds = groupIds;
    }

    public List<GroupData> getGroups() {
        return groups;
    }

    // multiple data

    private void readBasicData(SessionRequestData rdata) {
        setTitle(rdata.getString("title"));
        setFirstName(rdata.getString("firstName"));
        setLastName(rdata.getString("lastName"));
        setEmail(rdata.getString("email"));
        setStreet(rdata.getString("street"));
        setZipCode(rdata.getString("zipCode"));
        setCity(rdata.getString("city"));
        setCountry(rdata.getString("country"));
        setPhone(rdata.getString("phone"));
        setFax(rdata.getString("fax"));
        setMobile(rdata.getString("mobile"));
        setNotes(rdata.getString("notes"));
        BinaryFile file = rdata.getFile("portrait");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            try {
                BufferedImage source = ImageUtil.createImage(file.getBytes(), file.getContentType());
                if (source != null) {
                    float factor = ImageUtil.getResizeFactor(source, MAX_PORTRAIT_WIDTH, MAX_PORTRAIT_HEIGHT, true);
                    BufferedImage image = ImageUtil.copyImage(source, factor);
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                    ImageWriter writer = writers.next();
                    setPortrait(ImageUtil.writeImage(writer, image));
                }
            } catch (IOException e) {
                Log.error("could not create portrait", e);
            }
        }
    }

    private void checkBasics(SessionRequestData rdata) {
        if (lastName.isEmpty())
            rdata.addIncompleteField("lastName");
        if (email.isEmpty())
            rdata.addIncompleteField("email");
    }

    public void readSettingsRequestData(SessionRequestData rdata) {
        readBasicData(rdata);
        setLogin(rdata.getString("login"));
        setPassword(rdata.getString("password"));
        setGroupIds(rdata.getIntegerSet("groupIds"));
        if (login.isEmpty())
            rdata.addIncompleteField("login");
        if (isNew() && !hasPassword())
            rdata.addIncompleteField("password");
        checkBasics(rdata);
    }

    public void readProfileRequestData(SessionRequestData rdata) {
        readBasicData(rdata);
        checkBasics(rdata);
    }

    public void readRegistrationRequestData(SessionRequestData rdata) {
        readBasicData(rdata);
        Locale locale = rdata.getLocale();
        setLogin(rdata.getString("login"));
        String password1 = rdata.getString("password1");
        String password2 = rdata.getString("password2");
        checkBasics(rdata);
        if (login.isEmpty())
            rdata.addIncompleteField("login");
        if (login.length() < UserData.MIN_LOGIN_LENGTH) {
            rdata.addFormField("login");
            rdata.addFormError(Strings.string("_loginLengthError",locale));
        }
        if (password1.length() < UserData.MIN_PASSWORD_LENGTH) {
            rdata.addFormField("password1");
            rdata.addFormError(Strings.string("_passwordLengthError",locale));
        } else if (!password1.equals(password2)) {
            rdata.addFormField("password2");
            rdata.addFormError(Strings.string("_passwordsDontMatch",locale));
        } else
            setPassword(password1);
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJsonShort(Locale locale){
        JSONObject json = new JSONObject();
        json.put("id",getId());
        json.put("name",getName());
        return json;
    }

}
