<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<script type="module" src="/resources/script/liarGame/liarGame.js"></script>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=send"/>
<link rel="stylesheet" href="resources/styles/globals.css">
<link rel="stylesheet" href="resources/styles/chat/chat.css">
</head>

<body>
<div id ="root ">
	<div id="chat-area">
		<div id="chat"></div>
		<div id="msg-input-container">
			<input type="text" id="message" maxlength="50">
			<span id="send" class="material-symbols-outlined"> send </span>
		</div>
	</div>

	<!-- 라이어 투표 모달 -->
	<div id="vote-modal" class="modal">
		<div class="modal-content">
			<span class="close" id="vote-timer">00:30</span>
			<h2>라이어 투표</h2>
			<p>투표할 사람을 선택하세요</p>
			<div id="vote-options">
				<!-- 투표 옵션 동적 추가 -->
			</div>
			<button id="submit-vote">투표하기</button>
		</div>
	</div>
	
	<!-- 라이어 제시어 맞추기 모달 -->
	<div id="liar-chance-modal" class="modal">
		<div class="modal-content">
			<span class="close" id="close-modal">00:30</span>
			<h2>제시어를 맞추세요.</h2>
			<input id="liar-chance-input" type="text" maxlength="15">
			<button id="submit-keyword">제출</button>
		</div>
	</div>
	
	<!-- 최종 결과 출력 모달 -->
	<div id="game-result-modal" class="modal">
		<div class="modal-content">
		</div>
	</div>
</div>
</body>

</html>