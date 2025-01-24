import { validateRoomTitle, validateMaxPlayers, validateRoundCount, validatePassword } from './room-validation.js';

const ROOM_TITLES = [
	"TEST 1", "TEST 2"
];

const togglePasswordField = () => {
	const isPrivateCheckbox = document.getElementById('isPrivate');
	const passwordField = document.getElementById('password');
	const passwordErrorMsg = document.querySelector('#passwordError');
	const passwordLimitErrorMsg = document.querySelector('#passwordLimitError');

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
	const isPrivateCheckbox = document.getElementById('isPrivate');
	togglePasswordField();

	isPrivateCheckbox.addEventListener('change', togglePasswordField);
	addModalEventListeners();
	addInputEventListeners();
};

const addModalEventListeners = () => {
	document.getElementById("createRoomBtn").addEventListener("click", openRoomModal);
	document.getElementById("closeModal").addEventListener("click", closeRoomModal);
};

const openRoomModal = () => {
	document.getElementById("createRoomModal").style.display = "block";
	const randomTitle = ROOM_TITLES[Math.floor(Math.random() * ROOM_TITLES.length)];
	document.getElementById('roomTitle').placeholder = randomTitle;
};

const closeRoomModal = () => {
	document.getElementById("createRoomModal").style.display = "none";
	document.getElementById('createRoomForm').reset();
	togglePasswordField();
	hideAllErrorMessages();
};

const hideAllErrorMessages = () => {
	const errorElements = document.querySelectorAll(".error-msg");
	errorElements.forEach((element) => element.style.display = "none");
};

const addInputEventListeners = () => {
	document.getElementById('roomTitle').addEventListener('input', (e) => validateRoomTitle(e.target.value));
	document.getElementById('maxPlayers').addEventListener('input', (e) => validateMaxPlayers(e.target.value));
	document.getElementById('roundCount').addEventListener('input', (e) => validateRoundCount(e.target.value));
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
	const roomTitle = document.getElementById('roomTitle').value;
	const maxPlayers = document.getElementById('maxPlayers').value;
	const roundCount = document.getElementById('roundCount').value;
	const isPrivate = document.getElementById('isPrivate').checked;
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
		handleRoomCreationResponse(result, currentPage, fetchRoomList);
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
	const requestBody = {
		host_user: "ebbf307b-cf32-11ef-9079-025c4feb1d05",
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

const handleRoomCreationResponse = (result, currentPage, fetchRoomList) => {
	if (result.status !== 201) {
		alert(`게임방 생성 실패: ${result.message1}`);
		return;
	}

	alert("게임방이 성공적으로 생성되었습니다.");
	fetchRoomList(currentPage);
	closeRoomModal();
};
