import {
	checkDuplUsername,
} from "./validation.js";

document.addEventListener("DOMContentLoaded", async () => {

	const blockedUserNickname = document.getElementById("blocked-user");
	const userNickname = sessionStorage.getItem("nickname");
	const blockButton = document.getElementById("block-button");

	if (!userNickname || !userNickname.trim()) {
		alert("로그인 사용자 정보가 없습니다. 다시 로그인 해주세요.");
		return;
	}

	blockButton.addEventListener("click", async (e) => {
		e.preventDefault();

		if (!blockedUserNickname || !blockedUserNickname.value.trim()) {
			alert("차단할 유저의 닉네임을 입력하세요.");
			return;
		}

		if (userNickname === blockedUserNickname.value.trim()) {
			alert("본인은 차단할 수 없습니다.");
			return;
		}

		const isValidNickname = await checkDuplUsername(blockedUserNickname.value.trim());
		if (!isValidNickname) {
			alert("존재하지 않는 유저입니다.");
			return;
		}

		const apiKey = sessionStorage.getItem("apiKey");

		try {
			const blockResponse = await fetch("/v1/members?command=block", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					"Authorization": `Bearer ${apiKey}`
				},
				body: JSON.stringify({
					blocking_user: userNickname,
					blocked_user: blockedUserNickname.value.trim()
				})
			});

			if (blockResponse.ok) {
				const result = await blockResponse.json();
				alert(result.message);
			} else {
				const errorResult = await blockResponse.json();
				alert(`유저 차단 실패: ${errorResult.message}`);
			}
		} catch (error) {
			console.error("유저 차단 중 오류 발생:", error);
			alert("서버와의 통신 중 문제가 발생했습니다.");
		}
	});
});