<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/globals.css">
<link rel="stylesheet" href="resources/styles/game/play-room.css">
<title>Play</title>
</head>

<body>
	<div id="wrap">
		<div id="header">Lamong Us</div>
		
		<div id="main">
			<input type="hidden" id="roomUUID" value="${param.roomUUID}">
			<div id="cont-wrap">
				<div id="room-container">
					<div id="room-header">
							<button id="leave-btn" aria-label="퇴장"></button>
							<div id="room-info">
								<h3 id="room-number">2번방</h3>
								<h4 id="room-title">아무나 같이 하실분</h4>							
							</div>
					</div>
					<div id="room-body">
						<div id="role-info">
							<div id="topic-keyword">
								<p id="topic"></p>
								<p id="keyword"></p>
							</div>
							<div id="role-explanation">
								<p id="explanation-msg">플레이어가 모이면 게임이 시작됩니다.</p>
							</div>
						</div>
						<ul id="player-ilst">
						</ul>
					</div>
				</div>
				<div id="chat-container"><c:import url="/chat" /></div>
			</div>
		</div>
		
	</div>
</body>

</html>