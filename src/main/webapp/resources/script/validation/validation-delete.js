import { checkCurrentPassword, updateErrorElementStyle } from "./validation.js"; 

document.addEventListener("DOMContentLoaded", () => {
    const confirmDeleteButton = document.getElementById("confirm-delete-button");

    if (confirmDeleteButton) {
        confirmDeleteButton.addEventListener("click", async () => {
            const password = document.getElementById("delete-password").value;
            const errorMessageElement = document.getElementById("err-msg-password-mismatch");

            if (!password) {
                alert("비밀번호를 입력해주세요.");
                return;
            }

            const apiKey = sessionStorage.getItem("apiKey");

            if (!apiKey) {
                console.error("API 키가 없습니다.");
                alert("API 키가 존재하지 않습니다.");
                return;
            }

            const isValidPassword = await checkCurrentPassword(password);

            if (!isValidPassword) {
                alert("현재 비밀번호가 일치하지 않습니다.");
                updateErrorElementStyle(errorMessageElement, true); 
                return;
            } else {
                updateErrorElementStyle(errorMessageElement, false); 
            }

            const userUuid = sessionStorage.getItem("uuid");

            try {
                const response = await fetch("/v1/members?command=delete", {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${apiKey}`
                    },
                    body: JSON.stringify({
                        password: password,
                        uuid: userUuid
                    })
                });

                if (response.ok) {
                    const result = await response.json();
                    alert(result.message);
                    window.location.href = "/main";
                } else {
                    const errorResult = await response.json();
                    alert(`회원 탈퇴 실패: ${errorResult.message}`);
                }
            } catch (error) {
                console.error("Error during account deletion:", error);
                alert("서버와의 통신 중 문제가 발생했습니다.");
            }
        });
    }
});