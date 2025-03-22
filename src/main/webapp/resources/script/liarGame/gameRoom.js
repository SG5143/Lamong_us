document.addEventListener("DOMContentLoaded", function() {
	
	document.getElementById("exit-btn").addEventListener("click", function(){
		const exitModal = document.getElementById("exit-modal");
		const cancel = document.getElementById("exit-cancel");
		const exit = document.getElementById("exit-button");
		
		exitModal.style.display = "flex";
		
		cancel.addEventListener("click", function(){
			exitModal.style.display = "none";
		})
		
		exit.addEventListener("click", function(){
			exitModal.style.display = "none";
			window.location.href = "/lobby";
		})
	});
	
})