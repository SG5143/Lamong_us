<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="module" src="/resources/script/chat.js"></script>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=send"/>
<link rel="stylesheet" href="resources/styles/chat/chat.css">
</head>
<body>
<div id ="root">
	<div id="chat-area">
		<div id="chat"></div>
		<div id="msg-input-container">
			<input type="text" id="message" maxlength="50">
			<span id="send" class="material-symbols-outlined"> send </span>
		</div>
	</div>
</div>
</body>
</html>