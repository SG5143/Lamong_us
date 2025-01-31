import {
	updateErrorElementStyle,
	validatePassword,
	validateNickname,
	validatePhone,
	validateEmail,
	checkPassword,
	checkDuplPhone,
	checkDuplEmail,
	checkDuplNickname,
	checkCurrentPassword,
} from "./validation.js";

window.onload = () => {
	const userUuid = sessionStorage.getItem('uuid');

	const elements = {
		currentPassword: document.getElementById("current-password"),
		newPassword: document.getElementById("new-password"),
		confirmPassword: document.getElementById("confirm-password"),
		nickname: document.getElementById("new-nickname"),
		phone: document.getElementById("new-phone"),
		email: document.getElementById("new-email"),
		profileInfoTextarea: document.getElementById("profile-info"),
		profileImageInput: document.getElementById("profile-image"),
		saveProfileInfoButton: document.getElementById("save-profile-info"),
		saveProfileImageButton: document.getElementById("save-profile-image"),
		saveUserData: document.getElementById("save-user-data"),
	};

	const isValid = {
		passwordGroup: true,
		nickname: false,
		phone: false,
		email: false,
	};

	const checkAllFieldsEmpty = () => {
		const { newPassword, nickname, phone, email } = elements;
		return !newPassword.value.trim() && !nickname.value.trim() && !phone.value.trim() && !email.value.trim();
	};

	const resetErrorMessages = () => {
		document.querySelectorAll(".error-msg-group").forEach((msg) => {
			msg.style.display = "none";
		});
	};

	const handleValidation = async (input, validateFn, duplicateFn, errorIds, field) => {
		resetErrorMessages();

		const { patternId, duplicateId } = errorIds;
		const patternElement = document.getElementById(patternId);
		if (!patternElement) {
			console.error(`Element with ID '${patternId}' not found`);
			return false;
		}

		const isPatternValid = validateFn ? validateFn(input.value) : true;
		updateErrorElementStyle(patternElement, !isPatternValid);
		if (!isPatternValid) {
			isValid[field] = false;
			return false;
		}

		if (duplicateFn) {
			const duplicateElement = document.getElementById(duplicateId);
			if (!duplicateElement) {
				console.error(`Element with ID '${duplicateId}' not found`);
				return false;
			}
			const isDuplicateValid = await duplicateFn(input.value);
			updateErrorElementStyle(duplicateElement, !isDuplicateValid);
			if (!isDuplicateValid) {
				isValid[field] = false;
				return false;
			}
		}

		isValid[field] = true;
		return true;
	};

	const setInputEventListener = (inputElement, validateFn, duplicateFn, errorIds) => {
		inputElement.addEventListener('input', async () => {
			const { patternId, duplicateId } = errorIds;
			const patternElement = document.getElementById(patternId);

			if (patternElement) {
				const isPatternValid = validateFn ? validateFn(inputElement.value) : true;
				updateErrorElementStyle(patternElement, !isPatternValid);
				if (!isPatternValid) return;
			}

			if (duplicateFn) {
				const duplicateElement = document.getElementById(duplicateId);
				const isDuplicateValid = await duplicateFn(inputElement.value);
				updateErrorElementStyle(duplicateElement, !isDuplicateValid);
			}
		});
	};

	const setupEventListeners = () => {
		const { currentPassword, newPassword, confirmPassword, nickname, email, phone } = elements;

		setInputEventListener(currentPassword, checkCurrentPassword, checkCurrentPassword, {
			patternId: "error-msg-current-password-pattern",
			duplicateId: "err-msg-password-mismatch",
		}, "currentPassword");

		setInputEventListener(newPassword, validatePassword, null, {
			patternId: "error-msg-new-password-pattern"
		}, "newPassword");

		setInputEventListener(confirmPassword, checkPassword, null, {
			patternId: "error-msg-confirm-password-match"
		}, "confirmPassword");

		setInputEventListener(nickname, validateNickname, checkDuplNickname, {
			patternId: "error-msg-nickname-pattern",
			duplicateId: "error-msg-nickname-duplicate",
		}, "nickname");

		setInputEventListener(email, validateEmail, checkDuplEmail, {
			patternId: "error-msg-email-pattern",
			duplicateId: "error-msg-email-duplicate",
		}, "email");

		setInputEventListener(phone, validatePhone, checkDuplPhone, {
			patternId: "error-msg-phone-pattern",
			duplicateId: "error-msg-phone-duplicate",
		}, "phone");
	};

	// 프로필 이미지 저장 기능
	const saveProfileImage = async () => {
		const profileImage = elements.profileImageInput.files[0];
		if (!profileImage) {
			alert("프로필 이미지를 선택해주세요.");
			return;
		}

		try {
			const response = await fetch("/v1/members?command=update", {
				method: "PATCH",
				headers: {
					"Content-Type": "application/json",
					"Authorization": `Bearer ${apiKey}`
				},
				body: JSON.stringify({
					uuid: userUuid,
					profile_info: profileImage
				}),
			});

			if (response.status === 200 || response.status === 201) {
				alert("프로필 이미지가 성공적으로 변경되었습니다.");
			} else {
				alert("프로필 이미지 변경에 실패했습니다.");
			}
		} catch (error) {
			console.error("Error updating profile image:", error);
		}
	};

	// 유저 자기소개 수정, 저장
	const saveProfileInfo = async () => {
		const profileInfo = elements.profileInfoTextarea.value;
		console.log("Sending profile info:", profileInfo);

		const apiKey = sessionStorage.getItem('apiKey');

		try {
			const response = await fetch("/v1/members?command=update", {
				method: "PATCH",
				headers: {
					"Content-Type": "application/json",
					"Authorization": `Bearer ${apiKey}`
				},
				body: JSON.stringify({
					uuid: userUuid,
					profile_info: profileInfo
				}),
			});

			if (response.status === 200 || response.status === 201) {
				alert("자기소개가 성공적으로 저장되었습니다.");

			} else {
				alert("자기소개 저장에 실패했습니다.");
			}
		} catch (error) {
			console.error("Error saving profile info:", error);
		}
	};

	// 유저 회원정보 수정 
	const saveUserData = async (e) => {
		e.preventDefault();

		if (checkAllFieldsEmpty()) {
			alert("변경할 정보가 없습니다.");
			return;
		}

		const { currentPassword, newPassword, confirmPassword, nickname, email, phone } = elements;
		const validationPromises = [];

		if (currentPassword.value.trim()) {
			validationPromises.push(
				handleValidation(currentPassword, checkCurrentPassword, checkCurrentPassword, {
					patternId: "error-msg-current-password-pattern",
					duplicateId: "err-msg-password-mismatch",
				}, "currentPassword")
			);
		}

		if (newPassword.value.trim()) {
			validationPromises.push(
				handleValidation(newPassword, validatePassword, null, {
					patternId: "error-msg-new-password-pattern"
				}, "newPassword")
			);
		}

		if (confirmPassword.value.trim()) {
			validationPromises.push(
				handleValidation(confirmPassword, checkPassword, null, {
					patternId: "error-msg-confirm-password-match"
				}, "confirmPassword")
			);
		}

		if (nickname.value.trim()) {
			validationPromises.push(
				handleValidation(nickname, validateNickname, checkDuplNickname, {
					patternId: "error-msg-nickname-pattern",
					duplicateId: "error-msg-nickname-duplicate",
				}, "nickname")
			);
		}

		if (email.value.trim()) {
			validationPromises.push(
				handleValidation(email, validateEmail, checkDuplEmail, {
					patternId: "error-msg-email-pattern",
					duplicateId: "error-msg-email-duplicate",
				}, "email")
			);
		}

		if (phone.value.trim()) {
			validationPromises.push(
				handleValidation(phone, validatePhone, checkDuplPhone, {
					patternId: "error-msg-phone-pattern",
					duplicateId: "error-msg-phone-duplicate",
				}, "phone")
			);
		}

		const validationResults = await Promise.all(validationPromises);
		if (validationResults.includes(false)) {
			alert("입력한 정보가 유효하지 않습니다. 다시 확인해주세요.");
			return;
		}

		const formData = {
			uuid: userUuid,
			password: newPassword.value.trim() || null,
			nickname: nickname.value.trim() || null,
			phone: phone.value.trim() || null,
			email: email.value.trim() || null,
		};

		const apiKey = sessionStorage.getItem('apiKey');

		try {
			const response = await fetch("/v1/members?command=update", {
				method: "PATCH",
				headers: {
					"Content-Type": "application/json",
					"Authorization": `Bearer ${apiKey}`
				},
				body: JSON.stringify(formData),
			});

			if (response.status === 200 || response.status === 201) {
				alert("회원 정보가 성공적으로 수정되었습니다.");
				window.location.reload();
			} else {
				alert("회원 정보 수정에 실패했습니다.");
			}
		} catch (error) {
			console.error("회원 정보 업데이트 실패:", error);
		}
	};

	setupEventListeners();
	elements.saveProfileImageButton.addEventListener("click", saveProfileImage);
	elements.saveProfileInfoButton.addEventListener("click", saveProfileInfo);
	elements.saveUserData.addEventListener("click", saveUserData);
};