<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/game/waiting-room.css">
<link rel="stylesheet" href="resources/styles/globals.css">
<script type="module" src="resources/script/room-waiting.js"></script>
<title>Lamong Us Lobby</title>
</head>

<div id="wrap">
	<div id="header">Lamong Us</div>

	<body>
		<div id="main">
			<div id="cont-wrap">
				<div id="left-cont">
					<div id="left-top">
						<div>
							<button id="leave-btn">퇴장</button>
							<h2 id="room-tit"></h2>
						</div>
						<div>방설정 btn</div>

					</div>
					<div id="user-list"></div>
				</div>
				<div id="right-cont">채팅영역</div>
			</div>
		</div>
	</body>
</div>
</html>