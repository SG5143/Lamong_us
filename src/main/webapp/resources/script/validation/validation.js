export function updateErrorElementStyle(element, visible) {
	if (visible) {
		element.style.display = "block";
	} else {
		element.style.display = "none";
	}
}

export function resetErrorMessages() {
	const errorMessages = document.querySelectorAll(".error-message");
	errorMessages.forEach((msg) => {
		msg.style.display = "none";
	});
}

export const validateUsername = (username) => {
	const usernamePattern = /^[a-zA-Z0-9-_!@#$%^&*\.]{5,20}$/;
	return usernamePattern.test(username);
};

export const validatePassword = (password) => {
	const passwordPattern = /^(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,20}$/;
	return passwordPattern.test(password);
};

export const validateNickname = (nickname) => {
	const nicknamePattern = /^[a-zA-Z0-9가-힣]{2,12}$/;
	return nicknamePattern.test(nickname);
};

export const validateEmail = (email) => {
	const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	return emailPattern.test(email);
};

export const validatePhone = (phone) => {
	const phonePattern = /^(010-\d{4}-\d{4}|010\d{8})$/;
	return phonePattern.test(phone);
};

export const formatPhoneString = (phone) => {
	return phone.replace(/[^\d]/g, '');
};

export const checkPassword = () => {
	const newPassword = document.getElementById("new-password");
	const confirmPassword = document.getElementById("confirm-password");

	if (newPassword.value.trim() !== confirmPassword.value.trim())
		return false;


	return true;
};


export async function checkCurrentPassword(currentPassword) {
	let userUuid = sessionStorage.getItem('uuid');

	if (!userUuid) {
		console.error("사용자가 로그인되지 않았습니다.");
		return false;
	}

	try {
		const response = await fetch("http://localhost:8080/v1/members?command=check-password", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				uuid: userUuid,
				password: currentPassword,
			}),
		});

		if (!response.ok) {
			console.error("서버 오류:", response.status);
			return false;
		}

		const data = await response.json();

		if (data.isValid === undefined) {
			console.error("서버 응답이 유효한 isValid 값을 포함하지 않습니다.");
			return false;
		}

		return data.isValid;
	} catch (error) {
		console.error("현재 비밀번호 확인 오류:", error);
		return false;
	}
}


export async function checkDuplUsername(username) {
	try {
		const response = await fetch("/v1/members?command=search-username", {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify({
				"username": username
			})
		});
		if (!response.ok) {
			console.error("서버 오류:", response.status);
			return false;
		}

		const json = await response.json();
		return json.isValid;
	} catch (error) {
		console.error("닉네임 중복 확인 요청 실패:", error);
		return false;
	}
}

export async function checkDuplNickname(nickname) {
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



