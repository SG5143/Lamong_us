<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/header.css">

</head>
<body>
	<header id="header">
		<h1>Lamong Us</h1>
		<img id="profile-image" src="${log.profileImage != null ? log.profileImage : 'https://img.icons8.com/?size=100&id=Hj21JM30swCm&format=png&color=000000'}" alt="Profile Image">

		<h2>${log.nickname}</h2>
		<h3>${log.score}점</h3>
		<div class="gauge-bar">
			<div class="gauge-bar-fill"></div>
		</div>
		<p>승률:</p>
	</header>
</body>
</html>
