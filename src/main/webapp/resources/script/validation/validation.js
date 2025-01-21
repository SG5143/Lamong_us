export function updateErrorElementStyle(element, visible) {
	if (visible) {
		element.style.display = "block";
	} else {
		element.style.display = "none";
	}
}

export const validateUsername = (username) => {
	const usernamePattern = /^[a-zA-Z0-9-_!@#$%^&*\.]{5,20}$/;
	return usernamePattern.test(username);
};

export const validatePassword = (password) => {
	const passwordPattern = /^(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,20}$/;
	return passwordPattern.test(password);
};

export const validateName = (nickname) => {
	const nicknamePattern = /^[a-zA-Z0-9가-힣]{2,12}$/;
	return nicknamePattern.test(nickname);
};

export const validateEmail = (email) => {
	const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	return emailPattern.test(email);
};

export const validatePhone = (phone) => {
	const phonePattern = /^010-\d{4}-\d{4}$/;
	return phonePattern.test(phone);
};

export async function checkDuplUsername(username) {
	const response = await fetch("/v1/members?command=search-username", {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"username": username
		})
	});
	const json = await response.json();

	return json.isValid;
}

export async function checkDuplnickname(nickname) {
	const response = await fetch("/v1/members?command=search-nickname", {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"nickname": nickname
		})
	});
	const json = await response.json();

	return json.isValid;
}

export async function checkDuplPhone(phone) {
	const response = await fetch("/v1/members?command=search-phone", {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"phone": phone
		})
	});
	const json = await response.json();

	return json.isValid;
}

export async function checkDuplEmail(email) {
	const response = await fetch("/v1/members?command=search-email", {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify({
			"email": email
		})
	});
	const json = await response.json();

	return json.isValid;
}



