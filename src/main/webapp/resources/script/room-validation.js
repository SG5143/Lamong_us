export const validateRoomTitle = (roomTitle) => {
	const errEmpty = document.getElementById("empty-title-err");
	if (roomTitle === "") {
		updateErrorElementStyle(errEmpty, true);
		return false;
	} else {
		updateErrorElementStyle(errEmpty, false);
		return true;
	}
};

export const validateMaxPlayers = (maxPlayers) => {
	const errEmpty = document.getElementById("empty-players-err");
	const errLimit = document.getElementById("limit-players-err");

	if (maxPlayers === "") {
		updateErrorElementStyle(errEmpty, true);
		updateErrorElementStyle(errLimit, false);
		return false;
	} else {
		updateErrorElementStyle(errEmpty, false);
	}

	const maxPlayersNum = Number(maxPlayers);
	if (maxPlayersNum < 3 || maxPlayersNum > 8) {
		updateErrorElementStyle(errLimit, true);
		return false;
	} else {
		updateErrorElementStyle(errLimit, false);
		return true;
	}
};

export const validateRoundCount = (roundCount) => {
	const errEmpty = document.getElementById("empty-round-err");
	const errLimit = document.getElementById("limit-round-err");

	if (roundCount === "") {
		updateErrorElementStyle(errEmpty, true);
		updateErrorElementStyle(errLimit, false);
		return false;
	} else {
		updateErrorElementStyle(errEmpty, false);
	}

	const roundCountNum = Number(roundCount);
	if (roundCountNum < 2 || roundCountNum > 5) {
		updateErrorElementStyle(errLimit, true);
		return false;
	} else {
		updateErrorElementStyle(errLimit, false);
		return true;
	}
};

export const validatePassword = (password) => {
	const errEmpty = document.getElementById("empty-password-err");
	const errLimit = document.getElementById("limit-password-err");

	if (password === "") {
		updateErrorElementStyle(errEmpty, true);
		updateErrorElementStyle(errLimit, false);
		return false;
	} else {
		updateErrorElementStyle(errEmpty, false);
	}

	if (password.length !== 4) {
		updateErrorElementStyle(errLimit, true);
		return false;
	} else {
		updateErrorElementStyle(errLimit, false);
		return true;
	}
};

export const validateRoomNumber = (roomNumber) => {
	const errEmpty = document.getElementById("room-num-err");

	if (roomNumber === "") {
		errEmpty.textContent = "방 번호를 입력해주세요.";
		updateErrorElementStyle(errEmpty, true);
		return false;
	} else if (!/^\d+$/.test(roomNumber)) {
		errEmpty.textContent = "숫자만 입력 가능합니다.";
		updateErrorElementStyle(errEmpty, true);
		return false;
	} else {
		updateErrorElementStyle(errEmpty, false);
		return true;
	}
};

function updateErrorElementStyle(element, visible) {
	if (visible) {
		element.style.display = "block";
	} else {
		element.style.display = "none";
	}
}