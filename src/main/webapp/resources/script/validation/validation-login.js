import { updateErrorElementStyle, validateUsername, validatePassword } from "./validation.js";

document.addEventListener("DOMContentLoaded", () => {

	const form = document.querySelector("form");

	const usernameInput = document.getElementById("username-login");
	const passwordInput = document.getElementById("password-login");

	const usernameErrorEmpty = document.getElementById("error-msg-username-empty");
	const usernameErrorPattern = document.getElementById("error-msg-username-pattern");

	const passwordErrorEmpty = document.getElementById("error-msg-password-empty");
	const passwordErrorPattern = document.getElementById("error-msg-password-pattern");
	const loginButton = form.querySelector("button[type='submit']");

	const resetErrorMessages = () => {
		const errMsgGroup = document.getElementsByClassName("error-msg");
		for (let i = 0; i < errMsgGroup.length; i++) {
			updateErrorElementStyle(errMsgGroup[i], false);
		}
	};

	const validateField = (value, emptyErrId, patternErrId) => {
		if (!value) {
			const errMsg = document.getElementById(emptyErrId);
			updateErrorElementStyle(errMsg, true);
			return false;
		}

		const validateFunc = emptyErrId.includes('username') ? validateUsername : validatePassword;
		if (!validateFunc(value)) {
			const errMsg = document.getElementById(patternErrId);
			updateErrorElementStyle(errMsg, true);
			return false;
		}

		return true;
	};

	form.addEventListener("submit", async (e) => {
		e.preventDefault();
		resetErrorMessages();

		const usernameValue = usernameInput.value;
		const passwordValue = passwordInput.value;

		const isUsernameValid = validateField(
			usernameValue,
			usernameErrorEmpty.id,
			usernameErrorPattern.id
		);

		const isPasswordValid = validateField(
			passwordValue,
			passwordErrorEmpty.id,
			passwordErrorPattern.id
		);

		if (!isUsernameValid || !isPasswordValid)
			return;

		loginButton.disabled = true;

		try {
			const response = await fetch("/v1/members?command=login", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					username: usernameValue,
					password: passwordValue,
				}),
			});

			const data = await response.json();

			if (data.status === 200) {
				alert("로그인 성공!");
				sessionStorage.setItem('nickname', data.nickname);
				sessionStorage.setItem('uuid', data.uuid);
				sessionStorage.setItem('apiKey', data.apiKey);

				window.location.href = "/lobby";
			} else if (data.status === 403) {
				alert("계정이 비활성화되었습니다. 관리자에게 문의하세요.");
			} else {
				console.error("로그인 실패:", data);
				alert(data.message || "아이디 또는 비밀번호가 틀립니다.");
			}
		} catch (error) {
			console.error("Error during login:", error);
			alert("로그인 요청 중 오류가 발생했습니다.");
		} finally {
			loginButton.disabled = false;
		}
	});
});
