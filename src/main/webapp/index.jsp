<%@ page import="util.DBManager" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/styles/main.css">
<title>Insert title here</title>
</head>
<body>
	<div class="container">
		<h1>LAMONG US</h1>
		<h2>Web 라이어 게임</h2>

		<div class="button-container">
			<button class="btn btn-top" data-modal="play-method-modal">플레이 방법</button>
			<div class="btn-group">
				<button class="btn btn-mid" data-modal="login-modal">로그인</button>
				<button class="btn btn-mid" data-modal="join-modal">회원가입</button>
			</div>
			<div class="btn-group">
				<button class="btn btn-bottom" data-modal="find-id-modal">아이디 찾기</button>
				<button class="btn btn-bottom" data-modal="find-pw-modal">비밀번호 찾기</button>
			</div>
		</div>
	</div>

	<div class="modal-overlay" id="modal-overlay">
		<div class="modal" id="play-method-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<h2>플레이 방법</h2>
				<p>
					게임 방법:<br> 1. 제시어 전달: 사회자가 라이어를 제외한 모든 참가자에게 제시어를 전달합니다.<br> 2. 설명 단계: 각 참가자는 차례대로 제시어를 설명합니다. 설명은 한 문장으로 간단하게 진행하며, 라이어는 제시어를 모릅니다.<br> 3. 질문 및 투표: 설명 후, 참가자들은 서로 질문을 주고받습니다. 이후, 라이어를 지목하는 투표를 진행합니다.<br> 4. 결과 확인: 가장 많은 표를 받은 사람이 라이어로 지목되며, 라이어가 제시어를 맞추지 못하면 시민들이 승리합니다. 반대로, 라이어가 제시어를 맞추면 라이어가 승리합니다.<br>
				</p>
			</div>
		</div>

		<!-- 로그인 Modal -->
		<div class="modal" id="login-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<form id="login-form">
					<input id="username" type="text" placeholder="아이디" required> 
					<input id="password" type="password" placeholder="비밀번호" required> 

					<ul class="error-msg-group">
						<li id="error-msg-username-empty" class="error-msg">아이디: 필수 정보입니다.</li>
						<li id="error-msg-username-pattern" class="error-msg">아이디: 유효하지 않은 값입니다.</li>
						<li id="error-msg-password-empty" class="error-msg">비밀번호: 필수 정보입니다.</li>
						<li id="error-msg-password-pattern" class="error-msg">비밀번호: 유효하지 않은 값입니다.</li>
					</ul>

					<button type="submit">로그인</button>
					<button type="button">카카오 로그인</button>
					<button type="button">구글 로그인</button>
				</form>
			</div>
		</div>

		<div class="modal" id="join-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<form>
					<input type="text" placeholder="아이디" required> <input type="password" placeholder="비밀번호" required>
					<button type="submit">회원가입</button>
				</form>
			</div>
		</div>

		<div class="modal" id="find-id-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<p>아이디 찾기 절차를 여기에 추가하세요.</p>
			</div>
		</div>

		<div class="modal" id="find-pw-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<p>비밀번호 찾기 절차를 여기에 추가하세요.</p>
			</div>
		</div>
	</div>
</body>
</html>