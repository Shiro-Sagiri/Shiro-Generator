<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>FreeMarker</title>
</head>
<body>
<h1>FreeMarker Test</h1>
<#--  循环渲染导航条  -->
<ul>
    <#list menuItems as item>
        <li><a href="${item.url}">${item.label}</a></li>
    </#list>
</ul>
<footer>
    ${currentYear} &copy; <a href="https://www.baidu.com">百度</a>
</footer>
</body>
</html>