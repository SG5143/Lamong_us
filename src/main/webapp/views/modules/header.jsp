<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/header.css">
<script type="module" src="resources/script/validation/validation-logout.js"></script>

<!-- favicon image -->
<link rel="shortcut icon" href="resources/images/LAMONG.ico">
<!-- ... -->

</head>
<body>
	<header id="header">
		<h1>Lamong Us</h1>

		<div class="profile-container">
			<img id="profile-image" src="/resources/images/default_image.jpg" alt="Profile Image"> 
			<img id="myPage-button" src="/resources/images/myPage_button.png" alt="Setting Image" onclick="window.location.href='/myPage';" style="cursor: pointer;">
		</div>

		<h2>${log.nickname}</h2>
		<h3>${log.score}점</h3>
		<div class="gauge-bar">
			<div class="gauge-bar-fill"></div>
		</div>

		<div class="user-actions">
			<button id="logout-button" class="header-menu-button">로그아웃</button>
		</div>
	</header>
</body>
</html>
