document.addEventListener("DOMContentLoaded", async () => {
	const blockListContainer = document.getElementById("list-container");
	const apiKey = sessionStorage.getItem("apiKey");
	let currentPage = 1; 
	let isLastPage = false;

	async function fetchBlockList(page) {
		try {
			const blockResponse = await fetch(`/v1/members?command=blocked_list&page=${page}`, {
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
			console.log("서버 응답 데이터:", data);

			let blockList;
			if (data.Block_info) 
				blockList = data.Block_info;
			 else if (data.blockList) 
				blockList = data.blockList;
			 else 
				blockList = [];
			
			console.log("blockList 내용:", blockList);


			if (blockList && blockList.length > 0) {
				let html = '';
				blockList.forEach((block, index) => {
					html += `
		                   <tr>
		                       <td>${blockList.length - index}</td>
		                       <td><img src="/resources/images/default-profile.png" alt="프로필 이미지" /></td>
		                       <td>${block.blocked_user}</td>
		                       <td><button class="unblock-btn" onclick="unblockUser('${block.blocked_user}')">차단 해제</button></td>
		                   </tr>
		               `;
				});

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
			} else {
				blockListContainer.innerHTML = '<p>차단된 유저가 없습니다.</p>';
			}


			if (data.Meta && data.Meta.is_end) {
				isLastPage = data.Meta.is_end;
				nextPageBtn.disabled = isLastPage;
			}

		} catch (error) {
			console.error('차단된 유저 목록을 가져오는 데 실패했습니다:', error);
			alert("차단된 유저 목록을 가져오는 데 실패했습니다.");
		}
	}

	fetchBlockList(currentPage);

	const nextPageBtn = document.createElement('button');
	nextPageBtn.textContent = '다음 페이지';
	nextPageBtn.addEventListener('click', () => {
		if (!isLastPage) {
			currentPage++;
			fetchBlockList(currentPage);
		}
	});

	const prevPageBtn = document.createElement('button');
	prevPageBtn.textContent = '이전 페이지';
	prevPageBtn.addEventListener('click', () => {
		if (currentPage > 1) {
			currentPage--;
			fetchBlockList(currentPage);
		}
	});


	blockListContainer.insertAdjacentElement('afterend', prevPageBtn);
	blockListContainer.insertAdjacentElement('afterend', nextPageBtn);

});