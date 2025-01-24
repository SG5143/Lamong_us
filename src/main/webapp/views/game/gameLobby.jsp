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
									<div class="input-wrap">
										<span>방 제목</span>

										<div>
											<input type="text" id="roomTitle" name="roomTitle" required>
											<span class="error-msg" id="roomTitleError">방 제목을 입력해주세요.</span>
										</div>

									</div>
								</div>

								<div>
									<div class="input-wrap">
										<span>인원수</span>

										<div>
											<input type="number" id="maxPlayers" name="maxPlayers" value="3" required>
											<span class="error-msg" id="maxPlayersError">인원수를 정해주세요.</span>
											<span class="error-msg" id="maxPlayersLimitError">3명에서 8명까지 가능합니다.</span>
										</div>


									</div>


								</div>

								<div>
									<div class="input-wrap">
										<span>라운드수</span>
										<div>

											<input type="number" id="roundCount" name="roundCount" value="2" required>
											<span class="error-msg" id="roundCountError">라운드 수를 정해주세요.</span>
											<span class="error-msg" id="roundCountLimitError">2~5회까지 가능합니다.</span>
										</div>

									</div>


								</div>


								<div>
									<div class="pw-wrap">
										<div>
											<span>비밀번호</span>
											<input type="checkbox" id="isPrivate" name="isPrivate">
										</div>
										<div>

											<input type="password" id="password" name="password" style="display: none;" maxlength="4">
											<span class="error-msg" id="passwordError">비밀번호를 입력해주세요.</span>
											<span class="error-msg" id="passwordLimitError">비밀번호는 4자리로 설정해주세요.</span>
										</div>

									</div>

								</div>

							</div>
							<button type="submit" id="submitButton">방 만들기</button>
						</form>

						<!-- 방번호 입력 입장 -->
						<form method="POST" id="joinRoomForm">
							<h2>방번호 입장</h2>
							<div class="input-wrap">
								<span>방 번호</span>
								<input type="text" id="roomNumber" name="roomNumber" placeholder="입장하실 방 번호를 입력해주세요." required>
							</div>

							<div id="passwordField" style="display: none;">
								<div class="input-wrap">
									<span>비밀번호</span>
									<div>
										<input type="password" id="roomPassword" placeholder="비밀번호 입력" maxlength="4">
										<span class="error-msg" id="roomPasswordError">비밀번호가 일치하지 않습니다.</span>
									</div>

								</div>

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