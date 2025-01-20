export function updateErrorElementStyle(element, visible) {
	if (visible) {
		element.style.display = "block";
	} else {
		element.style.display = "none";
	}
}

// 아이디 유효성 검사
export const validateUsername = (username) => {
	const usernamePattern = /^[a-zA-Z0-9-_!@#$%^&*\.]{5,20}$/;
	return usernamePattern.test(username);
};

// 비밀번호 유효성 검사
export const validatePassword = (password) => {
	const passwordPattern = /^(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,20}$/;
	return passwordPattern.test(password);
};
