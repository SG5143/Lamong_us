<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="/resources/styles/globals.css">
<link rel="stylesheet" href="/resources/styles/ui/blockList.css">

<script type="module" src="/resources/script/validation/validation-blockList.js"></script>
<title>Blocked User List</title>
</head>
<body>
	<div id="header">
		<jsp:include page="/header" />
		<div class="header-menu">
			<button id="block-user-btn" data-modal="block-modal">유저 차단</button>
		</div>
	</div>

	<a id="backButton" class="back-button" href="/myPage"></a>
	<main class="main">
		<div class="main-content">
			<h2>차단된 유저 목록</h2>
			<c:set var="page" value="${empty param.page ? 1 : param.page}" />
			<c:set var="size" value="${empty blockList ? 0 : blockList.size()}" />
			<div id="list-container">
				<table class="block-list-table">
					<thead>
						<tr>
							<th>번호</th>
							<th>프로필 이미지</th>
							<th>닉네임</th>
							<th>차단 해제</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="block" varStatus="status" items="${blockList}">
							<tr>
								<td>
									<span class="no">${size - status.index - ((page - 1) * 10)}</span>
								</td>
								<td>
									<img src="/resources/images/blockedUsersImage.png" alt="프로필 이미지" />
								</td>
								<td>
									<span class="blocked-User-nickname">${block.blockedUserNickname}</span>
								</td>
								<td>
									<button class="unblock-btn" onclick="unblockUser('${block.blockedUser}')">차단 해제</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
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