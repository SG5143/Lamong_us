<%@ page import="util.DBManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/main.css">

<script type="module" src="resources/script/main-modul.js"></script>
<script type="module" src="resources/script/validation/validation-login.js"></script>
<script type="module" src="resources/script/validation/validation-join.js"></script>


<title>LAMONG US MAIN PAGE</title>
</head>
<body>
	<div class="container">
		<h1>LAMONG US</h1>
		<h2>Web 라이어 게임</h2>

		<div class="button-container">
			<button class="btn btn-top" data-modal="play-method-modal">플레이 방법</button>
			<div class="btn-group-mid">
				<button class="btn btn-mid" data-modal="login-modal">로그인</button>
				<button class="btn btn-mid" data-modal="join-modal">회원가입</button>
			</div>
			<div class="btn-group-bottom">
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
					게임 방법:
					<br>
					1. 제시어 전달: 사회자가 라이어를 제외한 모든 참가자에게 제시어를 전달합니다.
					<br>
					2. 설명 단계: 각 참가자는 차례대로 제시어를 설명합니다. 설명은 한 문장으로 간단하게 진행하며, 라이어는 제시어를 모릅니다.
					<br>
					3. 질문 및 투표: 설명 후, 참가자들은 서로 질문을 주고받습니다. 이후, 라이어를 지목하는 투표를 진행합니다.
					<br>
					4. 결과 확인: 가장 많은 표를 받은 사람이 라이어로 지목되며, 라이어가 제시어를 맞추지 못하면 시민들이 승리합니다. 반대로, 라이어가 제시어를 맞추면 라이어가 승리합니다.
				</p>
			</div>
		</div>

		<!-- 로그인 Modal -->
		<div class="modal" id="login-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<form method="POST" action="/v1/login">
					<h2>로그인</h2>
					<div class="login-container">
						<button class="social-login" type="button">카카오 로그인</button>
						<button class="social-login" type="button">구글 로그인</button>
						<p>또는</p>
						<input id="username-login" name="username" type="text" placeholder="아이디" required>
						<input id="password-login" name="password" type="password" placeholder="비밀번호" required>

						<ul class="error-msg-group">
							<li id="error-msg-username-empty" class="error-msg">아이디: 필수 정보입니다.</li>
							<li id="error-msg-username-pattern" class="error-msg">아이디: 유효하지 않은 값입니다.</li>
							<li id="error-msg-password-empty" class="error-msg">비밀번호: 필수 정보입니다.</li>
							<li id="error-msg-password-pattern" class="error-msg">비밀번호: 유효하지 않은 값입니다.</li>
						</ul>
					</div>

					<button type="submit">로그인</button>
				</form>
			</div>
		</div>

		<!-- 회원가입 Modal -->
		<div class="modal" id="join-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<form id="form-login" method="POST" action="/v1/login">
					<h2>로그인</h2>
					<input id="username-join" name="username" type="text" placeholder="아이디" required>
					<input id="password-join" name="password" type="password" placeholder="비밀번호" required>
					<input id="nickname-join" name="nickname" type="text" placeholder="닉네임" required>
					<input id="phone-join" name="phone" type="text" placeholder="전화번호" required>
					<input id="email-join" name="email" type="text" placeholder="이메일" required>
					<select id="login_type-join" name="login_type">
						<option value="">소셜 계정</option>
						<option value="1">kakao</option>
						<option value="2">google</option>
					</select>

					<ul class="error-msg-group">
						<li id="error-msg-username-pattern">아이디: 5~20자의 영문 소문자, 숫자와 최소 1개의 특수기호(-_!@#$%^&*.)만 사용 가능합니다.</li>
						<li id="error-msg-username">아이디: 사용할 수 없는 아이디입니다. 다른 아이디를 입력해 주세요.</li>
						<li id="error-msg-username-empty">아이디: 필수 정보입니다.</li>
						<li id="error-msg-password-pattern">비밀번호: 8~20자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.</li>
						<li id="error-msg-password-empty">비밀번호: 필수 정보입니다.</li>
						<li id="error-msg-nickname">닉네임: 사용할 수 없는 닉네임입니다. 다른 닉네임을 입력해 주세요.</li>
						<li id="error-msg-nickname-empty">닉네임: 필수 정보입니다.</li>
						<li id="error-msg-phone">전화번호: 사용할 수 없는 전화번호입니다. 다른 전화번호를 입력해 주세요.</li>
						<li id="error-msg-phone-empty">전화번호: 필수 정보입니다.</li>
						<li id="error-msg-email">이메일: 사용할 수 없는 이메일입니다. 다른 이메일을 입력해 주세요.</li>
						<li id="error-msg-email-empty">이메일: 필수 정보입니다.</li>
					</ul>

					<button type="submit">회원가입</button>
				</form>
			</div>
		</div>

		<!-- 아이디 찾기 Modal -->
		<div class="modal" id="find-id-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<p>아이디 찾기 절차를 여기에 추가하세요.</p>
			</div>
		</div>

		<!-- 비밀번호 찾기 Modal -->
		<div class="modal" id="find-pw-modal">
			<div class="modal-content">
				<span class="close" data-close>x</span>
				<p>비밀번호 찾기 절차를 여기에 추가하세요.</p>
			</div>
		</div>
	</div>
</body>
</html>