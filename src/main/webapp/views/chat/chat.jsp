<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<script type="module" src="/resources/javascript/chat/chat.js"></script>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=send"/>
<link rel="stylesheet" href="resources/styles/globals.css">
<link rel="stylesheet" href="resources/styles/chat/chat.css">
<title>채팅 테스트</title>
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
	<div id="liar-vote-modal" class="modal">
		<div class="modal-content">
			<span class="close" id="close-modal">00:30</span>
			<h2>라이어 투표</h2>
			<p>투표할 사람을 선택하세요</p>
			<div id="vote-options">
				<!-- 투표 옵션 동적 추가 -->
			</div>
			<button id="submit-vote">투표하기</button>
		</div>
	</div>
</div>
</body>

</html>