package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWriteSimple extends HttpServlet {

    private static final String KIND_QUERY_NAME = "_kind";
    private static final String KEY_QUERY_NAME = "_key";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                         throws ServletException, IOException {

        PrintWriter pw = resp.getWriter();
        Map<String, String[]> params = req.getParameterMap();
        resp.setContentType("text/plain");

        if (!params.containsKey(KIND_QUERY_NAME)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            pw.println("Error : missing query parameter " + KIND_QUERY_NAME);
            return;
        }

        String entityName = req.getParameter(KIND_QUERY_NAME);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity;

        if (params.containsKey(KEY_QUERY_NAME)) {
            String id = req.getParameter(KEY_QUERY_NAME);
            entity = new Entity(entityName, id);
        } else {
            entity = new Entity(entityName);
        }

        Enumeration<String> names = req.getParameterNames();
        pw.println("Writing entity to datastore (kind : " + entityName + ")");
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String paramValue = req.getParameter(name);
            if (paramValue == null || name.equals(KIND_QUERY_NAME) || name.equals(KEY_QUERY_NAME)) {
                continue;
            }
            pw.printf(" - %s = %s\n", name, paramValue);
            entity.setProperty(name, paramValue);
        }

        datastore.put(entity);
    }
}
