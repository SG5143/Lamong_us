import { createRoom, initializeRoomCreation } from './create-room.js';

let currentPage = 1;
let pageableCount;

window.onload = () => {
	fetchRoomList(1);
	document.getElementById("submit-button").addEventListener("click", async (e) => {
		e.preventDefault();
		await createRoom(currentPage, fetchRoomList);
	});

	initializeRoomCreation();
};

const fetchRoomList = async (page = 1) => {
	try {
		const response = await fetch(`/v1/game-room?command=all&page=${page}`);
		if (!response.ok) throw new Error("Failed to fetch room list");

		const data = await response.json();
		pageableCount = data.Meta.pageable_count;

		updateRoomList(data.GameRoom);
	} catch (error) {
		console.error("Error fetching room list:", error);
		alert("방 리스트를 불러오는 데 실패했습니다.");
	}
};

const updateRoomList = (rooms) => {
	console.log(rooms);
	const gameRoomContainer = document.querySelector(".game-room");

	gameRoomContainer.innerHTML = "";

	rooms.forEach((room) => {
		const roomElement = document.createElement("div");
		roomElement.classList.add("room");

		let borderColor = "";
		let stateText = "";
		const full = room.current_players == room.max_players;

		if (room.state === "ready") {
			borderColor = "#3ADE3C";
			stateText = "대기중";
		} else if (room.state === "playing") {
			borderColor = "#484F5E";
			stateText = "게임중";
		} else if ((room.state === "ready" && full) || room.state === "full") {
			borderColor = "#DE3A86";
			stateText = "인원초과";
		}

		roomElement.style.border = `2px solid ${borderColor}`;

		roomElement.innerHTML = `
            <div>
                <p>방번호: ${room.room_number}</p>
                <p>${room.room_title}</p>
            </div>
            <div>
                <div>
                    <div>방 상태: ${stateText} </div>
                    <div>${room.is_private ? "비번방" : "공개"}</div>
                </div>
                <div>
                    <div>인원수: ${room.current_players} / ${room.max_players}</div>
                    <div>icon2</div>
                </div>
            </div>
        `;
		gameRoomContainer.appendChild(roomElement);
	});
};

// 페이지 네이션
const changePage = (direction) => {
	if (direction === -1 && currentPage <= 1) {
		alert("첫 페이지입니다.");
		return;
	}

	if (direction === 1 && currentPage >= pageableCount) {
		alert("마지막 페이지입니다.");
		return;
	}

	currentPage += direction;
	fetchRoomList(currentPage);
};

document.getElementById("prevPageBtn").addEventListener("click", () => changePage(-1));
document.getElementById("nextPageBtn").addEventListener("click", () => changePage(1));
