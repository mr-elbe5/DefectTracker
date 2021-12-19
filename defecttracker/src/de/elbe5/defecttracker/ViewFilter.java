/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.defecttracker;

import de.elbe5.content.ContentCache;
import de.elbe5.defecttracker.defect.DefectBean;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.request.SessionRequestData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewFilter implements Comparator<DefectData> {

    public static final int TYPE_MY = 0;
    public static final int TYPE_CREATION = 1;
    public static final int TYPE_DUE_DATE = 2;
    public static final int TYPE_CLOSE_DATE = 3;
    public static final int TYPE_LOCATION = 4;
    public static final int TYPE_STATE = 5;
    public static final int TYPE_ASSIGNED = 6;
    public static final int TYPE_DESCRIPTION = 7;

    public static ViewFilter getFilter(SessionRequestData rdata){
        ViewFilter filter=rdata.getSessionObject("$filterData", ViewFilter.class);
        if (filter==null){
            filter=new ViewFilter();
            rdata.setSessionObject("$filterData",filter);
        }
        return filter;
    }

    private int sortType=TYPE_CREATION;
    private boolean ascending = false;
    private int currentUserId = 0;
    private boolean isEditor= false;
    private int projectId = 0;
    private int assignedId = 0;
    private boolean showClosed =false;

    private List<Integer> ownProjectIds=new ArrayList<>();

    public List<Integer> getOwnProjectIds() {
        return ownProjectIds;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        if (this.sortType==sortType)
            ascending=!ascending;
        else {
            this.sortType = sortType;
            ascending = true;
        }
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public boolean hasProjectReadRight(int projectId){
        return isEditor || ownProjectIds.contains(projectId);
    }

    public int getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(int assignedId) {
        this.assignedId = assignedId;
    }

    public boolean isShowClosed() {
        return showClosed;
    }

    public void setShowClosed(boolean showClosed) {
        this.showClosed = showClosed;
    }

    @Override
    public int compare(DefectData o1, DefectData o2) {
        int result=0;
        switch (sortType){
            case TYPE_CREATION:
                result = o1.getCreationDate().compareTo(o2.getCreationDate());
                break;
            case TYPE_DUE_DATE:
                result = compareLocalDates(o1.getDueDate(),o2.getDueDate());
                break;
            case TYPE_CLOSE_DATE:
                result = compareLocalDates(o1.getCloseDate(),o2.getCloseDate());
                break;
            case TYPE_LOCATION:
                result = o1.getLocationName().compareTo(o2.getLocationName());
                break;
            case TYPE_STATE:
                result = o1.getState().compareTo(o2.getState());
                break;
            case TYPE_ASSIGNED: {
                if (o1.getAssignedId()== currentUserId && o2.getAssignedId()== currentUserId)
                    result =  0;
                else if (o1.getAssignedId()== currentUserId)
                    result =  -1;
                else if (o2.getAssignedId()== currentUserId)
                    result =  1;
                else
                    result = o1.getAssignedName().compareTo(o2.getAssignedName());
                break;
            }
            case TYPE_DESCRIPTION:
                result = o1.getDescription().compareTo(o2.getDescription());
                break;
        }
        return ascending ? result : -result;
    }

    private int compareLocalDates(LocalDate date1, LocalDate date2){
        if (date1==null && date2==null)
            return 0;
        if (date1==null)
            return -1;
        if (date2==null)
            return 1;
        return date1.compareTo(date2);
    }

    public List<DefectData> getLocationDefects(int locationId){
        List<Integer> ids= DefectBean.getInstance().getLocationDefectIds(locationId);
        List<DefectData> list = ContentCache.getContents(DefectData.class);
        for (int i=list.size()-1;i>=0;i--){
            DefectData data=list.get(i);
            if (!ids.contains(data.getId())){
                list.remove(i);
                continue;
            }
            if (!showClosed && data.isClosed()){
                list.remove(i);
                continue;
            }
            if (!isEditor && data.getAssignedId()!=currentUserId){
                list.remove(i);
                continue;
            }
            if (assignedId!=0 && data.getAssignedId()!=assignedId){
                list.remove(i);
            }
        }
        list.sort(this);
        return list;
    }

    public List<DefectData> getProjectDefects(){
        List<Integer> ids= DefectBean.getInstance().getProjectDefectIds(projectId);
        List<DefectData> list = ContentCache.getContents(DefectData.class);
        for (int i=list.size()-1;i>=0;i--){
            DefectData data=list.get(i);
            if (!ids.contains(data.getId())){
                list.remove(i);
                continue;
            }
            if (!showClosed && data.isClosed()){
                list.remove(i);
                continue;
            }
            if (!isEditor && data.getAssignedId()!=currentUserId){
                list.remove(i);
                continue;
            }
            if (assignedId!=0 && data.getAssignedId()!=assignedId){
                list.remove(i);
            }
        }
        list.sort(this);
        return list;
    }
}
