<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/myPage.css">
<script type="module" src="resources/script/main-modal.js"></script>
<script type="module" src="resources/script/validation/validation-update.js"></script>
<script type="module" src="resources/script/validation/validation-logout.js"></script>
<script type="module" src="resources/script/validation/validation-delete.js"></script>
<script type="module" src="resources/script/validation/validation-block.js"></script>
<title>User Profile Page</title>
</head>
<body>
	<div id="header">
		<jsp:include page="/header" />
		<div class="header-menu">
			<button id="block-user-btn" data-modal="block-modal">유저 차단</button>
			<a href="/blockList">
				<button>차단 목록</button>
			</a>
		</div>

		<div class="user-actions">
			<button id="delete-account-btn" class="header-menu-button" data-modal="delete-modal">회원 탈퇴</button>
		</div>
	</div>

	<a id="backButton" class="back-button" href="/lobby"></a>
	<main class="main">
		<div class="main-content">
			<form id="form-update">
				<div class="grid-container">
					<section class="profile-section">
						<img id="user-profile-image" src="/resources/images/default_image.jpg" alt="Profile Image">
						<button type="button" id="save-profile-image">이미지 저장</button>
						<h2>${log.username}</h2>
						<p>${log.email}</p>
					</section>

					<section class="info-section">
						<h3>나를 소개하는 한 줄</h3>
						<textarea id="profile-info" placeholder='${log.profileInfo}'></textarea>
						<button type="button" id="save-profile-info">저장</button>
					</section>

					<section class="update-section">
						<h3>회원 정보 수정</h3>
						<div class="inline-div">
							<p>현재 비밀번호</p>
							<input type="password" id="current-password" placeholder="Password">
							<div id="err-msg-password-mismatch" class="error-msg">비밀번호가 일치하지 않습니다.</div>
							<div id="error-msg-current-password-pattern" class="error-msg">잘못된 형식입니다.</div>
							<div id="success-msg-password-check" class="success-msg">비밀번호 확인 완료</div>
						</div>

						<div class="inline-div">
							<p>새 비밀번호</p>
							<input type="password" id="new-password" placeholder="New Password">
							<div id="error-msg-new-password-pattern" class="error-msg">비밀번호는 최소 8자 이상, 특수기호를 포함해야 합니다.</div>
							<div id="error-msg-new-password-empty" class="error-msg">새 비밀번호를 입력해주세요.</div>
							<div id="success-msg-password-useable" class="success-msg">사용 가능한 비밀번호 입니다.</div>
						</div>

						<div class="inline-div">
							<p>비밀번호 확인</p>
							<input type="password" id="confirm-password" placeholder="Re Password">
							<div id="error-msg-confirm-password-match" class="error-msg">비밀번호가 일치하지 않습니다.</div>
							<div id="success-msg-password-match" class="success-msg">비밀번호 확인 완료</div>
						</div>

						<div class="inline-div">
							<p>닉네임</p>
							<input type="text" id="new-nickname" placeholder="${log.nickname}">
							<div id="error-msg-nickname-pattern" class="error-msg">잘못된 형식입니다.</div>
							<div id="error-msg-nickname-duplicate" class="error-msg">이 닉네임은 이미 사용 중입니다.</div>
							<div id="success-msg-nickname-useable" class="success-msg">사용 가능한 닉네임 입니다.</div>
						</div>

						<div class="inline-div">
							<p>전화번호</p>
							<input type="text" id="new-phone" placeholder="${log.phone}">
							<div id="error-msg-phone-pattern" class="error-msg">전화번호 형식이 올바르지 않습니다.</div>
							<div id="error-msg-phone-duplicate" class="error-msg">이미 사용 중인 전화번호입니다.</div>
							<div id="success-msg-phone-useable" class="success-msg">사용 가능한 전화번호 입니다.</div>
						</div>

						<div class="inline-div">
							<p>이메일</p>
							<input type="email" id="new-email" placeholder="${log.email}">
							<div id="error-msg-email-pattern" class="error-msg">이메일 형식이 올바르지 않습니다.</div>
							<div id="error-msg-email-duplicate" class="error-msg">이미 사용 중인 이메일입니다.</div>
							<div id="success-msg-email-useable" class="success-msg">사용 가능한 전화번호 입니다.</div>

						</div>

						<div class="inline-div">
							<p>계정 연동</p>
						</div>

						<div class="inline-div">
							<p>연동 정보</p>
						</div>
						<button type="button" id="save-user-data">변경 저장</button>
					</section>
				</div>
			</form>
		</div>
	</main>

	<div class="modal-overlay" id="modal-overlay">
		<div class="modal" id="block-modal">
			<div class="modal-content">
				<div class="block-icon"></div>
				<h2>유저 차단</h2>
				<div class="deletion-notice">
					<p>"차단할 유저의 닉네임을 입력하세요"</p>
				</div>
				<input type="text" id="blocked-user" placeholder="차단 유저 닉네임" />
				<div class="button-group">
					<button id="block-button">차단하기</button>
					<button id="cancel-button" data-close>취소하기</button>
				</div>
			</div>
		</div>

		<div class="modal" id="delete-modal">
			<div class="modal-content">
				<div class="delete-icon"></div>
				<h2>회원 탈퇴</h2>
				<div class="deletion-notice">
					<p>"회원 탈퇴 시, 계정은 삭제되며</p>
					<p>복구할 수 없습니다!"</p>
				</div>
				<input type="password" id="delete-password" placeholder="비밀번호" />
				<div id="err-msg-password-mismatch" class="error-msg">비밀번호가 일치하지 않습니다.</div>

				<div class="button-group">
					<button id="confirm-delete-button">탈퇴하기</button>
					<button id="cancel-button" data-close>취소하기</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
