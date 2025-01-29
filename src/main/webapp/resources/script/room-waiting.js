window.onload = () => {
	const roomData = JSON.parse(sessionStorage.getItem("roomData"));

	if (roomData) {
		console.log("Room Data:", roomData);
		document.getElementById("room-tit").textContent = roomData.GameRoom.room_title;

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
		const userCode = "95399f04-dd81-11ef-b3f2-025c4feb1d05";
		const authorizationToken = "sunsun";
		const requestBody = { user_code: userCode, room_code: roomCode };

		try {
			const response = await fetch("/v1/game-room?command=update-leave", {
				method: "PATCH",
				headers: {
					"Authorization": authorizationToken,
					"Content-Type": "application/json"
				},
				body: JSON.stringify(requestBody)
			});

			if (!response.ok) {
				handleFetchError(response);
				return;
			}

			const result = await response.json();
			if (result.status === 200) {
				window.location.href = "/lobby";
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
