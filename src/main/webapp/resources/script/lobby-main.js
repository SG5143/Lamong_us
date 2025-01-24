import { createRoom, initializeRoomCreation } from './create-room.js';
import { validateRoomNumber } from './room-validation.js';

let currentPage = 1;
let pageableCount;

window.onload = () => {
	fetchRoomList(currentPage);
	setupEventListeners();
	initializeRoomCreation();
	
	

};

const setupEventListeners = () => {
	addEventListenerById("submitButton", "click", handleCreateRoom);
	addEventListenerById("joinRoomForm", "submit", handleJoinRoomSubmit);
	addEventListenerById("prevPageBtn", "click", () => changePage(-1));
	addEventListenerById("nextPageBtn", "click", () => changePage(1));
	addEventListenerById("enterRoomNumberBtn", "click", openRoomModal);
	addEventListenerById("closeModal", "click", closeRoomModal);
	addEventListenerById('roomNumber', 'input', (e) => validateRoomNumber(e.target.value));
};

const addEventListenerById = (id, event, handler) => {
	document.getElementById(id).addEventListener(event, handler);
};

const handleCreateRoom = async (e) => {
	e.preventDefault();
	await createRoom(currentPage, fetchRoomList);
};

const handleJoinRoomSubmit = async(e) => {
	e.preventDefault();
	const roomNumber = document.getElementById('roomNumber').value.trim();

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
		pageableCount = data.Meta.pageable_count;
		updateRoomList(data.GameRoom);
	} catch (error) {
		console.error("Error fetching room list:", error);
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
		return data;
	} catch (error) {
		console.error("Error fetching room details:", error);
	}
};

const handleRoomDetails = (room) => {
	const inputRoomNum = document.getElementById('roomNumber');
	inputRoomNum.disabled = true;

	if (room.is_private){
		document.getElementById('passwordField').style.display = 'block';
		const joinRoomBtn = document.getElementById('joinRoomBtn');
		joinRoomBtn.addEventListener('click', clickJoinRoomButton(room));
	}
	else
		joinRoom(room.room_code);
};

const clickJoinRoomButton = (room) => {
	const inputPw = document.getElementById('roomPassword');

	if (inputPw.value.trim() === "" || inputPw.style.display === "none") {
		return;
	}

	if (inputPw.value === room.password) {
		document.getElementById('passwordField').style.display = 'none';
		joinRoom(room.room_code);
	} else {
		document.getElementById('roomPasswordError').style.display = 'block';
	}
};

const joinRoom = async (roomCode) => {
	const userCode = "02e192fa-d481-11ef-b3f2-025c4feb1d05";
	const authorizationToken = "sunsun";
	const requestBody = { user_code: userCode, room_code: roomCode };

	try {
		const response = await fetch("/v1/game-room?command=attend-log", {
			method: "POST",
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
		handleRoomJoinResponse(result);
	} catch (error) {
		console.error("Error Details:", { message: error.message, stack: error.stack, name: error.name });
		alert("방 입장에 실패했습니다.");
	}
};

const handleRoomJoinResponse = (result) => {
	if (result.status === 200) {
		closeErrorModal();
		closeRoomModal();
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

	roomElement.addEventListener("click", (e) => {
		const roomNumber = e.currentTarget.getAttribute("data-room-number");
		console.log("클릭한 방 번호:", roomNumber);

		fetchRoomByNumber(roomNumber);
	});

	return roomElement;
};

const getRoomStateStyles = (room) => {
	let borderColor = "";
	let stateText = "";
	const full = room.current_players === room.max_players;

	if (room.state === "ready") {
		borderColor = "#3ADE3C";
		stateText = "대기중";
	} else if (room.state === "playing") {
		borderColor = "#161719";
		stateText = "게임중";
	} else if ((room.state === "ready" && full) || room.state === "full") {
		borderColor = "#DE3A86";
		stateText = "인원초과";
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
	document.getElementById('passwordField').style.display = 'none';
	document.getElementById('roomPassword').value = '';

	const roomNumberInput = document.getElementById('roomNumber');
	roomNumberInput.disabled = false;
	resetJoinRoomForm();
};

const resetJoinRoomForm = () => {
	const joinRoomForm = document.getElementById("joinRoomForm");
	joinRoomForm.reset();
};

const toggleRoomModalVisibility = (isOpen) => {
	const createRoomModal = document.getElementById("createRoomModal");
	const createRoomForm = document.getElementById("createRoomForm");
	const joinRoomForm = document.getElementById("joinRoomForm");

	createRoomModal.style.display = isOpen ? "block" : "none";
	createRoomForm.style.display = isOpen ? "none" : "block";
	joinRoomForm.style.display = isOpen ? "block" : "none";
};

const showErrorModal = (message) => {
	const errorMessageElement = document.getElementById("errorMessage");
	const errorModal = document.getElementById("errorModal");

	errorMessageElement.textContent = message;
	errorModal.style.display = "block";

	addEventListenerById("closeErrorModal", "click", closeErrorModal);
};

const closeErrorModal = () => {
	const errorModal = document.getElementById("errorModal");
	errorModal.style.display = "none";
};
