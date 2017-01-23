/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hub;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Michael
 */
public class HubServlet extends HttpServlet {
    
    private final static String DB_PATH = "G:\\Hub WebApp\\HubDatabase.accdb";
    private final static String DB_URL = "jdbc:ucanaccess://" + DB_PATH;
    
    protected static Connection connection;  
    private static RequestDispatcher dispatcher = null;
    
    @Override
    public void init(){
        try{
            //Create database connection
            //TODO: Replace this with connection pool
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection(DB_URL);
        }
        catch (ClassNotFoundException | SQLException ex){}
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Choose the action based on the page sending the request
        //Each form on a page has a hidden input with name="page" and value="(page's name)"
        switch (request.getParameter("page")){
            case "login":
                //Attempt to log in. If it succeeds, forward to the menu. If not, display error.
                if (login(request)){
                    if (request.getSession().getAttribute("usertype").equals("technician")){
                        dispatcher = request.getRequestDispatcher("main.jsp");
                    }
                    else{
                        dispatcher = request.getRequestDispatcher("adminmain.jsp");
                    }
                    dispatcher.forward(request, response);
                }
                else{
                    dispatcher = request.getRequestDispatcher("login.jsp");
                    dispatcher.include(request, response);
                }
                break;
            case "main":
                switch (request.getParameter("selection")){
                    case "document":
                        setDocAttributes(request);
                        request.setAttribute("pagecontent", createDocumentation(request));
                        dispatcher = request.getRequestDispatcher("documentation.jsp");
                        dispatcher.forward(request, response);
                        break;
                }
                break;
            case "adminmain":
                switch (request.getParameter("selection")){
                    case "createuser":
                        dispatcher = request.getRequestDispatcher("createuser.jsp");
                        dispatcher.forward(request, response);
                        break;
                    case "query":
                        //Admin querying code
                        break;
                }
                break;
            case "createuser":
                if (addUser(request)){
                    request.setAttribute("successmessage", "User Successfully Added.");
                    request.setAttribute("successredirect", "adminmain.jsp");
                    dispatcher = request.getRequestDispatcher("success.jsp");
                    dispatcher.forward(request, response);
                }
                else{
                    dispatcher = request.getRequestDispatcher("createuser.jsp");
                    dispatcher.include(request, response);
                }
                
                break;
            case "documentation":
                switch (request.getParameter("submit")){
                    case "Change":
                        setDocAttributes(request);
                        request.setAttribute("pagecontent", createDocumentation(request));
                        dispatcher = request.getRequestDispatcher("documentation.jsp");
                        dispatcher.forward(request, response);
                        break;
                    case "Lookup":
                        String[] studentInfo = getStudentInfo(900000000 + Long.valueOf(request.getParameter("tbStudentID")));
                        request.setAttribute("tbStudentID", request.getParameter("tbStudentID"));
                        if (studentInfo == null){
                            request.setAttribute("lookupmessage", "ID Not Found");
                        }
                        else{
                            request.setAttribute("tbFirstName", studentInfo[0]);
                            request.setAttribute("tbLastName", studentInfo[1]);
                            request.setAttribute("tbPhone", studentInfo[2]);
                            request.setAttribute("tbEmail", studentInfo[3]);
                            request.setAttribute("cbStaff", studentInfo[4].equals("true")? "checked" : "");
                        }
                        
                        setDocAttributes(request);
                        request.setAttribute("pagecontent", createDocumentation(request));
                        dispatcher = request.getRequestDispatcher("documentation.jsp");
                        dispatcher.forward(request, response);
                        break;
                    case "Submit":
                        if (addStudent(request)){
                            request.setAttribute("successmessage", "Submission Successful.");
                            request.setAttribute("successredirect", request.getSession().getAttribute("usertype").equals("admin") ? "adminmain.jsp" : "main.jsp");
                            dispatcher = request.getRequestDispatcher("success.jsp");
                            dispatcher.forward(request, response);
                        }
                        else{
                            setDocAttributes(request);
                            request.setAttribute("pagecontent", createDocumentation(request));
                            dispatcher = request.getRequestDispatcher("documentation.jsp");
                            dispatcher.forward(request, response);
                        }
                        break;
                }
                break;
        }
    }
    
    public static boolean login(HttpServletRequest request){
        ResultSet userInfo = getUserInfo(request.getParameter("tbUsername"));
        try{
            if(userInfo.next()){
                if (PasswordHash.validatePassword(request.getParameter("tbPassword"), 
                    userInfo.getString("Password"))){
                    request.getSession().setAttribute("loginmessage", "");
                    boolean administrator = userInfo.getBoolean("Admin");
                    if (!administrator){
                        request.getSession().setAttribute("usertype", "admin");
                        return true;
                    }
                    else{
                        request.getSession().setAttribute("usertype", "admin");
                        return true;
                    }
                }
                else{
                    request.setAttribute("username", request.getParameter("tbUsername"));
                    request.setAttribute("loginmessage", "*Incorrect Password.");
                    return false;
                }
            }
            else{
                request.setAttribute("username", request.getParameter("tbUsername"));
                request.setAttribute("loginmessage", "*Incorrect Username.");
                return false;
            }
        }
        catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex){}
        request.setAttribute("loginmessage", "Error logging in.");
        return false;
    }
    
    public static ResultSet getUserInfo(String username){
        final String userQuery = 
                "Select *"
                + " FROM Accounts"
                + " WHERE Username = ?;";
        try{
            PreparedStatement queryStatement = connection.prepareStatement(userQuery);
            queryStatement.setString(1, username);
            return queryStatement.executeQuery();
        }
        catch (SQLException ex){
            
        }
        return null;
    }
    
    public boolean addUser(HttpServletRequest request){
        String username = request.getParameter("tbUsername");
        String password = request.getParameter("tbPassword");
        
        if (username.equals("")){
            request.setAttribute("createusermessage", "A username is required."); //Create error message
            return false;
        }
        else if (password.equals("")){
            //Save the sent username as a session attribute so the user doesn't have to re-enter in case of error
            request.setAttribute("sentusername", username);
            request.setAttribute("createusermessage", "A password is required."); //Create error message
            return false;
        }
        else{        
            final String userInsert = 
                    "INSERT INTO Accounts"
                    + " VALUES (?, ?, ?);";
            
            //Save the sent password as a session attribute so the user doesn't have to re-enter in case of error
            request.setAttribute("sentpassword", password);
            try{

                if (!getUserInfo(username).next()){
                    try{
                        PreparedStatement insertStatement = connection.prepareStatement(userInsert);
                        insertStatement.setBoolean(1, request.getParameter("cbAdmin") == null);
                        insertStatement.setString(2, username);
                        try{
                            insertStatement.setString(3, PasswordHash.createHash(password));
                            insertStatement.executeUpdate();
                            
                            request.setAttribute("createusermessage", "");
                            return true;
                        }
                        catch(Exception ex){
                            request.setAttribute("createusermessage", "Exception thrown when hashing password."); //Create error message
                            return false;
                        }

                    }
                    catch(SQLException ex){
                        request.setAttribute("createusermessage", "SQLException thrown."); //Create error message
                        return false;
                    }
                }
                else{
                    request.setAttribute("createusermessage", "Username taken."); //Create error message
                    return false;
                }
            }
            catch(SQLException ex){

            }
        }
        request.setAttribute("createusermessage", "Unknown error."); //Create error message
        return false;
    }
    
    private static String[] getStudentInfo(long studentID){
        final String studentQuery = 
                "SELECT FirstName, LastName, Phone, Email, Staff "
                + "FROM Students "
                + "WHERE StudentID = ?;";
        String[] studentInfo = {"","","","",""};
        try{
            PreparedStatement studentQueryStatement = connection.prepareStatement(studentQuery);
            studentQueryStatement.setLong(1, studentID);
            
            ResultSet results = studentQueryStatement.executeQuery();
            if (results.next()){
                for (int index = 0; index < 4; index++){
                    if (results.getString(index + 1) != null){
                        studentInfo[index] = results.getString(index + 1);
                    }
                    else{
                        studentInfo[index] = "";
                    }
                }
                studentInfo[4] = results.getBoolean(5) ? "true" : "false";
            }
            else{
                studentInfo = null;
            }
        }
        catch (SQLException ex){
            
        }
        return studentInfo;
    }
    
    private static boolean addStudent(HttpServletRequest request){
        final String studentInsertQuery = 
                "INSERT INTO "
                + "Students (StudentID, FirstName, LastName, Email, Phone, Staff) "
                + "VALUES (?, ?, ?, ?, ?, ?);";
        final String blankIDQuery = 
                "SELECT MAX(StudentID) "
                + "FROM "
                + "(SELECT StudentID "
                + "FROM Students "
                + "WHERE StudentID < 900000000);";
        long studentID = -1;
        
        if (!isDataValid(request)){
            return false;
        }
        
        String studentIDString = request.getParameter("tbStudentID");
        if (studentIDString == null || studentIDString.equals("")){
            try{
                PreparedStatement blankQueryStatement = connection.prepareStatement(blankIDQuery);
                ResultSet results = blankQueryStatement.executeQuery();
                if(results.next()){
                    studentID = results.getLong(1) + 1;
                }
                else{
                    studentID = 000000000;
                }
            }
            catch (SQLException ex){
                
            }
        }
        else{
            studentID = 900000000 + Integer.valueOf(studentIDString);
        }
        
        String firstName = request.getParameter("tbFirstName");
        String lastName = request.getParameter("tbLastName");
        String email = request.getParameter("tbEmail");
        String phoneNumber = request.getParameter("tbPhone");
        boolean staff = request.getParameter("cbStaff") != null;
        try{
            PreparedStatement statement = connection.prepareStatement(studentInsertQuery);

            statement.setLong(1, studentID);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, email);
            statement.setString(5, phoneNumber);
            statement.setBoolean(6, staff);
            statement.executeUpdate();
        }
        catch (SQLException ex){
        }
        return addVisit(request, studentID);
    }
    
    private static boolean isDataValid(HttpServletRequest request){
        String studentIDString = request.getParameter("tbStudentID");
        if (studentIDString != null && !studentIDString.equals("") && !studentIDString.matches("[0-9]{6}")){
            request.setAttribute("submitmessage", "Error: Invalid 900- ID Number");
            return false;
        }
        
        String firstName = request.getParameter("tbFirstName");
        if (firstName == null || firstName.equals("")){
            request.setAttribute("submitmessage", "Error: First Name is Required");
            return false;
        }
        
        String lastName = request.getParameter("tbLastName");
        if (lastName == null || lastName.equals("")){
            request.setAttribute("submitmessage", "Error: Last Name is Required");
            return false;
        }
        
        String phoneNumber = request.getParameter("tbPhone");
        if (phoneNumber != null && !phoneNumber.equals("") && !phoneNumber.matches("[0-9]{10}")){
            request.setAttribute("submitmessage", "Error: Invalid Phone Number");
            return false;
        }
        
        String deviceNumberString = request.getParameter("tbDeviceNumber");
        String device;
        String problem;
        String procedure;
        String technician;
        
        if (deviceNumberString != null && !deviceNumberString.equals("")){
            int deviceNumber = Integer.valueOf(deviceNumberString);
            for (int index = 1; index < deviceNumber + 1; index++){
                device = request.getParameter("tbDevice" + index);
                if (device == null || device.equals("")){
                    request.setAttribute("submitmessage", "Error: Device Field " + index + " Cannot Be Blank");
                    return false;
                }
                
                problem = request.getParameter("taProblem" + index);
                if (problem == null || problem.equals("")){
                    request.setAttribute("submitmessage", "Error: Problem Field " + index + " Cannot Be Blank");
                    return false;
                }
                
                procedure = request.getParameter("taProcedure" + index);
                if (procedure == null || procedure.equals("")){
                    request.setAttribute("submitmessage", "Error: Procedure Field " + index + " Cannot Be Blank");
                    return false;
                }
                
                technician = request.getParameter("tbTechnician" + index);
                if (technician == null || technician.equals("")){
                    request.setAttribute("submitmessage", "Error: Technician Field " + index + " Cannot Be Blank");
                    return false;
                }
            }   
        }
        return true;
    }
    
    private static boolean addVisit(HttpServletRequest request, long studentID){
        final String visitQuery = "SELECT MAX(VisitNumber) FROM Visits WHERE StudentID = ?;";
        final String visitInsertQuery = 
                "INSERT INTO "
                + "Visits (StudentID, VisitNumber) "
                + "VALUES (?, ?);";
        int thisVisitNumber = 1;
        
        try{
            PreparedStatement queryStatement = connection.prepareStatement(visitQuery);
            queryStatement.setLong(1, studentID);
            ResultSet queryResults = queryStatement.executeQuery();
            
            if (queryResults != null && queryResults.next()){
                thisVisitNumber = queryResults.getInt(1);
                thisVisitNumber++;
            }
            
            PreparedStatement insertStatement = connection.prepareStatement(visitInsertQuery);
            insertStatement.setLong(1, studentID);
            insertStatement.setInt(2, thisVisitNumber);
            insertStatement.executeUpdate();
            
            return addServices(request, studentID);
        }
        catch (SQLException ex){
        }
        request.setAttribute("submitmessage", "Error Adding Visit to Database");
        return false;
    }
    
    private static boolean addServices(HttpServletRequest request, long studentID){
        final String visitQuery = "SELECT MAX(VisitID) FROM Visits WHERE StudentID = ?;";
        final String serviceInsertQuery = 
                "INSERT INTO "
                + "Services (VisitID, Device, Model, Problem, Procedure, Instructed, Resolved, Technician) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        int visitID = -9999;
        
        try{
            PreparedStatement visitQueryStatement = connection.prepareStatement(visitQuery);
            visitQueryStatement.setLong(1, studentID);
            ResultSet queryResults = visitQueryStatement.executeQuery();
                    
            if (queryResults != null && queryResults.next()){
                visitID = queryResults.getInt(1);
            }
            
            for (int index = 1; index > 0; index++){
                if (request.getParameter("taProblem" + index) == null ||
                        request.getParameter("taProblem" + index).equals("")){
                    break;
                }
                PreparedStatement insertStatement = connection.prepareStatement(serviceInsertQuery);
                insertStatement.setInt(1, visitID);
                insertStatement.setString(2, request.getParameter("tbDevice" + index));
                insertStatement.setString(3, request.getParameter("tbModel" + index));
                insertStatement.setString(4, request.getParameter("taProblem" + index));
                insertStatement.setString(5, request.getParameter("taProcedure" + index));
                insertStatement.setBoolean(6, request.getParameter("cbInstructed" + index) != null);
                insertStatement.setBoolean(7, request.getParameter("cbResolved" + index) != null);
                insertStatement.setString(8, request.getParameter("tbTechnician" + index));
                
                insertStatement.executeUpdate();
            }
            return true;
        }
        catch (SQLException ex){
            
        }
        request.setAttribute("submitmessage", "Error Adding Services to Database");
        return false;
    }
    
    private static void setDocAttributes(HttpServletRequest request){
        final String[] docParams = {"tbStudentID", "cbStaff", "tbFirstName", "tbLastName", "tbPhone", "tbEmail", "lookupmessage",
            "tbDevice", "tbModel", "taProblem", "taProcedure", "cbInstructed", "cbResolved", "tbTechnician"};
        
        int index = 0;
        int devices = 1;
        String deviceString = request.getParameter("tbDeviceNumber");
        if (deviceString != null && deviceString.matches("[0-9]+")){
            devices = Integer.valueOf(deviceString);
            if (devices < 1){
                devices = 1;
            }
        }
        request.setAttribute("tbDeviceNumber", devices);
        
        for (String param : docParams){
            if (index < 7){
                if (request.getAttribute(param) == null){
                    if (!param.startsWith("cb")){
                        request.setAttribute(param, request.getParameter(param) != null ? request.getParameter(param) : "");
                    }
                    else{
                        request.setAttribute(param, request.getParameter(param) != null ? "checked" : "");
                    }
                }
            }
            else{
                for (int index2 = 1; index2 < devices + 1; index2++){
                    if (request.getAttribute(param + index2) == null){
                        if (!param.startsWith("cb")){
                            request.setAttribute(param + index2, request.getParameter(param + index2) != null ? request.getParameter(param + index2) : "");
                        }
                        else{
                            request.setAttribute(param + index2, request.getParameter(param + index2) != null ? "checked" : "");
                        }
                    }
                }
            }
            index++;
        }
        request.setAttribute("submitmessage", request.getAttribute("submitmessage") == null ? "" : request.getAttribute("submitmessage"));
    }
    
    private static String createDocumentation(HttpServletRequest request){
        int devices = Integer.valueOf(request.getAttribute("tbDeviceNumber").toString());
        StringBuilder builder = new StringBuilder();
        
        builder.append(
            "<head>\n" + 
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"master.css\">" +
                "<style>\n" +
                    
                    "p{\n" +
                        "margin: 10px auto 10px 15px;\n" +
                    "}\n" +

                    "div.header{\n" +
                        "min-width: calc(100% - 6px);\n" +
                    "}\n" +

                    "div.footer{\n" +
                        "border: double;" +
                        "min-width: calc(100% - 6px);\n" +
                        "clear: both;" +
                    "}\n" +

                    "div.footer button{\n" +
                        "margin: 5px auto 5px 20px;" +
                    "}\n" +
                    
                    "div.container{\n" +
                        "text-align: center;\n" +
                        "background-color: rgb(211, 227, 229);\n" +
                        "width: 100%;" +
                        "min-width: 475px;" +
                    "}\n" +

                    "div.studentinfo{\n" +
                        "border: double;\n" +
                    "}\n" +
                    
                    "div.row{\n" +
                        "width: 100%" +
                        "clear: both;" +
                    "}\n" +

                    "div.deviceinfo{\n" +
                        "border: double;\n" +
                        "float: left;" +
                        "width: calc(" + 100 / Math.min(2, devices) + "% - 6px);" +
                    "}\n" +

                    "text.name{\n" +
                        "width: 175px;\n" +
                    "}\n" +

                    "input{\n" +
                        "margin: auto 20px auto auto;\n" +
                    "}\n" +

                    "input.lookup{\n" +
                        "margin: auto 50px auto -15px;\n" +
                    "}\n" +

                    "textarea{\n" +
                        "margin: 0 2.5% 0 2.5%;\n" +
                        "width: calc(95% - 6px)" +
                    "}\n" +
                                
                    "textarea.problem{\n" +
                        "height: 30px;" +
                    "}\n" +
                                
                    "textarea.procedure{\n" +
                        "height: 60px;" +
                    "}\n" +
                "</style>" + 
                "<title>Documentation</title>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">\n" + 
                    "<div class=\"header\">\n" +
                        "<form id=\"Documentation\" action=\"HubServlet\" method=\"POST\">\n" +
"                           <h1>Documentation</h1>\n" +
"                               <input type=\"hidden\" name=\"page\" value=\"documentation\"/>\n" +
"                               <p>\n" +
"                                   Number of Devices: \n" +
"                                   <input type=\"text\" name=\"tbDeviceNumber\" maxlength=\"1\" size=\"2\" value=\"" + request.getAttribute("tbDeviceNumber") + "\"/>\n" +
"                                   <input type=\"submit\" name=\"submit\" value=\"Change\" tabindex=\"998\"/>\n" +
"                               </p>\n" +
"                       </div>" +
                        "<div class=\"studentinfo\">\n" +
        "                    <p>\n" +
        "                        Student ID: 900<input type=\"text\" class=\"lineone\" maxlength=\"6\" style=\"width: 45px;\" name=\"tbStudentID\" value=\"" + request.getAttribute("tbStudentID") + "\"/>\n" +
        "                        <input class=\"lookup\" type=\"submit\" name=\"submit\" value=\"Lookup\" tabindex=\"999\"/>\n" +
        "                        <label for=\"cbStaff\">Staff?</label> "
                                    + "<input type=\"checkbox\" id=\"cbStaff\" name=\"cbStaff\" " + request.getAttribute("cbStaff") + "/>\n" +
        "                    </p>\n" +
        "                    <p>\n" +
        "                        First Name: <input type=\"text\" class=\"name\" style=\"width: 90px;\" name=\"tbFirstName\" value=\"" + request.getAttribute("tbFirstName") + "\"/>\n" +
        "                        Last Name: <input type=\"text\" class=\"name\" style=\"width: 90px;\" name=\"tbLastName\" value=\"" + request.getAttribute("tbLastName") + "\"/>\n" +
        "                    </p>\n" +
        "                    <p>\n" +
        "                        Phone: <input type=\"text\" maxlength=\"10\" style=\"width: 70px;\" name=\"tbPhone\" value=\"" + request.getAttribute("tbPhone") + "\"/>\n" +
        "                        E-mail: <input type=\"email\" style=\"width: 175px;\" name=\"tbEmail\" value=\"" + request.getAttribute("tbEmail") + "\"/>\n" +
        "                    </p>\n" +
                            "<p>" + request.getAttribute("lookupmessage") + "</p>" +
        "                </div>" +
                        "<div class=\"row\">"
        );
            for (int index = 1; index <= devices; index++){
            builder.append(
                        "<div class=\"deviceinfo\">\n" +
        "                    <p>Device: "
                                + "<input type=\"text\" style=\"width: 90px;\" name=\"tbDevice" + index + "\" value=\"" + request.getAttribute("tbDevice" + index) + "\"/></p>\n" +
        "                    <p>Make/Model: "
                                + "<input type=\"text\" style=\"width: 90px;\" name=\"tbModel" + index + "\" value=\"" + request.getAttribute("tbModel" + index) + "\"/></p>\n" +
        "                    <p class=\"centerunderline\">Problem</p>\n" +
        "                    <textarea class=\"problem\" name=\"taProblem" + index + "\">" + request.getAttribute("taProblem" + index) + "</textarea>\n" +
        "                    <p class=\"centerunderline\">Procedure</p>\n" +
        "                    <textarea class=\"procedure\" name=\"taProcedure" + index + "\">" + request.getAttribute("taProcedure" + index) + "</textarea>\n" +
        "                    <p>\n" +
        "                        <label for=\"cbInstructed" + index + "\"> Instructed? </label>"
                                + "<input type=\"checkbox\" id=\"cbInstructed" + index + "\" name=\"cbInstructed" + index + "\" value=\"" + request.getAttribute("cbInstructed" + index) + "\"/>\n" +
        "                        <label for=\"cbResolved" + index + "\"> Resolved? </label>"
                                + "<input type=\"checkbox\" id=\"cbResolved" + index + "\" name=\"cbResolved" + index + "\" value=\"" + request.getAttribute("cbResolved" + index) + "\"/>\n" +
        "                    </p>\n" +
                             "<p>\n"
                                + "Technician: <input type=\"text\" maxlength=\"3\" style=\"width: 40px;\" name=\"tbTechnician" + index + "\" value=\"" + request.getAttribute("tbTechnician" + index) + "\"/>"
                                + "\n</p>" +
        "                </div>");
            if (devices != index && index % 2 == 0){
                builder.append(
                        "</div>"
                        + "<div class=\"row\">");
            }
        }
        builder.append(    
                        "</div>" +
                        "<div class=\"footer\">\n" +
"                           <p><button type=\"reset\">Clear</button>\n" +
"                           <input type=\"submit\" name=\"submit\" value=\"Submit\"/></p>\n" +
"                           <p><label>" + request.getAttribute("submitmessage") + "</label></p>" +
                        "</div>" + 
                    "</form>" +
                "</div class=\"container\">" +
            "</body>");

        return builder.toString();
    }
}
