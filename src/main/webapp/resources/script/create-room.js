import { validateRoomTitle, validateMaxPlayers, validateRoundCount, validatePassword } from './room-validation.js';

const ROOM_TITLES = [
	"방 제목을 입력해주세요.", "신나는 라이어게임"
];

const togglePasswordField = () => {
	const isPrivateCheckbox = document.getElementById('private');
	const passwordField = document.getElementById('password');
	const passwordErrorMsg = document.querySelector('#empty-password-err');
	const passwordLimitErrorMsg = document.querySelector('#limit-password-err');

	const shouldShowPassword = isPrivateCheckbox.checked;
	passwordField.style.display = shouldShowPassword ? 'block' : 'none';

	if (!shouldShowPassword) {
		hidePasswordErrors(passwordErrorMsg, passwordLimitErrorMsg);
	}
};

const hidePasswordErrors = (passwordErrorMsg, passwordLimitErrorMsg) => {
	if (passwordErrorMsg) passwordErrorMsg.style.display = 'none';
	if (passwordLimitErrorMsg) passwordLimitErrorMsg.style.display = 'none';
};

export const initializeRoomCreation = () => {
	const isPrivateCheckbox = document.getElementById('private');
	togglePasswordField();

	isPrivateCheckbox.addEventListener('change', togglePasswordField);
	addModalEventListeners();
	addInputEventListeners();
};

const addModalEventListeners = () => {
	document.getElementById("create-room-btn").addEventListener("click", openRoomModal);
	document.getElementById("close-modal").addEventListener("click", closeRoomModal);
};

const openRoomModal = () => {
	document.getElementById("create-room-modal").style.display = "block";
	const randomTitle = ROOM_TITLES[Math.floor(Math.random() * ROOM_TITLES.length)];
	document.getElementById('room-title').placeholder = randomTitle;
};

const closeRoomModal = () => {
	document.getElementById("create-room-modal").style.display = "none";
	document.getElementById('create-room-form').reset();
	togglePasswordField();
	hideAllErrorMessages();
};

const hideAllErrorMessages = () => {
	const errorElements = document.querySelectorAll(".error-msg");
	errorElements.forEach((element) => element.style.display = "none");
};

const addInputEventListeners = () => {
	document.getElementById('room-title').addEventListener('input', (e) => validateRoomTitle(e.target.value));
	document.getElementById('max-players').addEventListener('input', (e) => validateMaxPlayers(e.target.value));
	document.getElementById('round').addEventListener('input', (e) => validateRoundCount(e.target.value));
	document.getElementById('password').addEventListener('input', (e) => validatePassword(e.target.value));
};

const isRoomDataValid = (roomTitle, maxPlayers, roundCount, isPrivate, password) => {
	let isValid = true;

	if (!validateRoomTitle(roomTitle)) isValid = false;
	if (!validateMaxPlayers(maxPlayers)) isValid = false;
	if (!validateRoundCount(roundCount)) isValid = false;
	if (isPrivate && !validatePassword(password)) isValid = false;

	return isValid;
};

export const createRoom = async (currentPage, fetchRoomList) => {
	const roomTitle = document.getElementById('room-title').value;
	const maxPlayers = document.getElementById('max-players').value;
	const roundCount = document.getElementById('round').value;
	const isPrivate = document.getElementById('private').checked;
	const password = document.getElementById('password').value;

	if (!isRoomDataValid(roomTitle, maxPlayers, roundCount, isPrivate, password)) {
		return;
	}

	const requestBody = buildRequestBody(roomTitle, maxPlayers, roundCount, isPrivate, password);

	try {
		const response = await fetch("/v1/game-room", {
			method: "POST",
			headers: {
				"Authorization": "sunfive",
				"Content-Type": "application/json"
			},
			body: JSON.stringify(requestBody)
		});

		const result = await response.json();
		handleRoomCreationResponse(result);
		
		const data = await fetchRoomByNumber(result.room_number);
		sessionStorage.setItem("roomData", JSON.stringify(data));
		window.location.href = "/waiting-room";
	} catch (error) {
		console.error("Error Details:", {
			message: error.message,
			stack: error.stack,
			name: error.name,
		});    
		alert("게임방 생성에 실패했습니다.");
	}
};

const buildRequestBody = (roomTitle, maxPlayers, roundCount, isPrivate, password) => {
	const uuid = document.getElementById('uuid').value
	
	const requestBody = {
		host_user: uuid,
		title: roomTitle,
		is_private: isPrivate,
		max_players: maxPlayers,
		round: roundCount
	};

	if (isPrivate && password) {
		requestBody.password = password;
	}

	return requestBody;
};

const handleRoomCreationResponse = (result) => {
	if (result.status !== 201) {
		alert("게임방 생성을 실패하였습니다.");
		return;
	}

	alert("게임방이 성공적으로 생성되었습니다.");
	closeRoomModal();
};

const fetchRoomByNumber = async (roomNumber) => {
	try {
		const response = await fetch(`/v1/game-room?room_number=${roomNumber}`);
		const data = await response.json();
		sessionStorage.setItem("roomData", JSON.stringify(data));
		return data;
	} catch (error) {
		console.error("Error fetching room details:", error);
	}
};