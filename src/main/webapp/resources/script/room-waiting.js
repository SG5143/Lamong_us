import { validateRoomTitle, validateRoundCount, validatePassword } from './room-validation.js';

window.onload = () => {
	const roomData = JSON.parse(sessionStorage.getItem("roomData"));

	if (roomData) {
		console.log("Room Data:", roomData);
		document.getElementById("room-tit").textContent = roomData.GameRoom.room_title;
		document.getElementById("room-number").textContent = `${roomData.GameRoom.room_number}번방`;

	} else {
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
			alert("퇴장중 오류가 발생하였습니다.");
		}
	};


	document.getElementById("leave-btn").addEventListener("click", () => {
		const roomCode = roomData.GameRoom.room_code;
		leaveRoom(roomCode);
	});

	document.getElementById("start-btn").addEventListener("click", () => {
		window.location.href = "/play-room";
	});

	const settingBtn = document.getElementById("setting-btn");
	const modal = document.getElementById("setting-room-modal");
	const closeModal = document.getElementById("close-modal");
	const privateCheckbox = document.getElementById("private");
	const passwordInput = document.getElementById("password");


	settingBtn.addEventListener("click", () => {
		modal.style.display = "block";
	});


	if (closeModal) {
		closeModal.addEventListener("click", () => {
			settingRoom();
			modal.style.display = "none";
		});
	}

	window.addEventListener("click", (event) => {
		if (event.target === modal) {
			modal.style.display = "none";
		}
	});

	if (privateCheckbox) {
		privateCheckbox.addEventListener("change", () => {
			passwordInput.style.display = privateCheckbox.checked ? "block" : "none";
		});
	}


	const addInputEventListeners = () => {
		document.getElementById('room-title').addEventListener('input', (e) => validateRoomTitle(e.target.value));
		document.getElementById('round').addEventListener('input', (e) => validateRoundCount(e.target.value));
		document.getElementById('password').addEventListener('input', (e) => validatePassword(e.target.value));
	};

	addInputEventListeners();

	const isRoomDataValid = (roomTitle, roundCount, isPrivate, password) => {
		let isValid = true;

		if (!validateRoomTitle(roomTitle)) isValid = false;
		if (!validateRoundCount(roundCount)) isValid = false;
		if (isPrivate && !validatePassword(password)) isValid = false;

		return isValid;
	};


	const buildRequestBody = (roomTitle, roundCount, isPrivate, password) => {
		
		const roomCode = roomData.GameRoom.room_code;

		const requestBody = {
			title: roomTitle,
			is_private: isPrivate,
			round: roundCount,
			room_code: roomCode
		};

		if (isPrivate && password) {
			requestBody.password = password;
		}

		return requestBody;
	};

	const settingRoom = async () => {
		const roomTitle = document.getElementById('room-title').value;
		const roundCount = document.getElementById('round').value;
		const isPrivate = document.getElementById('private').checked;
		const password = document.getElementById('password').value;

		if (!isRoomDataValid(roomTitle, maxPlayers, roundCount, isPrivate, password)) {
			return;
		}
		const requestBody = buildRequestBody(roomTitle,roundCount, isPrivate, password);

		try {
			const response = await fetch("/v1/game-room", {
				method: "PATCH",
				headers: {
					"Authorization": "sunfive",
					"Content-Type": "application/json"
				},
				body: JSON.stringify(requestBody)
			});

			const result = await response.json();
			handleRoomSettingResponse(result);

			window.location.href = "/waiting-room";
		} catch (error) {
			console.error("Error Details:", {
				message: error.message,
				stack: error.stack,
				name: error.name,
			});
			alert("게임방 설정에 실패했습니다.");
		}

	}
	
	
	const handleRoomSettingResponse = (result) => {
		if (result.status !== 201) {
			alert("게임방 설정을 실패하였습니다.");
			return;
		}
		alert("게임방이 성공적으로 설정되었습니다.");
	};

};
