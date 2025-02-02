<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

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
		<c:if test="${empty log}">
			<c:redirect url="/" />
		</c:if>
		<div id="main">
			<input type="hidden" id="uuid" value="${log.uuid}">
			<div id="cont-wrap">
				<div id="btn-wrap">
					<div>
						<button id="create-room-btn">방만들기</button>
						<button id="quick-enter-btn">빠른입장</button>
						<button id="enter-room-btn">방번호 입장</button>
					</div>

					<div>
						<button id="refresh-btn">방 리스트</button>
					</div>
				</div>

				<!-- 방 리스트  -->
				<div class="game-room"></div>

				<!-- 페이지 네이션 -->
				<div id="page-btns">
					<button id="prev-btn">이전</button>
					<button id="next-btn">다음</button>
				</div>

				<!-- 모달 -->
				<div id="create-room-modal" class="modal">
					<div class="modal-content">
						<span id="close-modal" class="close-btn">&times;</span>
						<form method="POST" id="create-room-form">
							<h2>방 만들기</h2>
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
										<span>인원수</span>
										<div>
											<input type="number" id="max-players" name="max-players" value="3" required>
											<span class="error-msg" id="empty-players-err">인원수를 정해주세요.</span>
											<span class="error-msg" id="limit-players-err">3명에서 8명까지 가능합니다.</span>
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
							<button type="submit" id="submit-btn">방 만들기</button>
						</form>

						<!-- 방번호 입력 입장 -->
						<form method="POST" id="join-room-form">
							<h2 id="modal-tit">방번호 입장</h2>
							<div class="input-wrap">
								<span>방 번호</span>
								<input type="text" id="room-num-input" name="room-num-input" placeholder="입장하실 방 번호를 입력해주세요.">
							</div>
							<div id="password-field" style="display: none;">
								<div class="input-wrap">
									<span>비밀번호</span>
									<div>
										<input type="password" id="room-password" placeholder="비밀번호 입력" maxlength="4">
										<span class="error-msg" id="room-password-err">비밀번호가 일치하지 않습니다.</span>
									</div>
								</div>
							</div>
							<div id="room-number">
								<div id="num-err">
									<span class="error-msg" id="room-num-err"></span>
								</div>
							</div>
							<button type="submit" id="join-room-btn">입장</button>
						</form>
					</div>
				</div>

				<!-- 에러 모달 -->
				<div id="err-modal" class="modal">
					<div class="modal-content">
						<span id="close-err-modal" class="close-btn">&times;</span>
						<p id="empty-err-msg"></p>
					</div>
				</div>
			</div>
		</div>
	</body>
</div>
</html>