import { updateErrorElementStyle, validateUsername, validatePassword } from "./validation.js";

document.addEventListener("DOMContentLoaded", () => {
	const usernameInput = document.getElementById("username-login");
	const passwordInput = document.getElementById("password-login");
	
	const form = document.querySelector("form");
	const loginButton = form.querySelector("button[type='submit']");

	const usernameErrorEmpty = document.getElementById("error-msg-username-empty");
	const usernameErrorPattern = document.getElementById("error-msg-username-pattern");
	const passwordErrorEmpty = document.getElementById("error-msg-password-empty");
	const passwordErrorPattern = document.getElementById("error-msg-password-pattern");

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

	// 로그인 버튼 클릭 이벤트
	loginButton.addEventListener("click", async (e) => {
		e.preventDefault();
		console.log("Login button clicked");
		resetErrorMessages();

		const usernameValue = usernameInput.value;
		const passwordValue = passwordInput.value;

		console.log("username:", usernameValue);
		console.log("password:", passwordValue);

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

		if (!isUsernameValid || !isPasswordValid) {
			return;
		}

		try {
			// 로그인 API 요청
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
				window.location.href = "/lobby";
			} else {
				console.error("Login failed:", data);
				alert(data.message || "아이디 또는 비밀번호가 틀립니다.");
			}
		} catch (error) {
			console.error("Error during login:", error);
			alert("로그인 요청 중 오류가 발생했습니다.");
		}
	});
});
