export const validateRoomTitle = (roomTitle) => {
	const errEmpty = document.getElementById("roomTitleError");
	if (roomTitle === "") {
		updateErrorElementStyle(errEmpty, true);
		return false;
	} else {
		updateErrorElementStyle(errEmpty, false);
		return true;
	}
};

export const validateMaxPlayers = (maxPlayers) => {
	const errEmpty = document.getElementById("maxPlayersError");
	const errLimit = document.getElementById("maxPlayersLimitError");

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
	const errEmpty = document.getElementById("roundCountError");
	const errLimit = document.getElementById("roundCountLimitError");

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
	const errEmpty = document.getElementById("passwordError");
	const errLimit = document.getElementById("passwordLimitError");

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

function updateErrorElementStyle(element, visible) {
	if (visible) {
		element.style.display = "block";
	} else {
		element.style.display = "none";
	}
}