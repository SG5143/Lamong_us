import { updateErrorElementStyle, validateUsername, validatePassword } from "./validation.js";

window.onload = () => {
	const username = document.getElementById("username");
	const password = document.getElementById("password");
	const form = document.querySelector("form");
	const loginButton = form.querySelector("button[type='submit']");

	loginButton.addEventListener("click", async (e) => {
		e.preventDefault(); 

		const errMsgGroup = document.getElementsByClassName("error-msg");
		for (let i = 0; i < errMsgGroup.length; i++) {
			updateErrorElementStyle(errMsgGroup[i], false); 
		}

		const usernameValue = username.value;
		const passwordValue = password.value;
		let isValid = true;

		if (!usernameValue) {
			const errMsg = document.getElementById("error-msg-username-empty");
			updateErrorElementStyle(errMsg, true);
			isValid = false;
		} else if (!validateUsername(usernameValue)) {
			const errMsg = document.getElementById("error-msg-username-pattern");
			updateErrorElementStyle(errMsg, true); 
			isValid = false;
		}

		if (!passwordValue) {
			const errMsg = document.getElementById("error-msg-password-empty");
			updateErrorElementStyle(errMsg, true); 
			isValid = false;
		} else if (!validatePassword(passwordValue)) {
			const errMsg = document.getElementById("error-msg-password-pattern");
			updateErrorElementStyle(errMsg, true);
			isValid = false;
		}

		if (!isValid) {
			return;
		}

		try {
			const response = await fetch("https://lamong-server.com/v1/members/login", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					username: usernameValue,
					password: passwordValue,
				}),
			});

			if (!response.ok) {
				throw new Error("로그인 실패");
			}

			const data = await response.json();
			if (data.success) {
				alert("로그인 성공!");
				window.location.href = "/main"; 
			} else {
				alert("아이디 또는 비밀번호가 틀립니다."); 
			}
		} catch (error) {
			console.error("Error during login:", error);
			alert("로그인 요청 중 오류가 발생했습니다."); 
		}
	});
};
