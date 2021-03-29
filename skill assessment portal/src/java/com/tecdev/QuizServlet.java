package com.tecdev;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Bhupendra
 */
public class QuizServlet extends HttpServlet {

   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Step XX(a) Fetch op (operation) from request
        String op=request.getParameter("op");
        //(b) Check if it is null then redirect back to login.html
        if(op==null){
            response.sendRedirect("login.html");
            return;     //Terminate fn
        }
        //(c) Otherwise, perform switch case based on op
        switch(op){   //OCJP: String in switch is introduced in JDK7
            //Delegate/pass request, response data to UDF
            case "1": verifyUser(request,response); break;  //27-Feb-2021
            case "2": registerUser(request,response); break; //02-Mar-2021
            case "3": forgottenPassword(request, response); break; //09-Mar-2021
            case "4": startExam(request,response); break;  //Step 57, 11-Mar-2021
            case "5": saveQuestion(request,response); break;  //Step 78: 16-Mar-2021
            case "6": navigation(request,response); break;  //Step 87, 18-Mar-2021
            case "7": findQid(request, response); break;  //Step 92-20-Mar-2021
            case "8": searchQuestions(request, response); break; //Step 99-23-Mar-2021
            case "9": showQuestion(request, response); break; //Step 110-24-Mar-2021
         /*   case "7": generateCertificate(request,response); break; //14-Mar-2021
            case "8": logout(); break;  //16-Mar-2021*/
        }
            
        //PrintWriter out=response.getWriter();
        //out.println("Welcome by QuizServlet");
    }
    //Step 111: 24-Mar-2021 Coding of showQuestion
     private void showQuestion(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
         
     }
    //Step 100: 23-Mar-2021 Definition of searchQuestions
     private void searchQuestions(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
         //(a) Collect data from query Strng
         String field=request.getParameter("field");
         String operator=request.getParameter("operator");
         String value=request.getParameter("value");
         
         PrintWriter out=response.getWriter();
        //(b) Debugging purpose, display them back        
        //out.println(field + "," + operator + "," + value);
        //(c) Generate SQL select Query
        String sql=String.format("Select * from question where %s %s '%s'", field, operator, value);
        //out.print("Query=" + sql);
        //(d) Using DatabaseBean execute Query and store result in ResultSet
         ResultSet rs = null;
        try {
            rs = DatabaseBean.executeQuery(sql);
            //(e) If data found convert it into html <table>
            if (rs.next()) { //true when Data Found
                rs.previous(); //Move Back to First Row 
                String table = rsToTable(rs);  //Step 101 UDF to convert ResultSet to <table>
                out.print(table); //return back to success: fn of jQuery
                out.flush();
            } else { //No data found
                out.print("Sorry!! No Row Found");
                out.flush();
            }
        } catch (Exception e) {
            out.print("[searchQuestion] " + e.toString());
            e.printStackTrace();
        }
    }
    //Step 101-23-Mar-2021 Definition of UDF- rsToTable
       private String rsToTable(ResultSet rs) throws SQLException {
       //(a) StringBuilder - Mutable (Fast)
       StringBuilder sb = new StringBuilder(2 * 1024); //approx 2KB
        //(b) append (add to the end of buffer)
        //<table> tag of html
        sb.append("<table border='1' width='100%'>");
        //1st row is heading row
        sb.append("<thead>");
        sb.append("<td>Qid</td>");
        sb.append("<td>Question</td>");
        sb.append("<td>Category</td>");
        sb.append("<td>Option1</td>");
        sb.append("<td>Option2</td>");
        sb.append("<td>Option3</td>");
        sb.append("<td>Option4</td>");
        sb.append("<td>Answer</td>");
        sb.append("</thead>");
        //(c) For remaining rows perform loop till rs.next() true
        while (rs.next()) {
            //(d) Add new row to the <table>
            sb.append("<tr>");
            //(e)Now create Columns
            sb.append(String.format("<td>%d</td>", rs.getInt("qid")));
            sb.append(String.format("<td>%s</td>", rs.getString("question")));
            sb.append(String.format("<td>%s</td>", rs.getString("category")));
            sb.append(String.format("<td>%s</td>", rs.getString("option1")));
            sb.append(String.format("<td>%s</td>", rs.getString("option2")));
            sb.append(String.format("<td>%s</td>", rs.getString("option3")));
            sb.append(String.format("<td>%s</td>", rs.getString("option4")));
            sb.append(String.format("<td>%s</td>", rs.getString("answer")));         
            //(f) End of current row
            sb.append("</tr>");
        }
        //(g) When loop ends close table tag
        sb.append("</table>");
        //(h) return back StringBuilder as String to success: fn of JQuery
        return sb.toString(); //Return back table as a String
    }
    //Step 93: Defining UDF findQid
    private void findQid(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
        String qid = request.getParameter("qid");
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        //out.println("Qid Inside findQuestion" + qid);
        QuestionBean qb = new QuestionBean();
        if (qb.select(qid) == null) {
            out.println("-1");
            out.flush();
            return;
        }
        out.println(qb.toString());
        out.flush(); 
    }
    
  

    //Step 88: Defining UDF navigation -18-Mar-2021
     private void navigation(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
         //Step (a) Obtain index 
         String choice=request.getParameter("choice");
         //Obtain session details of current user
         HttpSession session=request.getSession(false);
         int index=Integer.parseInt(session.getAttribute("index").toString());
         ArrayList<QuestionBean>al=(ArrayList)session.getAttribute("al");
         switch(choice){
             case "1": index=0; break;//First
             case "2":  //Previous
                 --index;
                 if(index<0) index=al.size()-1; //Cycle back to last
                 break;
             case "3": //Next 
                 ++index;
                 if(index==al.size()) index=0; //Cycle back to first
                 break;
             case "4": index=al.size()-1;break;//Last
         }
         session.setAttribute("index" , index); //Update index in session
         QuestionBean q=al.get(index); //Obtain question of current index
         PrintWriter out=response.getWriter();
         out.print(q.toString());
     }
//Step 79-16-Mar-2021   [Step 81 - 18-Mar-2021 Improved Code] 
    private void saveQuestion(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
        //Obtain sql query sent by ajax
        String sql=request.getParameter("sql");
        //String sql="All is Well";
        //Debugging Purpose -16-Mar-2021
        PrintWriter out=response.getWriter();
       // out.println("Query received by Servlet=" + sql);
        //New Logic on 18-Mar-2021  -Step 81
        int rowsAffected=0;
        try{
        rowsAffected=DatabaseBean.executeUpdate(sql); 
        if(rowsAffected>0) //means row inserted or update successfully
            DatabaseBean.commit(); //Save Rows Permanently
            //Step 103 - 24-Mar-2021 Refreshing ArrayList of Questions
            //(a) load Questions of all categories without shuffle
            ArrayList<QuestionBean>al=QuestionBean.loadQuestions(null, false);
            //(b) Obtain session of current session
            HttpSession session=request.getSession(false);
            //(c) Store ArrayList into session variable
            session.setAttribute("al", al);
            out.print("Row Saved Successfully"); //return back to success fn of jQuery
        }
        catch(Exception se){
            out.print("Sorry. Failed to Save Record");
            System.out.println("Exception in saveQuestion: " + se.toString());
            se.printStackTrace();
        }
    }
//Step 58 11-Mar-2021  (Step 106-24-Mar-2021)
     private void startExam(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
         PrintWriter out=response.getWriter();
         response.setContentType("text/html");
         //out.println("Welcome, Your exam started now " + request.getParameter("category"));
         //(a) Obtain category choosen from combobox in welcome.jsp
         String category=request.getParameter("category");
         //(b) Now create ArrayList<QuestionBean> of given category, true=>shuffled
         ArrayList<QuestionBean>al=QuestionBean.loadQuestions(category, true);
         //(c) Check if no questions found (size 0)
         if(al.size()==0){
             out.println("Sorry!!! No Question Found for Category "+ category);
             out.println("<input type=button value=Back onClick='history.back()'/>");
             return;  //Terminate Fn
         }
         //Otherwise
         //(d) Obtain current session of user, false=>do not create new session
         HttpSession session=request.getSession(false);
         //(e) Store arraylist into session
         session.setAttribute("al", al);
         session.setAttribute("maxQuestions", 5);
         //(f) Set current question index to 0
         session.setAttribute("index" , 0);
         //(g) Redirect user to startexam.jsp page
         response.sendRedirect("startexam.jsp");
     }
    //4-Mar-2021 Step 36
    private void registerUser(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
	//PrintWriter out=response.getWriter();   //(*1)
 	//out.println("Inside registerUser");  //Debugging
        //(a) Collect form data
        String u=request.getParameter("userid"); //name property of <input>
        String p=request.getParameter("password");
        String q=request.getParameter("question"); //Case-Sensitive
        String a=request.getParameter("answer");
        PrintWriter out=response.getWriter();  //Debugging first
        //out.printf("%s  %s  %s %s " , u,p,q,a);
        //(b) Create instance of QuizUserBean
        QuizUserBean qb=new QuizUserBean(u,p,q,a);  //Step 37
        //New logic added on 09-Mar-2021 (Step 42)
         if(qb.exists(false))  //means only check userid and do not check password
        {
            response.sendRedirect("register_forgotten.jsp?op=2&msg='Sorry, Userid already in Use' ");
          return ;
         }
        //(c) Call insert fn of QuizUserBean class
        boolean success=qb.insert();  //Step 38
        if(success){  //if(success==true)
            //(d) Maintain state of current user using Cookie and Session
            Cookie c=new Cookie("qb" , qb.toString()); //key,string
            c.setMaxAge(24*60*60); //1 day          
            HttpSession session=request.getSession(true); //Interview 
            session.setAttribute("qb", qb); //key, object
            response.sendRedirect("welcome.jsp");
        }
        else{  //(e) 
            out.println("<b>Sorry. Unable to register User. Try Again");
            out.println("<input type='button' value='back' onclick='history.back()'/>");
        }
}
//Step 44: Dated 09-Mar-2021
private void forgottenPassword(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
	//PrintWriter out=response.getWriter();
 	//out.println("Inside forgottenPassword");  //Debugging
         //(a) Collect form data
        String u=request.getParameter("userid"); //name property of <input>
        String p=request.getParameter("password"); //means new password required
        String q=request.getParameter("question"); //Case-Sensitive
        String a=request.getParameter("answer");
        //(b) Create instance of QuizUserBean [M:Model]
        QuizUserBean qb=new QuizUserBean(u,p,q,a);  //Step 37
        //Check if userid exists, if not return back to register_forgotten
        if(!qb.exists(false))  //Means userid NOT exists
        {                       //Send back
          response.sendRedirect("register_forgotten.jsp?op=3&msg=Sorry, Userid Not Found ");
          return ;
        }
        //Otherwise, use setter fn to pass question and answer provided by user via the form
         qb.setQuestion(q); qb.setAnswer(a); //Bug Removed Using this Line
         boolean success=qb.update();  //M:Model -
         if(success){  //means password updated 
            //(d) Maintain state of current user using Cookie and Session
            Cookie c=new Cookie("qb" , qb.toString()); //key,string
            c.setMaxAge(24*60*60); //1 day          
            HttpSession session=request.getSession(true); //Interview 
            session.setAttribute("qb", qb); //key, object
            response.sendRedirect("welcome.jsp");
        }
        else{  //(e) 
            response.sendRedirect("register_forgotten.jsp?op=3&msg=sorry!!! atleast userid/question/answer mismatched");
        }
}
    //Step XXI : Coding of UDF  - verifyUser -27-Feb-2021
    //private - So that it can be called internally from within
    //another fn of the class but not from outside the class
    private void verifyUser(HttpServletRequest request, HttpServletResponse response )throws ServletException, IOException{
        //XXI (a) fetch name="userid" and name="password" from form
        String userid=request.getParameter("userid");
        String password=request.getParameter("password");
        //XXI (b) Check if it is null, redirect back to login.html
        if(userid==null || password==null){
            response.sendRedirect("login.html");
            return;
        }
        //(c) Otherwise display userid/password (Debugging only)
        //PrintWriter out=response.getWriter();
       // out.printf("Userid=%s and Password=%s" , userid, password);
        //(d) Now use M:Model to verify userid exists or not
        //Wrong way - writing that logic directly inside Servlet
        QuizUserBean qb=new QuizUserBean(userid,password); //Calls PC
        //(e) Call exists() fn of QuizUserBean
        if(qb.exists()){ //Abstraction Principle
            //Stateful state - Save data between pages
            Cookie c=new Cookie("qb" , qb.toString()); //key,string
            c.setMaxAge(24*60*60); //1 day
            //true in session means: create new session if not exist
            //if we pass false, it means - do not create a new session if not found
            HttpSession session=request.getSession(true); //Interview 
            session.setAttribute("qb", qb); //key, object
            
            //Step 84-18-Mar-2021 Additional logic for admin user
            if(qb.getUsertype().equals("A")){
                //null=>all category   false=>no shuffling
                ArrayList<QuestionBean>al=QuestionBean.loadQuestions(null, false);
                session.setAttribute("al", al); //key, value
                session.setAttribute("index", 0); //index for ArrayList
            }
            response.sendRedirect("welcome.jsp");
        }
        else{
            response.sendRedirect("login.html");
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
