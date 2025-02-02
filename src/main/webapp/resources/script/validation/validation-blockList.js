import {
	getNicknameByUuid,
} from "./validation.js";

document.addEventListener("DOMContentLoaded", async () => {
	const blockListContainer = document.getElementById("list-container");
	const apiKey = sessionStorage.getItem("apiKey");
	const userUuid = sessionStorage.getItem("uuid"); 

	let currentPage = parseInt("${page}".trim());
	if (isNaN(currentPage)) {
		currentPage = 1; 
	}

	if (!apiKey) {
		console.error("apiKey가 존재하지 않습니다. 로그인 상태를 확인하세요.");
	}

	async function fetchBlockList(currentPage) {
		try {

			const blockResponse = await fetch(`/v1/members?command=blocked_list&blocking_user=${userUuid}&page=${currentPage}`, {
				method: "GET",
				headers: {
					"Content-Type": "application/json",
					"Authorization": `Bearer ${apiKey}`
				}
			});

			if (!blockResponse.ok) {
				throw new Error(`HTTP error! status: ${blockResponse.status}`);
			}

			const data = await blockResponse.json();

			let blockList = data.Block_info || [];

			if (blockList.length > 0) {
				let html = '';
				blockList.forEach(async (block, index) => {

					const nickname = await getNicknameByUuid(block.blocked_user, apiKey);

					html += `
                        <tr>
						<td>${index + 1}</td> 
                            <td><img src="/resources/images/blockedUsersImage.png" alt="프로필 이미지" /></td>
                            <td>${nickname}</td>
                            <td><button class="unblock-btn" onclick="unblockUser('${block.blockedUser}')">차단 해제</button></td>
                        </tr>
                    `;

					blockListContainer.innerHTML = `
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
                            ${html}
                        </tbody>
                    </table>
                `;
				});

			} else {
				blockListContainer.innerHTML = '<p>차단된 유저가 없습니다.</p>';
			}

		} catch (error) {
			console.error('차단된 유저 목록을 가져오는 데 실패했습니다:', error);
			alert("차단된 유저 목록을 가져오는 데 실패했습니다.");
		}
	}

	fetchBlockList(currentPage);
});
