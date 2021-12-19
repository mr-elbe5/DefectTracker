package de.elbe5.defecttracker.defect;

import de.elbe5.file.DocumentData;

public class DefectCommentDocumentData extends DocumentData {

    private int commentId=0;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

}
