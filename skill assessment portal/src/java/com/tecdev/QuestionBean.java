package com.tecdev;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionBean {
    //Members same as column names in database table
private int qid;
private String question, category, option1, option2, option3, option4;
private String answer;  //Means actual answer
private String opted;  //Means what answer given by the candidate
//Default Constructor
public QuestionBean() {
        qid =0;
        question = option1 = option2 = option3 = option4 = answer = null;
        //opted = "";  //Empty -old logic
        opted="99";  //New logic-19-Jan
    }
//Same as we did in core java  Parameterized Constructor(*2)
 public QuestionBean(int qid, String question, String category, String option1, String option2, String option3, String option4, String answer) {
        this.qid = qid;
        this.question = question;
        this.category=category; //18-Mar-2021
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.opted="99"; 
    }
 //setter/getter fn

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOpted() {
        return opted;
    }

    public void setOpted(String opted) {
        this.opted = opted;
    }
    //Step 83: Fn to load questions from question table [RC=>Fix Imports]
    public static ArrayList<QuestionBean> loadQuestions(String category, boolean shuffle){
       //(a) Generate embeded SQL query
       String sql="Select * from question ";
       if(category!=null) //Means some category is provided 
           sql+=String.format(" Where category='%s' " , category); //apply where clause
           
        try {
            //(b) Execute the Select Query using DatabaseBean
           ResultSet rs=DatabaseBean.executeQuery(sql); //For Select 
           //(c) Create a Local ArrayList of QuestionBean [Generic Class]
           ArrayList<QuestionBean>qb=new ArrayList<>();
           //(d) Define some local variables
           int qi; String q, c, op1,op2, op3, op4, ans;
           //(e) Perform loop till rows available in resultSet
           while(rs.next())
           {    //(f) assign ResultSet data to local variables
               qi=rs.getInt("qid");
               q=rs.getString("question");
               c=rs.getString("category");
               op1=rs.getString("option1");
               op2=rs.getString("option2");
               op3=rs.getString("option3");
               op4=rs.getString("option4");
               ans=rs.getString("answer");
               //(g) Create Anonymous object of QuestionBean class and pass it to ArrayList
              qb.add(new QuestionBean(qi,q,c,op1,op2,op3,op4,ans)); //Calls (*2) PC
           }
           //(h) After Loop completes, we have to perform Shufflin
           if(shuffle){  //if(shuffle==true) - required for exam
               Collections.shuffle(qb); //Only required for candidate
           }
           return qb;  //Means return back ArrayList to calling/parent fn
        } catch (Exception e) {
            System.out.println("Exception in loadQuestions: " + e.toString());
            e.printStackTrace();
            return null;  //Means No ArrayList Created
        }
    }
    //Getting Question of current index [Not Used]
    public static QuestionBean getCurrentQuestion(ArrayList<QuestionBean>al, int index){
        return al.get(index);
    }
    //Step 85 - Converting Question data to comma seperated values
     @Override
    public String toString(){     
        String data=String.format("%s,%s,%s,%s,%s,%s,%s,%s",qid,question,category,option1,option2,option3,option4,answer);
        //System.out.println("JSon Array=" + data);
        return data;
    }
    //Step 94-20-Mar-2021
     public QuestionBean select(String qid){
        this.qid=Integer.parseInt(qid);
        String sql=String.format("Select * from Question where qid='%d'", this.qid);
        ResultSet rs=null;
        try {
            rs=DatabaseBean.executeQuery(sql);
            if(rs.next()){
                this.question=rs.getString("question");
                this.category=rs.getString("category");
                this.option1=rs.getString("option1");
                this.option2=rs.getString("option2");
                this.option3=rs.getString("option3");
                this.option4=rs.getString("option4");
                this.answer=rs.getString("answer");
                
            }
            else return null;
        } catch (Exception e) {
            System.out.println("[QB Select ] " + e);
            e.printStackTrace();
            return null;
        }
        return this;  //Means Current Object of the Class
    }
}
