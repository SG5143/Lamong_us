window.onload = () => {
	const roomData = JSON.parse(sessionStorage.getItem("roomData"));

	if (roomData) {
		console.log("Room Data:", roomData);
		document.getElementById("room-tit").textContent = roomData.GameRoom.room_title;
		document.getElementById("roomUUID").textContent = roomData.GameRoom.room_code;

		const userList = roomData.GameRoom.user_list;

		const userListContainer = document.getElementById("user-list");

		const userListHTML = userList.map(user => {
			const profileImg = user.profile_image ? user.profile_image : "/resources/images/default_image.jpg";
			return `
                <div class="user-item">
                    <img src="${profileImg}" alt="${user.nickname}" class="profile-img">
                    <div class="nickname">${user.nickname}</div>
                </div>
            `;
		}).join("");

		userListContainer.innerHTML = userListHTML;
	} else {
		alert("방 정보를 불러오지 못했습니다.");
		window.location.href = "/lobby";
	}

	const leaveRoom = async (roomCode) => {
		const authorizationToken = "sunsun";
		const requestBody = { room_code: roomCode };

		try {
			const response = await fetch("/v1/game-room?command=update-leave", {
				method: "PATCH",
				headers: {
					"Authorization": authorizationToken,
					"Content-Type": "application/json"
				},
				body: JSON.stringify(requestBody)
			});

			const result = await response.json();
			if (result.status === 200) {
				window.location.href = "/lobby";
			} else if (result.status === 401) {
				alert("로그인이 필요합니다.");
				window.location.href = "/";
			} else {
				alert("퇴장중 오류가 발생하였습니다.");
			}

		} catch (error) {
			console.error("Error Details:", { message: error.message, stack: error.stack, name: error.name });
			alert("퇴장에 실패했습니다.");
		}
	};


	document.getElementById("leave-btn").addEventListener("click", () => {
		const roomCode = roomData.GameRoom.room_code;
		leaveRoom(roomCode);
	});
};
