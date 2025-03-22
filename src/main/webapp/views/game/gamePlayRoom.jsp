<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/globals.css">
<link rel="stylesheet" href="resources/styles/game/play-room.css">
<link rel="shortcut icon" href="resources/images/LAMONG.ico">
<script src="resources/script/liarGame/gameRoom.js"></script>
<title>Play</title>
</head>

<body>
	<div id="head"><c:import url="/header" /></div>
	
	<div id="wrap">
		<div id="main">
			<input type="hidden" id="roomUUID" value="${param.roomUUID}">
			<div id="cont-wrap">
				<div id="room-container">
					<div id="room-header">
							<button id="exit-btn" aria-label="퇴장"></button>
							<div id="room-info">
								<h3 id="room-number">1번방</h3>
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
	
	<div id="exit-modal">
		<div id="exit-container">
			<h3>게임중 퇴장시 <br>패널티가 부과될 수 있습니다.</h3>
			<h4>정말 퇴장하시겠습니까?</h4>
			<div id="button-group">
				<button id="exit-cancel">취소</button>
				<button id="exit-button">나가기</button>
			</div>
		</div>
	</div>
</body>

</html>