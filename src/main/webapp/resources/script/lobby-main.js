import { createRoom, initializeRoomCreation } from './create-room.js';
import { validateRoomNumber } from './room-validation.js';

let currentPage = 1;
let pageableCount;
let roomNumberInput;
let passwordField;

window.onload = () => {
	roomNumberInput = document.getElementById('room-num-input');
	passwordField = document.getElementById('password-field');

	fetchRoomList(currentPage);
	setupEventListeners();
	initializeRoomCreation();
};

const setupEventListeners = () => {
	addEventListenerById("submit-btn", "click", handleCreateRoom);
	addEventListenerById("join-room-form", "submit", handleJoinRoomSubmit);
	addEventListenerById("prev-btn", "click", () => changePage(-1));
	addEventListenerById("next-btn", "click", () => changePage(1));
	addEventListenerById("enter-room-btn", "click", openRoomModal);
	addEventListenerById("close-modal", "click", closeRoomModal);
	addEventListenerById('room-num-input', 'input', (e) => validateRoomNumber(e.target.value));
	addEventListenerById("quick-enter-btn", "click", handleQuickEnter);
	addEventListenerById("refresh-btn", "click", () => fetchRoomList(currentPage))
};

const addEventListenerById = (id, event, handler) => {
	document.getElementById(id).addEventListener(event, handler);
};

const handleCreateRoom = async (e) => {
	e.preventDefault();
	await createRoom(currentPage, fetchRoomList);
};

const handleJoinRoomSubmit = async (e) => {
	e.preventDefault();
	const roomNumber = roomNumberInput.value.trim();

	if (validateRoomNumber(roomNumber)) {
		const data = await fetchRoomByNumber(roomNumber);
		handleRoomDetails(data.GameRoom);
	}
};

const fetchRoomList = async (page = 1) => {
	try {
		const response = await fetch(`/v1/game-room?command=all&page=${page}`);
		if (!response.ok) throw new Error("Failed to fetch room list");

		const data = await response.json();
		console.log(data);
		pageableCount = data.Meta.pageable_count;
		updateRoomList(data.GameRoom);
	} catch (error) {
		console.error(error);
		alert("방 리스트를 불러오는 데 실패했습니다.");
	}
};

const fetchRoomByNumber = async (roomNumber) => {
	try {
		const response = await fetch(`/v1/game-room?room_number=${roomNumber}`);
		if (!response.ok) {
			if (response.status === 400 || response.status === 404) {
				showErrorModal("없는 방 번호입니다.");
			}
			return;
		}
		const data = await response.json();
		sessionStorage.setItem("roomData", JSON.stringify(data));
		return data;
	} catch (error) {
		console.error("Error fetching room details:", error);
	}
};

const handleRoomDetails = (room) => {
	roomNumberInput.disabled = true;

	if (room.is_private) {
		passwordField.style.display = 'block';
		const joinRoomBtn = document.getElementById('join-room-btn');
		joinRoomBtn.addEventListener('click', clickJoinRoomButton(room));
	}
	else
		joinRoom(room.room_code);
};

const clickJoinRoomButton = (room) => {
	const inputPw = document.getElementById('room-password');

	if (inputPw.value.trim() === "" || inputPw.style.display === "none") {
		return;
	}

	if (inputPw.value === room.password) {
		passwordField.style.display = 'none';
		joinRoom(room.room_code);
	} else {
		document.getElementById('room-password-err').style.display = 'block';
	}
};

const joinRoom = async (roomCode) => {
	const authorizationToken = "sunsun";
	const requestBody = { room_code: roomCode };

	try {
		const response = await fetch("/v1/game-room?command=attend-log", {
			method: "POST",
			headers: {
				"Authorization": authorizationToken,
				"Content-Type": "application/json"
			},
			body: JSON.stringify(requestBody)
		});
		
		const result = await response.json();
		if (result.status === 401) {
			alert("로그인이 필요합니다.");
			window.location.href = "/";
		}

		if (result.message === "참가 기록이 성공적으로 저장되었습니다." || result.message === "참가 시간이 성공적으로 업데이트되었습니다.") {
			handleRoomJoinResponse(result);
		}

	} catch (error) {
		console.error("Error Details:", { message: error.message, stack: error.stack, name: error.name });
		alert("방 입장에 실패했습니다.");
	}
};


const handleRoomJoinResponse = (result) => {
	if (result.status === 200) {
		closeErrorModal();
		closeRoomModal();
		window.location.href = "/waiting-room";
	} else {
		closeRoomModal();
		showErrorModal("없는 방 번호입니다.");
	}
};

const updateRoomList = (rooms) => {
	const gameRoomContainer = document.querySelector(".game-room");
	gameRoomContainer.innerHTML = "";

	rooms.forEach((room) => {
		const roomWrapper = createRoomWrapper(room);
		gameRoomContainer.appendChild(roomWrapper);
	});
};

const createRoomWrapper = (room) => {
	const roomWrapper = document.createElement("div");
	roomWrapper.classList.add("room-wrapper");

	const roomElement = createRoomElement(room);
	roomWrapper.appendChild(roomElement);

	return roomWrapper;
};

const createRoomElement = (room) => {
	const roomElement = document.createElement("button");
	roomElement.classList.add("room");

	roomElement.setAttribute("data-room-number", room.room_number);

	const { borderColor, stateText } = getRoomStateStyles(room);

	roomElement.style.border = `3px solid ${borderColor}`;
	roomElement.innerHTML = `
        <div id="room-top">
            <p>${room.room_number}</p>
            <p>${room.room_title}</p>
        </div>
        <div>
            <div>${stateText} / ${room.is_private ? "비번방" : "공개"}</div>
            <div>인원수: ${room.current_players} / ${room.max_players}</div>
        </div>
    `;

	roomElement.addEventListener("click", () => handleRoomClick(room.room_number));
	return roomElement;
};

const handleRoomClick = async (roomNumber) => {
	try {
		const data = await fetchRoomByNumber(roomNumber);
		if (data && data.GameRoom) {
			const room = data.GameRoom;
			if (room.is_private) {
				openRoomModal();

				roomNumberInput.value = room.room_number;
				roomNumberInput.disabled = true;

				const tit = document.querySelector("#modal-tit");
				if (roomNumberInput.disabled) {
					tit.textContent = "비밀방 입장";
				} else {
					const tit = document.querySelector("#modal-tit");
					tit.textContent = "방번호 입장";
				}

				passwordField.style.display = 'block';

				const joinRoomBtn = document.getElementById('join-room-btn');
				joinRoomBtn.onclick = () => {
					const roomPassword = document.getElementById('room-password').value.trim();
					if (roomPassword === room.password) {
						joinRoom(room.room_code);
						closeRoomModal();
					}
				};
			} else {
				joinRoom(room.room_code);
			}
		} else {
			alert("방 정보를 가져오는 데 실패했습니다.");
		}
	} catch (error) {
		console.error(error);
	}
};

const getRoomStateStyles = (room) => {
	let borderColor = "";
	let stateText = "";
	const full = room.current_players === room.max_players;

	if (full && room.state === "ready" || room.state === "full") {
		borderColor = "#DE3A86";
		stateText = "인원초과";
	} else if (room.state === "ready") {
		borderColor = "#3ADE3C";
		stateText = "대기중";
	} else if (room.state === "playing") {
		borderColor = "#161719";
		stateText = "게임중";
	}

	return { borderColor, stateText };
};

const changePage = (direction) => {
	if ((direction === -1 && currentPage <= 1) || (direction === 1 && currentPage >= pageableCount)) {
		alert(direction === -1 ? "첫 페이지입니다." : "마지막 페이지입니다.");
		return;
	}

	currentPage += direction;
	fetchRoomList(currentPage);
};

const openRoomModal = () => toggleRoomModalVisibility(true);

const closeRoomModal = () => {
	toggleRoomModalVisibility(false);
	passwordField.style.display = 'none';
	document.getElementById('room-password').value = '';

	const tit = document.querySelector("#modal-tit");
	tit.textContent = "방번호 입장";

	roomNumberInput.disabled = false;
	resetJoinRoomForm();
};

const resetJoinRoomForm = () => {
	const joinRoomForm = document.getElementById("join-room-form");
	joinRoomForm.reset();
};

const toggleRoomModalVisibility = (isOpen) => {
	const createRoomModal = document.getElementById("create-room-modal");
	const createRoomForm = document.getElementById("create-room-form");
	const joinRoomForm = document.getElementById("join-room-form");

	createRoomModal.style.display = isOpen ? "block" : "none";
	createRoomForm.style.display = isOpen ? "none" : "block";
	joinRoomForm.style.display = isOpen ? "block" : "none";
};

const showErrorModal = (message) => {
	const errorMessageElement = document.getElementById("empty-err-msg");
	const errorModal = document.getElementById("err-modal");

	errorMessageElement.textContent = message;
	errorModal.style.display = "block";

	addEventListenerById("close-err-modal", "click", closeErrorModal);
};

const closeErrorModal = () => {
	const errorModal = document.getElementById("err-modal");
	errorModal.style.display = "none";
};

const handleQuickEnter = async () => {
	try {
		let availableRooms = [];
		let currentPage = 1;

		while (currentPage <= pageableCount) {
			const response = await fetch(`/v1/game-room?command=all&page=${currentPage}`);
			if (!response.ok) throw new Error("방 목록을 가져올 수 없습니다.");

			const data = await response.json();
			const roomsOnPage = data.GameRoom.filter(room =>
				room.state === "ready" &&
				room.current_players < room.max_players &&
				!room.is_private
			);

			if (roomsOnPage.length > 0) {
				availableRooms = [...availableRooms, ...roomsOnPage];
				break;
			}
			currentPage++;
		}

		if (availableRooms.length === 0) {
			alert("입장 가능한 방이 없습니다.");
			return;
		}

		const randomRoom = availableRooms[Math.floor(Math.random() * availableRooms.length)];
		joinRoom(randomRoom.room_code);
	} catch (error) {
		console.error("Error fetching room list:", error);
		alert("방 리스트를 불러오는 데 실패했습니다.");
	}
};
