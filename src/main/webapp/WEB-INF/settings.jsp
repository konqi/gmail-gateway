<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="req" value="${pageContext.request}"/>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>Pirate Mail Gateway</title>
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        /* Move down content because we have a fixed navbar that is 50px tall */
        body {
        padding-top: 50px;
        padding-bottom: 20px;
        }
    </style>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
    <script src="/scripts/ajax.js"></script>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Pirate Mail Gateway</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="#">Welcome back, ${mapping.ownerEmail}</a></li>
        </ul>
        <!--/.navbar-collapse -->
    </div>
</nav>


<div class="jumbotron">
    <div class="container">
        <h1>Sending mail as you from anywhere</h1>

        <p>&lt;handle&gt;@
            ${mailDomain}
        </p>

        <a class="btn btn-primary btn-lg" data-toggle="collapse" href="#collapseInfo" aria-expanded="false"
           aria-controls="collapseInfo">Learn more »</a>

        <div class="collapse" id="collapseInfo">
            <h2>What's this for</h2>
            Say...
            <ul>
                <li>you want to allow others to send email from your email address, but only to certain recipients</li>
                <li>you want to send your mail with your google apps account but you don't want to log into that
                    account.<br/>
                    That's usually because there's that stupid mailing list which can only be accesses from inside your
                    company's Google Apps account
                </li>
            </ul>
            <h2>How does it work</h2>
            <ol>
                <li>This app requests your permission to send emails as your Gmail or Google Apps account (don't worry,
                    you can
                    revoke permissions any time, see <a
                            href="https://support.google.com/accounts/answer/3466521?hl=en">here</a>)
                </li>
                <li>
                    On this page you configure the sender email address under "Inbound Configuration". This is the
                    address from which you or someone else will be able to send email as you.
                </li>
                <li>
                    In the "Outbound Configuration" section you create a handle to which you will send mail from the
                    email configured above. For this new handle enter the mail addresses of who will receive the email
                    sent as either the recipient, CC or BCC.
                </li>
                <li>
                    Once your handle is saved you will see the new email address to which you can now send email.
                    <strong>Remember: </strong>This will only work from the email address configured in the second step.
                </li>
            </ol>
        </div>
    </div>
</div>

<div class="container">
    <h2>Inbound Configuration</h2>

    <form name="senderMail">
        <div class="input-group">
            <input type="email" class="form-control" id="senderEmail" placeholder="Email Adresse"
                   value="${fn:escapeXml(mapping.senderEmail)}">
            <span class="input-group-btn">
                <button type="submit" class="btn btn-default">Speichern</button>
            </span>
        </div>
    </form>

    <h2>Outbound Configuration</h2>

    <form name="addHandle">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>handle</th>
                <th>to</th>
                <th>cc</th>
                <th>bcc</th>
                <th></th>
            </tr>
            </thead>
            <tfoot>
            <tr>
                <td><input type="text" id="newHandleName" placeholder="handle"></td>
                <td><input type="email" id="newHandleTo" placeholder="to"></td>
                <td><input type="email" id="newHandleCc" placeholder="cc"></td>
                <td><input type="email" id="newHandleBcc" placeholder="bcc"></td>
                <td>
                    <button type="btn btn-default"><span class="glyphicon glyphicon-floppy-save"></span></button>
                </td>
            </tr>
            </tfoot>
            <tbody>
            <c:forEach items="${handles}" var="currentHandle">
                <tr>
                    <td>
                        <a href="mailto:${currentHandle.handle}@${mailDomain}">${currentHandle.handle}@${mailDomain}</a>
                    </td>
                    <td>
                        <c:out value="${currentHandle.to}"></c:out>
                    </td>
                    <td>
                        <c:out value="${currentHandle.cc}"></c:out>
                    </td>
                    <td>
                        <c:out value="${currentHandle.bcc}"></c:out>
                    </td>
                    <td>
                        <a class="btn btn-default" href="javascript:deleteHandle('${currentHandle.handle}');"><span class="glyphicon glyphicon-trash"></span></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </form>

    <footer class="footer">
        <p>&copy; konqi 2015</p>
    </footer>
</div>
</body>
</html>