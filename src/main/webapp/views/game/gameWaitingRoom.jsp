<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/game/waiting-room.css">
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
						<button id="setting-btn">방설정</button>
					</div>
					<div id="user-wrap">
						<div id="user-list"></div>
						<button id="start-btn">게임시작</button>
					</div>

				</div>
				<div id="right-cont">
					<c:import url="/wait-chat" />
				</div>
			</div>
		</div>

		<div id="setting-room-modal" class="modal">
			<div class="modal-content">
				<span id="close-modal" class="close-btn">&times;</span>
				<form method="PATCH" id="setting-room-form">
					<h2>방 설정</h2>
					<div id="modal-cont">
						<div>
							<div class="input-wrap">
								<span>방 제목</span>
								<div>
									<input type="text" id="room-title" name="room-title" required>
									<span class="error-msg" id="empty-title-err">방 제목을 입력해주세요.</span>
								</div>
							</div>
						</div>
						<div>
							<div class="input-wrap">
								<span>라운드수</span>
								<div>
									<input type="number" id="round" name="round" value="2" required>
									<span class="error-msg" id="empty-round-err">라운드 수를 정해주세요.</span>
									<span class="error-msg" id="limit-round-err">2~5회까지 가능합니다.</span>
								</div>
							</div>
						</div>
						<div>
							<div id="pw-wrap">
								<div>
									<span>비밀번호</span>
									<input type="checkbox" id="private" name="private">
								</div>

								<div>
									<input type="password" id="password" name="password" style="display: none;" maxlength="4">
									<span class="error-msg" id="empty-password-err">비밀번호를 입력해주세요.</span>
									<span class="error-msg" id="limit-password-err">비밀번호는 4자리로 설정해주세요.</span>
								</div>
							</div>
						</div>
					</div>
					<button type="submit" id="submit-btn">확인</button>
				</form>
			</div>
		</div>
</body>
</html>