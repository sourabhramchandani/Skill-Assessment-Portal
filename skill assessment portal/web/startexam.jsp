<%@page contentType="text/html" pageEncoding="UTF-8"
        import="com.tecdev.QuestionBean, com.tecdev.QuizUserBean, 
        java.util.*"
        session="true"
        errorPage="error.jsp"
        %>
<%--To work with JSTL Step I is to import them using taglib direction--%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>

<!DOCTYPE html>
<html>
    <title>Bootstrap Example</title>
    <meta charset="UTF-8">
    <!--Backward compatible with Internet Explorer 8.0-->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!--Responsive Apps, Size adjusted as per device size-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!--Add Bootstrap CSS Before any other custom css -->
    <link rel="stylesheet" type="text/css" href="css/bootstrap.css" /> 
    <body>
        <div class="container">
            <%--(a) Obtain value of index from session--%>
            <c:set var="index" scope="session" value="${sessionScope.index}"/>
            <%--(b) Obtain question with current index from arraylist al into variable q--%>
            <c:set var="q" scope="session" value="${sessionScope.al.get(index)}" />
            <%--(c) If hacker is trying to bypass project, in that case index not found--%>
            <c:if test="${empty index}">
                <%--(d) Send it back to the login.html page--%>
                <jsp:forward page="login.html" />
            </c:if>
            <%--(e) Otherwise create a page header to display welcome userid--%>
            <div class="page-header">
                <h1>Welcome <c:out value="${sessionScope.qb.userid}"/> </h1>
                <%--f. get value of variable maxQuestions from session--%>
                <c:set var="maxQuestions" scope="session" value="${sessionScope.maxQuestions}" />
                <%--g. Create a bootstrap panel for UI - User Interface--%>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <%-- Q${qno+1} <label id="qq">${qno}</label>--%>
                        Q.No:<label id="qIndex">${index+1}</label>
                    </div>
                    <div class="panel-body">

                        <p>${q.question} </p> <%--Automatically calls getQuestion()--%>
                        <%--Display options in the form of Checkboxes--%>

                        <p><input type="checkbox" id="option1" value="1" ${(q.getOpted().indexOf("1")>=0) ? "checked":" "}/> ${q.option1}</p>
                        <p><input type="checkbox" id="option2" value="2" ${(q.getOpted().indexOf("2")>=0) ? "checked":" "}/> ${q.option2}</p>
                        <p><input type="checkbox" id="option3" value="3" ${(q.getOpted().indexOf("3")>=0) ? "checked":" "}/> ${q.option3}</p>
                        <p><input type="checkbox" id="option4" value="4" ${(q.getOpted().indexOf("4")>=0) ? "checked":" "}/> ${q.option4}</p>


                    </div>
                    <div class="panel-footer">
                        <%--<input type="hidden" id="oldqno" name="oldqno" value="${qno}" />--%>
                        <input type="button" value="Previous" style="visibility: ${index>0 ? "visible": "hidden"};" class="btn btn-primary"/>                  
                        <input type="button" value="Next"  style="visibility: ${index<maxQuestions-1? "visible": "hidden"};" class="btn btn-primary"/>                  
                        <input type="button" value="Finish" id="finish" style="visibility: ${index==maxQuestions-1? "visible": "hidden"};"  class="btn btn-primary"/>                 


                        <%--(h) Display Button currospend to each question for direct access--%>
                        <c:forEach var="i" begin="0" end="${maxQuestions-1}" step="1">
                            <c:set var="x" value="${sessionScope.al.get(i).opted}" scope="page" />
                            <input type="button"  id="btn${i}" 
                                   value="Q${i+1}" ${i eq index ?'disabled':' ' } 
                                   style="background-color:${x eq '-99' ?'grey':(x.length()>0?'green':'red')}" />

                        </c:forEach>
                    </div> <%--End of Panel-Footer--%>
                    <input type="hidden" id="temp" name="temp" value="${q.opted}" />

                </div>
                </form>
            </div>
        </div>
        <script src="script/jquery-3.5.1.min.js"></script>
        <script src="script/bootstrap.js"></script>
        <script>
            $(document).ready(function () {


                //End of FullScreen Logic
                $("input[type='checkbox']").change(function () {
                    var opted = "";
                    var $boxes = $('input[name=option]:checked');
                    $boxes.each(function () {
                        opted += $(this).val();
                    });
                    $("#temp").val(opted);
                    //alert("Ok");

                    //New Logic added on 02-Oct - To change color of
                    //Buttons when checkbox change state
                    //Obtain question index from hidden field
                    var index = $("#qIndex").text() - 1;
                    //Generate dynamic url - startexam tabular op=12 as well as index and opted
                    var myurl = "QuizServlet?op=9&index=" + index + "&opted=" + opted;
                    $.ajax({
                        url: myurl,
                        async: false,
                        type: 'POST', /*success fn execute when servlet call ends*/
                        success: function (data) {  //Get data from Servlet
                            var color = (opted.length == 0 ? 'red' : 'green');
                            //Pick button with id same as current question index
                            var btn = "#btn" + index;  // # is need for id
                            //Change css property using jQuery
                            $(btn).css('background-color', color);

                        },
                        error: function (jqXHR, exception) {
                            console.log('arrayToText Exception ' + excepttion);
                        }
                    });
                });
            });
            function switchPage(link) {
                //alert("Switch Page");
                //Obtain current time from hidden field time
                var timer = $("#time").val();
                //Obtain Hyperlink from parameter passed to this fn
                var href = $(link).attr("href");
                //Append current time with hyperlink
                href += "?time=" + timer;
                //Update href attribute
                $(link).attr("href", href);
                //alert("Time=" + timer + " Href=" + href);
                //If return false, hyperlink not fn
                return true; //means now redirect to the link
            }
        </script>
    </body>
</html>
