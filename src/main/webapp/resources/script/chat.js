const chat = document.getElementById('chat');
const message = document.getElementById('message');
const send = document.getElementById('send');

let players = null;
let userUUID = null;

const roomUUID = 'room1';
//const roomUUID = document.getElementById("roomUUID").value;

const ws = new WebSocket(`ws://localhost:8080/ws/wait/${roomUUID}`);

ws.onopen = function() {
	ws.send('TEST_SESSION_ID')
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
			displayMessage(data.senderUUID, data.senderNickname, data.message);
			break;
	}
};

function displayMessage(senderId, senderName, message) {
	const msgBox = document.createElement('div');
	msgBox.className = "user-msg-container"

	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0');
	const minutes = String(now.getMinutes()).padStart(2, '0');
	const formattedTime = `${hours}:${minutes}`;

	msgBox.innerHTML = `
        <div class="message-content">
			<div class = "message-writer-time">
				<span class="message-writer">${senderId === userUUID ? senderName + "(나)" : senderName}</span>
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

	if (senderId === userUUID) {
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

	players.forEach(player => {
		if (player.uuid === senderId) {
			if (player.profileImage.startsWith("resources/images/Default")) {
				profileImg.style.backgroundImage = `url('${player.profileImage}')`;
			} else {
				profileImg.style.backgroundImage = `url('data:image/png;base64,${player.profileImage}')`;
			}
		}
	});

	chat.appendChild(msgBox);
	chat.scrollTop = chat.scrollHeight;
}

