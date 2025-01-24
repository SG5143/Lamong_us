<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/myPage.css">

<script type="module" src="resources/script/validation/validation-update.js"></script>
<title>User Profile Page</title>

</head>
<body>
	<div id="lobby-wrap">
		<header id="header">
			<h1>Lamong Us</h1>

			<img src="https://search.pstatic.net/sunny/?src=https%3A%2F%2Fw7.pngwing.com%2Fpngs%2F1004%2F26%2Fpng-transparent-emojipedia-space-invaders-sticker-computer-space-invaders-purple-violet-text.png&type=sc960_832" alt="Profile Picture">
			<h3>${log.nickname}</h3>
			<p>1123점</p>
			<div class="header-menu">
				<button>최근 플레이</button>
				<button>차단 목록</button>
			</div>
			<div class="user-actions">
				<a href="/v1/members?command=logout" class="text-link">로그아웃</a> <a href="/v1/members?command=delete" class="text-link">탈퇴</a>
			</div>
		</header>

		<main class="main">
			<div class="main-content">
				<section class="profile-section">
					<img src="https://via.placeholder.com/80" alt="Profile Picture">

					<div class="info">
						<h2>${log.username}</h2>
						<p>${log.email}</p>
					</div>
				</section>
				<section class="info-section">
					<h3>나를 소개하는 한 줄</h3>
					<textarea placeholder="자신을 소개하세요..."></textarea>
					<button type="button">저장</button>
				</section>
				<section class="update-section">
					<form>
						<h3>회원 정보 수정</h3>
						<div class="inline-div">
							<p>닉네임</p>
							<input type="text" id="new-nickname" placeholder="${log.nickname}">
							<div id="error-msg-nickname-pattern" class="error-msg">잘못된 형식입니다.</div>
							<div id="error-msg-nickname" class="error-msg">이 닉네임은 이미 사용 중입니다.</div>
						</div>

						<div class="inline-div">
							<p>현재 비밀번호</p>
							<input type="password" id="current-password" placeholder="Password">
							<div id="error-msg-password-empty" class="error-msg">현재 비밀번호를 입력해주세요.</div>
							<div id="err-msg-password-mismatch" class="error-msg">비밀번호가 일치하지 않습니다.</div>
						</div>

						<div class="inline-div">
							<p>새 비밀번호</p>
							<input type="password" id="new-password" placeholder="New Password">
							<div id="error-msg-new-password-pattern" class="error-msg">비밀번호는 최소 8자 이상이어야 합니다.</div>
							<div id="error-msg-password-empty" class="error-msg">새 비밀번호를 입력해주세요.</div>
						</div>

						<div class="inline-div">
							<p>비밀번호 확인</p>
							<input type="password" id="confirm-password" placeholder="Re Password">
							<div id="error-msg-confirm-password" class="error-msg">비밀번호가 일치하지 않습니다.</div>
						</div>

						<div class="inline-div">
							<p>전화번호</p>
							<input type="text" id="new-phone" placeholder="${log.phone}">
							<div id="error-msg-phone-pattern" class="error-msg">전화번호 형식이 올바르지 않습니다.</div>
							<div id="error-msg-phone" class="error-msg">이미 사용 중인 전화번호입니다.</div>
						</div>

						<div class="inline-div">
							<p>이메일</p>
							<input type="email" id="new-email" placeholder="${log.email}">
							<div id="error-msg-email-pattern" class="error-msg">이메일 형식이 올바르지 않습니다.</div>
							<div id="error-msg-email" class="error-msg">이미 사용 중인 이메일입니다.</div>
						</div>

						<div class="inline-div">
							<p>계정 연동</p>
						</div>

						<div class="inline-div">
							<p>연동 정보</p>
						</div>
						<button type="submit">변경 저장</button>
					</form>
				</section>
			</div>
		</main>
	</div>
</body>
</html>
