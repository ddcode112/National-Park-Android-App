package ds.nationalpark;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Apr 5, 2023
 *
 * This servlet is acting as the controoler.
 * There are five views.
 * 1. Without search parameters - index.jsp
 * 2. With search parameters - result.jsp (json string)
 * 3. Dashboard with operations analytics and search logs - dashboard.jsp
 * 4. List of topics - topics.jsp (json string)
 * 5. List of states - states.jsp (json string)
 * The model is provided by NationalParkModel.
 */

import ds.nationalpark.NationalParkModel;
import java.io.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.json.JSONArray;

@WebServlet(name = "NationalParkServlet",
        urlPatterns = {"/NationalPark", "/getDashBoard", "/getTopics", "/getStates"})
public class NationalParkServlet extends HttpServlet {
    // The business model for this app.
    NationalParkModel npm = null;

    /**
     * Initiate this servlet by instantiating the model that it will use.
     */
    @Override
    public void init() {
        npm = new NationalParkModel();
    }

    /**
     * Reply to HTTP GET requests.
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Get servlet path
        String path = request.getServletPath();
        // Refer to Lab 2
        // Determine what type of device our user is.
        String ua = request.getHeader("User-Agent");

        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }
        String nextView;
        if (path.equals("/getDashBoard")) { // Dashboard view
            npm.setDashboard();
            request.setAttribute("records", npm.getRecords());
            request.setAttribute("top3topics", npm.getTop3Topics());
            request.setAttribute("top3states", npm.getTop3States());
            request.setAttribute("minFee", npm.getMinFee());
            request.setAttribute("maxFee", npm.getMaxFee());
            nextView = "dashboard.jsp";
        } else if (path.equals("/getTopics")) { // List of topics
            request.setAttribute("topicList", npm.getTopicList());
            nextView = "topics.jsp";
        } else if (path.equals("/getStates")) { // List of states
            request.setAttribute("stateList", npm.getStateList());
            nextView = "states.jsp";
        } else {
            String topic = request.getParameter("topic");
            String stateCode = request.getParameter("stateCode");
            String q = request.getParameter("q");
            if (topic != null && stateCode != null) { // Search View with parameters
                String parkInfo = npm.search(topic, stateCode, q);
                JSONArray parkInfoArray = new JSONArray(parkInfo);
                request.setAttribute("parkInfoArray", parkInfoArray);
                nextView = "result.jsp";
            } else { // Initial View
                request.setAttribute("topicList", npm.getTopicList());
                request.setAttribute("stateList", npm.getStateList());
                nextView = "index.jsp";
            }
        }

        // Transfer control over the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);


    }

    public void destroy() {
    }
}