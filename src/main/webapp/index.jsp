<%@ page import="util.DBManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>LAMONG US MAIN PAGE</title>
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/main.css">
<script type="module" src="resources/script/main-modul.js"></script>
<script type="module" src="resources/script/validation/validation-login.js"></script>
<script type="module" src="resources/script/validation/validation-join.js"></script>
</head>
<body>
	<div class="container">
		<h1>LAMONG US</h1>
		<h2>Web 라이어 게임</h2>

		<div class="button-container">
			<button class="btn btn-top" data-modal="play-method-modal">플레이 방법</button>
			<div class="btn-group btn-group-mid">
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

		<!-- 플레이 방법 Modal -->
		<div class="modal" id="play-method-modal">
			<span class="close" data-close>&times;</span>
			<div class="modal-content">
				<h2>플레이 방법</h2>
				<div class="play-method">
					<div class="step">
						<span class="step-number">1</span>
						<h3>제시어 전달</h3>
						<p>
							사회자가 <strong>라이어를 제외한</strong> 모든 참가자에게 제시어를 전달합니다.
						</p>
					</div>
					<div class="step">
						<span class="step-number">2</span>
						<h3>설명 단계</h3>
						<p>
							각 참가자는 차례대로 제시어를 <strong>한 문장으로</strong> 설명합니다. 라이어는 제시어를 모릅니다.
						</p>
					</div>
					<div class="step">
						<span class="step-number">3</span>
						<h3>질문 및 투표</h3>
						<p>
							설명 후, 참가자들은 서로 질문을 주고받습니다. 이후, <strong>라이어를 지목하는 투표</strong>를 진행합니다.
						</p>
					</div>
					<div class="step">
						<span class="step-number">4</span>
						<h3>결과 확인</h3>
						<p>
							가장 많은 표를 받은 사람이 라이어로 지목됩니다. 라이어가 제시어를 맞추지 못하면 <strong>시민들 승리</strong>, 맞추면 <strong>라이어 승리</strong>입니다.
						</p>
					</div>
				</div>
			</div>
		</div>

		<!-- 로그인 Modal -->
		<div class="modal" id="login-modal">
			<span class="close" data-close>&times;</span>
			<div class="modal-content">
				<h2>로그인</h2>
				<form id="login-form" method="POST" action="/v1/login">
					<input id="username-login" name="username" type="text" placeholder="아이디" required>
					<input id="password-login" name="password" type="password" placeholder="비밀번호" required>
					<ul class="error-msg-group">
						<li id="error-msg-username-empty">아이디: 필수 정보입니다.</li>
						<li id="error-msg-username-pattern">아이디: 유효하지 않은 값입니다.</li>
						<li id="error-msg-password-empty">비밀번호: 필수 정보입니다.</li>
						<li id="error-msg-password-pattern">비밀번호: 유효하지 않은 값입니다.</li>
					</ul>
					<button id="login-btn" type="submit">로그인</button>
				</form>
				<p>or</p>
				<button class="social-login" type="button">카카오 로그인</button>
				<button class="social-login" type="button">구글 로그인</button>
			</div>
		</div>

		<!-- 회원가입 Modal -->
		<div class="modal" id="join-modal">
			<span class="close" data-close>&times;</span>
			<div class="modal-content">
				<h2>회원가입</h2>
				<form id="form-join" method="POST" action="/v1/join">
					<input id="username-join" name="username" type="text" placeholder="아이디" required>
					<input id="password-join" name="password" type="password" placeholder="비밀번호" required>
					<input id="nickname-join" name="nickname" type="text" placeholder="닉네임" required>
					<input id="phone-join" name="phone" type="tel" placeholder="전화번호" required>
					<input id="email-join" name="email" type="email" placeholder="이메일" required>
					<select id="login_type-join" name="login_type">
						<option value="">소셜 계정</option>
						<option value="1">kakao</option>
						<option value="2">google</option>
					</select>
					<button type="submit">회원가입</button>
				</form>
				<ul class="error-msg-group">
				</ul>
			</div>
		</div>

		<!-- 아이디 찾기 Modal -->
		<div class="modal" id="find-id-modal">
			<span class="close" data-close>&times;</span>
			<div class="modal-content">
				<h2>아이디 찾기</h2>
				<p>아이디 찾기 절차를 여기에 추가하세요.</p>
			</div>
		</div>

		<!-- 비밀번호 찾기 Modal -->
		<div class="modal" id="find-pw-modal">
			<span class="close" data-close>&times;</span>
			<div class="modal-content">
				<h2>비밀번호 찾기</h2>
				<p>비밀번호 찾기 절차를 여기에 추가하세요.</p>
			</div>
		</div>
	</div>
</body>
</html>
