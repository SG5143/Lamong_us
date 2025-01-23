<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/styles/game/lobby.css">
<link rel="stylesheet" href="resources/styles/globals.css">
<script type="module" src="resources/script/lobby-main.js"></script>
<title>Lamong Us Lobby</title>
</head>

<div id="lobby-wrap">
	<div id="header">Lamong Us</div>

	<body>
		<div id="main">

			<div id="cont-wrap">
				<div id="btn-wrap">
					<button id="createRoomBtn">방만들기</button>
					<button>빠른입장</button>
					<button id="enterRoomNumberBtn">방번호 입장</button>
				</div>


				<!-- 방 리스트  -->
				<div class="game-room"></div>

				<!-- 페이지 네이션 -->
				<div id="page-btns">
					<button id="prevPageBtn">이전</button>
					<button id="nextPageBtn">다음</button>
				</div>


				<!-- 모달 -->
				<div id="createRoomModal" class="modal">
					<div class="modal-content">
						<span id="closeModal" class="close-btn">&times;</span>

						<form method="POST" id="createRoomForm">
							<h2>방 만들기</h2>

							<div id="modal-cont">
								<div>
									<label for="roomTitle">방 제목</label>
									<input type="text" id="roomTitle" name="roomTitle" required>
								</div>
								<span class="error-msg" id="roomTitleError">방 제목을 입력해주세요.</span>

								<div class="grid-pair">
									<div>
										<label for="maxPlayers">인원수</label>
										<div>
											<input type="number" id="maxPlayers" name="maxPlayers" value="3" required>
										</div>

									</div>
									<span class="error-msg" id="maxPlayersError">인원수를 정해주세요.</span>
									<span class="error-msg" id="maxPlayersLimitError">인원수는 최소3명 최대8명까지 설정 가능합니다.</span>

									<div>
										<label for="roundCount">라운드 수</label>
										<input type="number" id="roundCount" name="roundCount" value="2" required>
									</div>
									<span class="error-msg" id="roundCountError">라운드 수를 정해주세요.</span>
									<span class="error-msg" id="roundCountLimitError">라운드 수는 2~5회 설정 가능합니다.</span>

								</div>

								<div id="password-wrap">

									<div>
										<label for="isPrivate">비밀번호 설정</label>
										<input type="checkbox" id="isPrivate" name="isPrivate">
									</div>


									<div>
										<label for="password" id="passwordLabel" style="display: none;">비밀번호</label>
										<input type="password" id="password" name="password" style="display: none;" maxlength="4">

									</div>
								</div>
								<span class="error-msg" id="passwordError">비밀번호를 입력해주세요.</span>
								<span class="error-msg" id="passwordLimitError">비밀번호는 4자리로 설정해주세요.</span>
							</div>





							<button type="submit" id="submitButton">방 만들기</button>


						</form>

						<!-- 방번호 입력 입장 -->
						<form method="POST" id="joinRoomForm">
							<h2>방번호 입장</h2>
							<label for="roomNumber">방 번호</label>
							<input type="text" id="roomNumber" name="roomNumber" placeholder="입장하실 방 번호를 입력해주세요." required>
							<div id="passwordField" style="display: none;">
								<label for="roomNumber">방 비밀번호</label>
								<input type="password" id="roomPassword" placeholder="비밀번호 입력" maxlength="4">
								<span class="error-msg" id="roomPasswordError">비밀번호가 일치하지 않습니다.</span>
							</div>
							<span class="error-msg" id="roomNumError"></span>

							<button type="submit" id="joinRoomBtn">입장</button>
						</form>

					</div>
				</div>

				<!-- 에러 모달 -->
				<div id="errorModal" class="modal">
					<div class="modal-content">
						<span id="closeErrorModal" class="close-btn">&times;</span>
						<p id="errorMessage"></p>
					</div>
				</div>

			</div>

		</div>

	</body>


</div>

</html>