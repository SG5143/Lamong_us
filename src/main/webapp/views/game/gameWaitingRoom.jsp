<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/game/waiting-room.css">
<link rel="stylesheet" href="resources/styles/globals.css">
<script type="module" src="resources/script/room-waiting.js"></script>
<link rel="shortcut icon" href="resources/images/LAMONG.ico">
<title>Lamong Us Lobby</title>
</head>

<body>
	<div id="wrap">
		<div id="head">
			<c:import url="/header" />
		</div>
		<div id="main">
			<div id="cont-wrap">
				<div id="left-cont">
					<div id="left-top">
						<div>
							<button id="leave-btn" aria-label="퇴장"></button>
							<h3 id="room-number"></h3>
							<h4 id="room-tit"></h4>
						</div>
						<button id="start-btn">게임시작</button>

					</div>
					<div id="user-list"></div>
				</div>
				<div id="right-cont">
					<c:import url="/wait-chat" />
				</div>
			</div>
		</div>
</body>
</html>