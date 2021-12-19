package de.elbe5.servlet;

import de.elbe5.base.cache.Strings;
import de.elbe5.content.JspContentData;
import de.elbe5.request.*;
import de.elbe5.view.IView;
import de.elbe5.view.ContentView;
import de.elbe5.view.UrlView;

public abstract class Controller {

    public abstract String getKey();

    protected IView showHome() {
        return new UrlView("/");
    }

    protected void checkRights(boolean hasRights){
        if (!hasRights)
            throw new CmsAuthorizationException();
    }

    protected void setSaveError(SessionRequestData rdata) {
        rdata.setMessage(Strings.string("_saveError",rdata.getLocale()), SessionRequestData.MESSAGE_TYPE_ERROR);
    }

    protected IView openAdminPage(SessionRequestData rdata, String jsp, String title) {
        rdata.put(RequestData.KEY_JSP, jsp);
        rdata.put(RequestData.KEY_TITLE, title);
        return new UrlView("/WEB-INF/_jsp/administration/adminMaster.jsp");
    }

    protected IView showSystemAdministration(SessionRequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/systemAdministration.jsp", Strings.string("_systemAdministration",rdata.getLocale()));
    }

    protected IView showPersonAdministration(SessionRequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/personAdministration.jsp", Strings.string("_personAdministration",rdata.getLocale()));
    }

    protected IView showContentAdministration(SessionRequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/contentAdministration.jsp", Strings.string("_contentAdministration",rdata.getLocale()));
    }

    protected IView openJspPage(String jsp) {
        JspContentData contentData = new JspContentData();
        contentData.setJsp(jsp);
        return new ContentView(contentData);
    }
}
