const chat = document.getElementById('chat');
const message = document.getElementById('message');
const send = document.getElementById('send');

let users = null;
let uuid = null;

const roomData = JSON.parse(sessionStorage.getItem("roomData"));
const roomUUID = roomData.GameRoom.room_code;	
const ws = new WebSocket(`${window.location.protocol}//${window.location.host}/ws/wait/${roomUUID}`);

ws.onopen = function() {
	ws.send('TEST_SESSION_ID')
	ws.send(JSON.stringify({ type: 'PLAYER_INFO'}));
};

send.addEventListener('click', sendMessage);
message.addEventListener('keyup', function(event) {
	if (event.key === 'Enter') {
		sendMessage();
	}
});

function sendMessage() {
	const msg = message.value;
	if (msg.trim() !== '' && !message.disabled) {
		ws.send(JSON.stringify({ type: 'MESSAGE', message: msg }));
		message.value = '';
	}
}

ws.onmessage = function(event) {
	const data = JSON.parse(event.data);

	switch (data.type) {
		case 'MESSAGE':
			displayMessage(data.sender, data.profileImage, data.nickname, data.message);
			break;
		case 'SESSION_ID':
			uuid = data.uuid;
			break;
		case 'READY':
			changeReadyState();
			break;
		case 'START':
			gameStart();
			break;
		case 'USER_UPDATE':
			users = data.clientInfo;
			break;
	}
};

function displayMessage(senderId, profileImage, senderName, message) {
	const msgBox = document.createElement('div');
	msgBox.className = "user-msg-container"

	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0');
	const minutes = String(now.getMinutes()).padStart(2, '0');
	const formattedTime = `${hours}:${minutes}`;

	msgBox.innerHTML = `
        <div class="message-content">
			<div class = "message-writer-time">
				<span class="message-writer">${senderId === uuid ? senderName + "(나)" : senderName}</span>
				<span class="message-time">${formattedTime}</span>
			</div>
            <p class="message-text">${message}</p>
        </div>
        <div class="profile-image"></div>
    `;

	const profileImg = msgBox.querySelector('.profile-image');
	const writerTime = msgBox.querySelector('.message-writer-time');
	const writer = msgBox.querySelector('.message-writer');
	const time = msgBox.querySelector('.message-time');
	const text = msgBox.querySelector('.message-text');


	if (senderId === uuid) {
		msgBox.style.justifyContent = "end";
		writerTime.style.justifyContent = "end";
		writer.style.textAlign = "right";
		text.style.textAlign = "right";
		profileImg.style.order = 1;
		time.style.order = -1;
	} else {
		msgBox.style.justifyContent = "start";
		writerTime.style.justifyContent = "start";
		writer.style.textAlign = "left";
		text.style.textAlign = "left";
		profileImg.style.order = -1;
		time.style.order = 1;
	}

	if (typeof profileImage === "string" && profileImage.startsWith("resources/images/Default")) {
	    profileImg.style.backgroundImage = `url('${profileImage}')`;
	} else {
	    profileImg.style.backgroundImage = `url('resources/images/Default01.jpg')`;
	}

	chat.appendChild(msgBox);
	chat.scrollTop = chat.scrollHeight;
}

function changeReadyState(sender, state){
	// 헤딩 유저 레디상태 조회
}

function gameStart(){
	// 화면이동로직
}
s