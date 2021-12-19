/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FormError {

    private List<String> formErrors = null;
    private Set<String> formFields = null;
    private boolean formIncomplete = false;

    public FormError() {
    }

    public void addFormError(String s) {
        if (formErrors == null)
            formErrors = new ArrayList<>();
        if (!formErrors.contains(s)) {
            formErrors.add(s);
        }
    }

    public void addFormField(String field) {
        if (formFields == null)
            formFields = new HashSet<>();
        formFields.add(field);
    }

    public boolean isFormIncomplete() {
        return formIncomplete;
    }

    public void setFormIncomplete() {
        this.formIncomplete = true;
    }

    public boolean isEmpty() {
        return formErrors.isEmpty() && formFields.isEmpty();
    }

    public String getFormErrorString() {
        if (formErrors == null || formErrors.isEmpty()) {
            return null;
        }
        if (formErrors.size() == 1) {
            return formErrors.get(0);
        }
        StringBuilder sb = new StringBuilder();
        for (String formError : formErrors) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(formError);
        }
        return sb.toString();
    }

    public boolean hasFormErrorField(String name) {
        if (formFields == null)
            return false;
        return formFields.contains(name);
    }

}
