<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/game/lobby.css">
<script type="module" src="resources/script/lobby-main.js"></script>
<title>Lamong Us Lobby</title>
</head>
<body>
	<h1>game lobby</h1>

	<button id="createRoomBtn">방만들기</button>
	<button>빠른입장</button>
	<button id="enterRoomNumberBtn">방번호 입장</button>

	<!-- 방 리스트  -->
	<div class="game-room"></div>

	<!-- 페이지 네이션 -->
	<button id="prevPageBtn">이전 페이지</button>
	<button id="nextPageBtn">다음 페이지</button>

	<!-- 모달 -->
	<div id="createRoomModal" class="modal">
		<div class="modal-content">
			<span id="closeModal" class="close-btn">&times;</span>
			<h2>방 만들기</h2>
			<form method="POST" id="createRoomForm">

				<label for="roomTitle">방 제목</label>
				<input type="text" id="roomTitle" name="roomTitle" required>
				<span class="error-msg" id="roomTitleError">방 제목을 입력해주세요.</span>

				<div class="number-wrap">
					<div>
						<label for="maxPlayers">인원수</label>
						<input type="number" id="maxPlayers" name="maxPlayers" value="3" required>
						<span class="error-msg" id="maxPlayersError">인원수를 정해주세요.</span>
						<span class="error-msg" id="maxPlayersLimitError">인원수는 최소3명 최대8명까지 설정 가능합니다.</span>
					</div>
					<div>
						<label for="roundCount">라운드 수</label>
						<input type="number" id="roundCount" name="roundCount" value="2" required>
						<span class="error-msg" id="roundCountError">라운드 수를 정해주세요.</span>
						<span class="error-msg" id="roundCountLimitError">라운드 수는 2~5회 설정 가능합니다.</span>
					</div>
				</div>
				<div class="password-wrap">
					<label for="isPrivate">비밀번호 설정</label>
					<input type="checkbox" id="isPrivate" name="isPrivate">

					<label for="password" id="passwordLabel" style="display: none;">비밀번호</label>
					<input type="password" id="password" name="password" style="display: none;" maxlength="4">
					<span class="error-msg" id="passwordError">비밀번호를 입력해주세요.</span>
					<span class="error-msg" id="passwordLimitError">비밀번호는 4자리로 설정해주세요.</span>
				</div>
				<button type="submit" id="submit-button">방 만들기</button>
			</form>

			<!-- 방번호 입력 입장 -->
			<div id="joinRoomForm" style="display: none;">
				<label for="roomNumber">방 번호</label>
				<input type="number" id="roomNumber" name="roomNumber" required>
				<button type="button" id="joinRoomBtn">입장</button>
			</div>
		</div>
	</div>
</body>
</html>