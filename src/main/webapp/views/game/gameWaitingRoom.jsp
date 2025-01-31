<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/game/waiting-room.css">
<link rel="stylesheet" href="resources/styles/globals.css">
<script type="module" src="resources/script/room-waiting.js"></script>
<title>Lamong Us Lobby</title>
</head>

<body>
	<div id="wrap">
		<div id="header">Lamong Us</div>
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
				<div id="right-cont"><c:import url="/chat" /></div>
			</div>
		</div>
	</div>
</body>
</html>