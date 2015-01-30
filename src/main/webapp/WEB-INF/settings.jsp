<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="de.konqi.remailer.AppengineEnv" %>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>P2B Mail Gateway</title>
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="/styles/jumbotron-narrow.css" rel="stylesheet">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div class="header">
        <nav></nav>
    </div>

    <div class="jumbotron">
        <h1>Send your shit to</h1>
        <p>handle@<%= AppengineEnv.appId %>.appspot.com</p>
        <p><a class="btn btn-primary btn-lg" href="#" role="button">Useless Button</a></p>
    </div>

    <form>
        <div class="form-group">
            <label for="source-email">Quell Email (außerhalb vom Firmennetz)</label>
            <input type="email" class="form-control" id="source-email" placeholder="Email Adresse">
        </div>
        <button type="submit" class="btn btn-default">Speichern</button>
    </form>

    <footer class="footer">
        <p>&copy; konqi 2015</p>
    </footer>
</div>
</body>
</html>