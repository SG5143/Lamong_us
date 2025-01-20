
window.onload = () => {
	fetchRoomList(1);
};

const fetchRoomList = async (page = 1) => {
	try {
		const response = await fetch(`/v1/game-room?command=all&page=${page}`);
		if (!response.ok) throw new Error("Failed to fetch room list");

		const data = await response.json();
		updateRoomList(data.GameRoom);
	} catch (error) {
		console.error("Error fetching room list:", error);
		alert("방 리스트를 불러오는 데 실패했습니다.");
	}
};

const updateRoomList = (rooms) => {
	const gameRoomContainer = document.querySelector(".game-room");

	gameRoomContainer.innerHTML = "";

		console.log(rooms);
	rooms.forEach((room) => {
		
		const roomElement = document.createElement("div");
		roomElement.classList.add("room");

		roomElement.innerHTML = `
            <div>
                <p>방번호: ${room.room_number}</p>
                <p>${room.room_title}</p>
            </div>
            <div>
                <div>
                    <div>게임 진행 여부: ${room.max_players} </div>
                    <div>${room.is_private ? "비공개" : "공개"}</div>
                </div>
                <div>
                    <div>인원수: ${room.max_players}</div>
                    <div>icon2</div>
                </div>
            </div>
        `;

		gameRoomContainer.appendChild(roomElement);
	});
};
